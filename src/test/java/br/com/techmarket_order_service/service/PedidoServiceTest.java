package br.com.techmarket_order_service.service;

import br.com.techmarket_order_service.dto.itemPedido.ItemPedidoCreateDTO;
import br.com.techmarket_order_service.dto.pedido.PedidoCreateDTO;
import br.com.techmarket_order_service.dto.pedido.PedidoStatusUpdateDTO;
import br.com.techmarket_order_service.exception.RegraNegocioException;
import br.com.techmarket_order_service.model.Pedido;
import br.com.techmarket_order_service.model.ProdutoSnapshot;
import br.com.techmarket_order_service.model.enums.MetodoPagamento;
import br.com.techmarket_order_service.model.enums.StatusPedido;
import br.com.techmarket_order_service.model.enums.StatusProduto;
import br.com.techmarket_order_service.repository.PedidoRepository;
import br.com.techmarket_order_service.repository.ProdutoSnapshotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ProdutoSnapshotRepository produtoSnapshotRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private PedidoService pedidoService;

    private ProdutoSnapshot produto;

    @BeforeEach
    void setup() {
        produto = new ProdutoSnapshot();
        produto.setIdMongo("abc123");
        produto.setNome("Produto Teste");
        produto.setEstoque(10);
        produto.setPrecoUnitario(BigDecimal.valueOf(100));
        produto.setStatus(StatusProduto.ATIVO);
    }

    @Test
    void deveCadastrarPedidoComSucesso() {
        ItemPedidoCreateDTO itemDTO = new ItemPedidoCreateDTO("abc123", 2);

        PedidoCreateDTO pedidoDTO = new PedidoCreateDTO(
                1L,
                MetodoPagamento.CARTAO_CREDITO,
                List.of(itemDTO)
        );

        when(produtoSnapshotRepository.findByIdMongo("abc123"))
                .thenReturn(Optional.of(produto));

        when(pedidoRepository.save(any(Pedido.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = pedidoService.cadastrarPedido(pedidoDTO);

        assertNotNull(response);
        verify(pedidoRepository, times(1)).save(any(Pedido.class));
        verify(rabbitTemplate, times(1)).convertAndSend(
                        eq("pedido.exchange"),
                        eq("pedido.criado"),
                        any(Object.class));
    }

    @Test
    void deveLancarExcecaoQuandoProdutoNaoExiste() {
        ItemPedidoCreateDTO itemDTO = new ItemPedidoCreateDTO("abc123", 2);

        PedidoCreateDTO pedidoDTO = new PedidoCreateDTO(
                1L,
                MetodoPagamento.CARTAO_CREDITO,
                List.of(itemDTO)
        );

        when(produtoSnapshotRepository.findByIdMongo("abc123"))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                pedidoService.cadastrarPedido(pedidoDTO)
        );
    }

    @Test
    void deveLancarExcecaoQuandoEstoqueInsuficiente() {
        produto.setEstoque(1);

        ItemPedidoCreateDTO itemDTO = new ItemPedidoCreateDTO("abc123", 5);

        PedidoCreateDTO pedidoDTO = new PedidoCreateDTO(
                1L,
                MetodoPagamento.CARTAO_CREDITO,
                List.of(itemDTO)
        );

        when(produtoSnapshotRepository.findByIdMongo("abc123"))
                .thenReturn(Optional.of(produto));

        assertThrows(RegraNegocioException.class, () ->
                pedidoService.cadastrarPedido(pedidoDTO)
        );
    }

    @Test
    void deveAtualizarStatusComSucesso() {
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setStatusPedido(StatusPedido.CRIADO);
        pedido.setItens(new ArrayList<>());

        when(pedidoRepository.findById(1L))
                .thenReturn(Optional.of(pedido));

        PedidoStatusUpdateDTO dto = new PedidoStatusUpdateDTO(StatusPedido.PAGAMENTO_APROVADO);

        var response = pedidoService.atualizarStatus(1L, dto);

        assertEquals(StatusPedido.PAGAMENTO_APROVADO, pedido.getStatusPedido());
        verify(pedidoRepository).save(pedido);
    }

    @Test
    void naoDevePermitirAtualizarParaCancelado() {
        PedidoStatusUpdateDTO dto = new PedidoStatusUpdateDTO(StatusPedido.CANCELADO);

        assertThrows(RegraNegocioException.class, () ->
                pedidoService.atualizarStatus(1L, dto)
        );
    }

    @Test
    void deveCancelarPedidoComSucesso() {
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setStatusPedido(StatusPedido.CRIADO);
        pedido.setItens(new ArrayList<>());


        when(pedidoRepository.findById(1L))
                .thenReturn(Optional.of(pedido));

        when(pedidoRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = pedidoService.cancelarPedido(1L);

        assertEquals(StatusPedido.CANCELADO, pedido.getStatusPedido());
        verify(rabbitTemplate, times(1)).convertAndSend(
                eq("pedido.exchange"),
                eq("pedido.cancelado"),
                any(Object.class));
    }

    @Test
    void naoDeveCancelarPedidoEntregue() {
        Pedido pedido = new Pedido();
        pedido.setStatusPedido(StatusPedido.ENTREGUE);

        when(pedidoRepository.findById(1L))
                .thenReturn(Optional.of(pedido));

        assertThrows(RegraNegocioException.class, () ->
                pedidoService.cancelarPedido(1L)
        );
    }

    @Test
    void naoDeveCancelarPedidoJaCancelado() {
        Pedido pedido = new Pedido();
        pedido.setStatusPedido(StatusPedido.CANCELADO);

        when(pedidoRepository.findById(1L))
                .thenReturn(Optional.of(pedido));

        assertThrows(RegraNegocioException.class, () ->
                pedidoService.cancelarPedido(1L)
        );
    }
}