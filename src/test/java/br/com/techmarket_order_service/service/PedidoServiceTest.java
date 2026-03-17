package br.com.techmarket_order_service.service;

import br.com.techmarket_order_service.dto.itemPedido.ItemPedidoCreateDTO;
import br.com.techmarket_order_service.dto.pedido.PedidoCreateDTO;
import br.com.techmarket_order_service.dto.pedido.PedidoResponseDTO;
import br.com.techmarket_order_service.dto.pedido.PedidoStatusUpdateDTO;
import br.com.techmarket_order_service.model.ItemPedido;
import br.com.techmarket_order_service.model.Pedido;
import br.com.techmarket_order_service.model.ProdutoSnapshot;
import br.com.techmarket_order_service.model.enums.MetodoPagamento;
import br.com.techmarket_order_service.model.enums.StatusPedido;
import br.com.techmarket_order_service.repository.PedidoRepository;
import br.com.techmarket_order_service.repository.ProdutoSnapshotRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    @InjectMocks
    private PedidoService pedidoService;

    @Captor
    private ArgumentCaptor<Pedido> pedidoCaptor;

    private Pedido pedido;
    private ProdutoSnapshot produtoSnapshot;
    private ItemPedido itemPedido;
    private final Long PEDIDO_ID = 1L;
    private final Long USUARIO_ID = 100L;
    private final String ID_MONGO_PRODUTO = "507f1f77bcf86cd799439011";

    @BeforeEach
    void setUp() {
        // Configuração do ProdutoSnapshot
        produtoSnapshot = new ProdutoSnapshot();
        produtoSnapshot.setId(1L);
        produtoSnapshot.setIdMongo(ID_MONGO_PRODUTO);
        produtoSnapshot.setCodigo("PROD001");
        produtoSnapshot.setNome("Produto Teste");
        produtoSnapshot.setPrecoUnitario(new BigDecimal("100.00"));

        // Configuração do ItemPedido
        itemPedido = new ItemPedido();
        itemPedido.setId(1L);
        itemPedido.setProduto(produtoSnapshot);
        itemPedido.setQuantidade(2);
        itemPedido.setPrecoUnitario(new BigDecimal("100.00"));
        itemPedido.setSubtotal(new BigDecimal("200.00"));

        // Configuração do Pedido com lista de itens inicializada
        pedido = new Pedido();
        pedido.setId(PEDIDO_ID);
        pedido.setIdUsuario(USUARIO_ID);
        pedido.setMetodoPagamento(MetodoPagamento.CARTAO_CREDITO);
        pedido.setStatusPedido(StatusPedido.CRIADO);
        pedido.setDataCriacao(LocalDateTime.now());
        pedido.setValorTotal(new BigDecimal("200.00"));

        List<ItemPedido> itens = new ArrayList<>();
        itens.add(itemPedido);
        pedido.setItens(itens);

        itemPedido.setPedido(pedido);
    }

    @Nested
    @DisplayName("Testes para obterTodosPedidos")
    class ObterTodosPedidosTests {

        @Test
        @DisplayName("Deve retornar página de pedidos quando existirem pedidos")
        void deveRetornarPaginaDePedidos() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            Page<Pedido> pedidosPage = new PageImpl<>(List.of(pedido), pageable, 1);

            when(pedidoRepository.findAll(pageable)).thenReturn(pedidosPage);

            // Act
            Page<PedidoResponseDTO> resultado = pedidoService.obterTodosPedidos(pageable);

            // Assert
            assertNotNull(resultado);
            assertEquals(1, resultado.getTotalElements());
            assertEquals(PEDIDO_ID, resultado.getContent().get(0).id());
            assertEquals(USUARIO_ID, resultado.getContent().get(0).idUsuario());

            verify(pedidoRepository, times(1)).findAll(pageable);
        }

        @Test
        @DisplayName("Deve retornar página vazia quando não existirem pedidos")
        void deveRetornarPaginaVazia() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            Page<Pedido> emptyPage = new PageImpl<>(List.of(), pageable, 0);

            when(pedidoRepository.findAll(pageable)).thenReturn(emptyPage);

            // Act
            Page<PedidoResponseDTO> resultado = pedidoService.obterTodosPedidos(pageable);

            // Assert
            assertNotNull(resultado);
            assertEquals(0, resultado.getTotalElements());
            assertTrue(resultado.getContent().isEmpty());

            verify(pedidoRepository, times(1)).findAll(pageable);
        }
    }

    @Nested
    @DisplayName("Testes para buscarPedidoPorId")
    class BuscarPedidoPorIdTests {

        @Test
        @DisplayName("Deve retornar pedido quando ID existir")
        void deveRetornarPedidoQuandoIdExistir() {
            // Arrange
            when(pedidoRepository.findById(PEDIDO_ID)).thenReturn(Optional.of(pedido));

            // Act
            PedidoResponseDTO resultado = pedidoService.buscarPedidoPorId(PEDIDO_ID);

            // Assert
            assertNotNull(resultado);
            assertEquals(PEDIDO_ID, resultado.id());
            assertEquals(USUARIO_ID, resultado.idUsuario());
            assertEquals(new BigDecimal("200.00"), resultado.valorTotal());
            assertEquals(1, resultado.itens().size());

            verify(pedidoRepository, times(1)).findById(PEDIDO_ID);
        }

        @Test
        @DisplayName("Deve lançar EntityNotFoundException quando ID não existir")
        void deveLancarExcecaoQuandoIdNaoExistir() {
            // Arrange
            Long idInexistente = 999L;
            when(pedidoRepository.findById(idInexistente)).thenReturn(Optional.empty());

            // Act & Assert
            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> pedidoService.buscarPedidoPorId(idInexistente)
            );

            assertEquals("Pedido com id: 999 não encontrado", exception.getMessage());
            verify(pedidoRepository, times(1)).findById(idInexistente);
        }
    }

    @Nested
    @DisplayName("Testes para buscarPorUsuario")
    class BuscarPorUsuarioTests {

        @Test
        @DisplayName("Deve retornar pedidos do usuário quando existirem")
        void deveRetornarPedidosDoUsuario() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            Page<Pedido> pedidosPage = new PageImpl<>(List.of(pedido), pageable, 1);

            when(pedidoRepository.findByIdUsuario(USUARIO_ID, pageable)).thenReturn(pedidosPage);

            // Act
            Page<PedidoResponseDTO> resultado = pedidoService.buscarPorUsuario(USUARIO_ID, pageable);

            // Assert
            assertNotNull(resultado);
            assertEquals(1, resultado.getTotalElements());
            assertEquals(USUARIO_ID, resultado.getContent().get(0).idUsuario());

            verify(pedidoRepository, times(1)).findByIdUsuario(USUARIO_ID, pageable);
        }

        @Test
        @DisplayName("Deve retornar página vazia quando usuário não tiver pedidos")
        void deveRetornarPaginaVaziaQuandoUsuarioSemPedidos() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            Page<Pedido> emptyPage = new PageImpl<>(List.of(), pageable, 0);

            when(pedidoRepository.findByIdUsuario(USUARIO_ID, pageable)).thenReturn(emptyPage);

            // Act
            Page<PedidoResponseDTO> resultado = pedidoService.buscarPorUsuario(USUARIO_ID, pageable);

            // Assert
            assertNotNull(resultado);
            assertEquals(0, resultado.getTotalElements());
            assertTrue(resultado.getContent().isEmpty());

            verify(pedidoRepository, times(1)).findByIdUsuario(USUARIO_ID, pageable);
        }
    }

    @Nested
    @DisplayName("Testes para cadastrarPedido")
    class CadastrarPedidoTests {

        private PedidoCreateDTO pedidoCreateDTO;
        private ItemPedidoCreateDTO itemPedidoCreateDTO;

        @BeforeEach
        void setUp() {
            itemPedidoCreateDTO = new ItemPedidoCreateDTO(
                    ID_MONGO_PRODUTO,
                    2
            );

            pedidoCreateDTO = new PedidoCreateDTO(
                    USUARIO_ID,
                    MetodoPagamento.CARTAO_CREDITO,
                    List.of(itemPedidoCreateDTO)
            );
        }

        @Test
        @DisplayName("Deve cadastrar pedido com sucesso")
        void deveCadastrarPedidoComSucesso() {
            // Arrange
            when(produtoSnapshotRepository.findByIdMongo(ID_MONGO_PRODUTO))
                    .thenReturn(Optional.of(produtoSnapshot));

            Pedido pedidoParaSalvar = new Pedido();
            pedidoParaSalvar.setId(PEDIDO_ID);
            pedidoParaSalvar.setIdUsuario(USUARIO_ID);
            pedidoParaSalvar.setMetodoPagamento(MetodoPagamento.CARTAO_CREDITO);
            pedidoParaSalvar.setStatusPedido(StatusPedido.CRIADO);
            pedidoParaSalvar.setDataCriacao(LocalDateTime.now());
            pedidoParaSalvar.setValorTotal(new BigDecimal("200.00"));

            List<ItemPedido> itens = new ArrayList<>();
            ItemPedido item = new ItemPedido();
            item.setId(1L);
            item.setProduto(produtoSnapshot);
            item.setQuantidade(2);
            item.setPrecoUnitario(new BigDecimal("100.00"));
            item.setSubtotal(new BigDecimal("200.00"));
            item.setPedido(pedidoParaSalvar);
            itens.add(item);
            pedidoParaSalvar.setItens(itens);

            when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoParaSalvar);

            // Act
            PedidoResponseDTO resultado = pedidoService.cadastrarPedido(pedidoCreateDTO);

            // Assert
            assertNotNull(resultado);
            assertEquals(PEDIDO_ID, resultado.id());
            assertEquals(USUARIO_ID, resultado.idUsuario());
            assertEquals(new BigDecimal("200.00"), resultado.valorTotal());
            assertEquals(StatusPedido.CRIADO, resultado.statusPedido());
            assertEquals(1, resultado.itens().size());

            verify(produtoSnapshotRepository, times(1)).findByIdMongo(ID_MONGO_PRODUTO);
            verify(pedidoRepository, times(1)).save(pedidoCaptor.capture());

            Pedido pedidoSalvo = pedidoCaptor.getValue();
            assertEquals(USUARIO_ID, pedidoSalvo.getIdUsuario());
            assertEquals(new BigDecimal("200.00"), pedidoSalvo.getValorTotal());
            assertEquals(StatusPedido.CRIADO, pedidoSalvo.getStatusPedido());
            assertNotNull(pedidoSalvo.getItens());
            assertEquals(1, pedidoSalvo.getItens().size());
        }

        @Test
        @DisplayName("Deve calcular valor total corretamente com múltiplos itens")
        void deveCalcularValorTotalCorretamente() {
            // Arrange
            ItemPedidoCreateDTO segundoItemDTO = new ItemPedidoCreateDTO(
                    "507f1f77bcf86cd799439012",
                    3
            );

            ProdutoSnapshot segundoProduto = new ProdutoSnapshot();
            segundoProduto.setId(2L);
            segundoProduto.setIdMongo("507f1f77bcf86cd799439012");
            segundoProduto.setCodigo("PROD002");
            segundoProduto.setNome("Produto Teste 2");
            segundoProduto.setPrecoUnitario(new BigDecimal("50.00"));

            PedidoCreateDTO pedidoComMultiplosItens = new PedidoCreateDTO(
                    USUARIO_ID,
                    MetodoPagamento.CARTAO_CREDITO,
                    List.of(itemPedidoCreateDTO, segundoItemDTO)
            );

            when(produtoSnapshotRepository.findByIdMongo(ID_MONGO_PRODUTO))
                    .thenReturn(Optional.of(produtoSnapshot));
            when(produtoSnapshotRepository.findByIdMongo("507f1f77bcf86cd799439012"))
                    .thenReturn(Optional.of(segundoProduto));

            // Criar o pedido que será retornado pelo save com os itens já configurados
            Pedido pedidoSalvo = new Pedido();
            pedidoSalvo.setId(PEDIDO_ID);
            pedidoSalvo.setIdUsuario(USUARIO_ID);
            pedidoSalvo.setMetodoPagamento(MetodoPagamento.CARTAO_CREDITO);
            pedidoSalvo.setStatusPedido(StatusPedido.CRIADO);
            pedidoSalvo.setDataCriacao(LocalDateTime.now());
            pedidoSalvo.setValorTotal(new BigDecimal("350.00")); // 200 + 150

            List<ItemPedido> itensSalvos = new ArrayList<>();

            ItemPedido item1 = new ItemPedido();
            item1.setId(1L);
            item1.setProduto(produtoSnapshot);
            item1.setQuantidade(2);
            item1.setPrecoUnitario(new BigDecimal("100.00"));
            item1.setSubtotal(new BigDecimal("200.00"));
            item1.setPedido(pedidoSalvo);
            itensSalvos.add(item1);

            ItemPedido item2 = new ItemPedido();
            item2.setId(2L);
            item2.setProduto(segundoProduto);
            item2.setQuantidade(3);
            item2.setPrecoUnitario(new BigDecimal("50.00"));
            item2.setSubtotal(new BigDecimal("150.00"));
            item2.setPedido(pedidoSalvo);
            itensSalvos.add(item2);

            pedidoSalvo.setItens(itensSalvos);

            when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoSalvo);

            // Act
            PedidoResponseDTO resultado = pedidoService.cadastrarPedido(pedidoComMultiplosItens);

            // Assert
            assertNotNull(resultado);
            assertEquals(new BigDecimal("350.00"), resultado.valorTotal());
            assertEquals(2, resultado.itens().size());

            verify(pedidoRepository, times(1)).save(pedidoCaptor.capture());
            Pedido pedidoAntesDeSalvar = pedidoCaptor.getValue();
            assertEquals(new BigDecimal("350.00"), pedidoAntesDeSalvar.getValorTotal());
            assertNotNull(pedidoAntesDeSalvar.getItens());
            assertEquals(2, pedidoAntesDeSalvar.getItens().size());
        }

        @Test
        @DisplayName("Deve lançar EntityNotFoundException quando produto não existir")
        void deveLancarExcecaoQuandoProdutoNaoExistir() {
            // Arrange
            when(produtoSnapshotRepository.findByIdMongo(ID_MONGO_PRODUTO))
                    .thenReturn(Optional.empty());

            // Act & Assert
            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> pedidoService.cadastrarPedido(pedidoCreateDTO)
            );

            assertTrue(exception.getMessage().contains("Produto com idMongo: " + ID_MONGO_PRODUTO));
            verify(produtoSnapshotRepository, times(1)).findByIdMongo(ID_MONGO_PRODUTO);
            verify(pedidoRepository, never()).save(any(Pedido.class));
        }

        @Test
        @DisplayName("Deve lançar EntityNotFoundException quando um dos produtos não existir")
        void deveLancarExcecaoQuandoUmDosProdutosNaoExistir() {
            // Arrange
            ItemPedidoCreateDTO itemInvalidoDTO = new ItemPedidoCreateDTO(
                    "idInvalido",
                    1
            );

            PedidoCreateDTO pedidoComItemInvalido = new PedidoCreateDTO(
                    USUARIO_ID,
                    MetodoPagamento.CARTAO_CREDITO,
                    List.of(itemPedidoCreateDTO, itemInvalidoDTO)
            );

            when(produtoSnapshotRepository.findByIdMongo(ID_MONGO_PRODUTO))
                    .thenReturn(Optional.of(produtoSnapshot));
            when(produtoSnapshotRepository.findByIdMongo("idInvalido"))
                    .thenReturn(Optional.empty());

            // Act & Assert
            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> pedidoService.cadastrarPedido(pedidoComItemInvalido)
            );

            assertTrue(exception.getMessage().contains("Produto com idMongo: idInvalido"));
            verify(produtoSnapshotRepository, times(1)).findByIdMongo(ID_MONGO_PRODUTO);
            verify(produtoSnapshotRepository, times(1)).findByIdMongo("idInvalido");
            verify(pedidoRepository, never()).save(any(Pedido.class));
        }
    }

    @Nested
    @DisplayName("Testes para atualizarStatus")
    class AtualizarStatusTests {

        private PedidoStatusUpdateDTO statusUpdateDTO;

        @BeforeEach
        void setUp() {
            statusUpdateDTO = new PedidoStatusUpdateDTO(StatusPedido.PAGAMENTO_APROVADO);
        }

        @Test
        @DisplayName("Deve atualizar status do pedido com sucesso")
        void deveAtualizarStatusComSucesso() {
            // Arrange
            when(pedidoRepository.findById(PEDIDO_ID)).thenReturn(Optional.of(pedido));
            when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

            // Act
            PedidoResponseDTO resultado = pedidoService.atualizarStatus(PEDIDO_ID, statusUpdateDTO);

            // Assert
            assertNotNull(resultado);
            assertEquals(PEDIDO_ID, resultado.id());
            assertEquals(StatusPedido.PAGAMENTO_APROVADO, resultado.statusPedido());

            verify(pedidoRepository, times(1)).findById(PEDIDO_ID);
            verify(pedidoRepository, times(1)).save(pedidoCaptor.capture());

            Pedido pedidoAtualizado = pedidoCaptor.getValue();
            assertEquals(StatusPedido.PAGAMENTO_APROVADO, pedidoAtualizado.getStatusPedido());
        }

        @Test
        @DisplayName("Deve lançar EntityNotFoundException quando pedido não existir")
        void deveLancarExcecaoQuandoPedidoNaoExistir() {
            // Arrange
            Long idInexistente = 999L;
            when(pedidoRepository.findById(idInexistente)).thenReturn(Optional.empty());

            // Act & Assert
            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> pedidoService.atualizarStatus(idInexistente, statusUpdateDTO)
            );

            assertTrue(exception.getMessage().contains("Pedido com id: " + idInexistente));
            verify(pedidoRepository, times(1)).findById(idInexistente);
            verify(pedidoRepository, never()).save(any(Pedido.class));
        }

        @Test
        @DisplayName("Deve atualizar para diferentes status")
        void deveAtualizarParaDiferentesStatus() {
            // Arrange
            StatusPedido[] statusList = StatusPedido.values();

            // Para cada status, criar um teste separado ou usar lenient()
            for (StatusPedido status : statusList) {
                // Criar um novo pedido para cada iteração para evitar referências compartilhadas
                Pedido pedidoParaTeste = new Pedido();
                pedidoParaTeste.setId(PEDIDO_ID);
                pedidoParaTeste.setIdUsuario(USUARIO_ID);
                pedidoParaTeste.setMetodoPagamento(MetodoPagamento.CARTAO_CREDITO);
                pedidoParaTeste.setStatusPedido(StatusPedido.CRIADO); // Status inicial
                pedidoParaTeste.setDataCriacao(LocalDateTime.now());
                pedidoParaTeste.setValorTotal(new BigDecimal("200.00"));

                List<ItemPedido> itens = new ArrayList<>();
                ItemPedido item = new ItemPedido();
                item.setId(1L);
                item.setProduto(produtoSnapshot);
                item.setQuantidade(2);
                item.setPrecoUnitario(new BigDecimal("100.00"));
                item.setSubtotal(new BigDecimal("200.00"));
                item.setPedido(pedidoParaTeste);
                itens.add(item);
                pedidoParaTeste.setItens(itens);

                PedidoStatusUpdateDTO dto = new PedidoStatusUpdateDTO(status);

                when(pedidoRepository.findById(PEDIDO_ID)).thenReturn(Optional.of(pedidoParaTeste));

                Pedido pedidoAtualizado = new Pedido();
                pedidoAtualizado.setId(PEDIDO_ID);
                pedidoAtualizado.setIdUsuario(USUARIO_ID);
                pedidoAtualizado.setMetodoPagamento(MetodoPagamento.CARTAO_CREDITO);
                pedidoAtualizado.setStatusPedido(status); // Status atualizado
                pedidoAtualizado.setDataCriacao(LocalDateTime.now());
                pedidoAtualizado.setValorTotal(new BigDecimal("200.00"));
                pedidoAtualizado.setItens(itens);

                when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoAtualizado);

                // Act
                PedidoResponseDTO resultado = pedidoService.atualizarStatus(PEDIDO_ID, dto);

                // Assert
                assertEquals(status, resultado.statusPedido());

                verify(pedidoRepository, times(1)).save(pedidoCaptor.capture());
                assertEquals(status, pedidoCaptor.getValue().getStatusPedido());

                // Reset para próxima iteração (apenas os mocks, não o captor)
                reset(pedidoRepository);
            }
        }

        @Test
        @DisplayName("Deve atualizar status de CRIADO para PAGO")
        void deveAtualizarDeCriadoParaPago() {
            // Arrange
            Pedido pedidoCriado = new Pedido();
            pedidoCriado.setId(PEDIDO_ID);
            pedidoCriado.setIdUsuario(USUARIO_ID);
            pedidoCriado.setMetodoPagamento(MetodoPagamento.CARTAO_CREDITO);
            pedidoCriado.setStatusPedido(StatusPedido.CRIADO);
            pedidoCriado.setDataCriacao(LocalDateTime.now());
            pedidoCriado.setValorTotal(new BigDecimal("200.00"));

            List<ItemPedido> itens = new ArrayList<>();
            ItemPedido item = new ItemPedido();
            item.setId(1L);
            item.setProduto(produtoSnapshot);
            item.setQuantidade(2);
            item.setPrecoUnitario(new BigDecimal("100.00"));
            item.setSubtotal(new BigDecimal("200.00"));
            item.setPedido(pedidoCriado);
            itens.add(item);
            pedidoCriado.setItens(itens);

            Pedido pedidoPago = new Pedido();
            pedidoPago.setId(PEDIDO_ID);
            pedidoPago.setIdUsuario(USUARIO_ID);
            pedidoPago.setMetodoPagamento(MetodoPagamento.CARTAO_CREDITO);
            pedidoPago.setStatusPedido(StatusPedido.PAGAMENTO_APROVADO);
            pedidoPago.setDataCriacao(LocalDateTime.now());
            pedidoPago.setValorTotal(new BigDecimal("200.00"));
            pedidoPago.setItens(itens);

            PedidoStatusUpdateDTO dto = new PedidoStatusUpdateDTO(StatusPedido.PAGAMENTO_APROVADO);

            when(pedidoRepository.findById(PEDIDO_ID)).thenReturn(Optional.of(pedidoCriado));
            when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoPago);

            // Act
            PedidoResponseDTO resultado = pedidoService.atualizarStatus(PEDIDO_ID, dto);

            // Assert
            assertEquals(StatusPedido.PAGAMENTO_APROVADO, resultado.statusPedido());
            verify(pedidoRepository, times(1)).findById(PEDIDO_ID);
            verify(pedidoRepository, times(1)).save(any(Pedido.class));
        }

        @Test
        @DisplayName("Deve atualizar status de PAGO para ENVIADO")
        void deveAtualizarDePagoParaEnviado() {
            // Arrange
            Pedido pedidoPago = new Pedido();
            pedidoPago.setId(PEDIDO_ID);
            pedidoPago.setIdUsuario(USUARIO_ID);
            pedidoPago.setMetodoPagamento(MetodoPagamento.CARTAO_CREDITO);
            pedidoPago.setStatusPedido(StatusPedido.PAGAMENTO_APROVADO);
            pedidoPago.setDataCriacao(LocalDateTime.now());
            pedidoPago.setValorTotal(new BigDecimal("200.00"));

            List<ItemPedido> itens = new ArrayList<>();
            ItemPedido item = new ItemPedido();
            item.setId(1L);
            item.setProduto(produtoSnapshot);
            item.setQuantidade(2);
            item.setPrecoUnitario(new BigDecimal("100.00"));
            item.setSubtotal(new BigDecimal("200.00"));
            item.setPedido(pedidoPago);
            itens.add(item);
            pedidoPago.setItens(itens);

            Pedido pedidoEnviado = new Pedido();
            pedidoEnviado.setId(PEDIDO_ID);
            pedidoEnviado.setIdUsuario(USUARIO_ID);
            pedidoEnviado.setMetodoPagamento(MetodoPagamento.CARTAO_CREDITO);
            pedidoEnviado.setStatusPedido(StatusPedido.ENVIADO);
            pedidoEnviado.setDataCriacao(LocalDateTime.now());
            pedidoEnviado.setValorTotal(new BigDecimal("200.00"));
            pedidoEnviado.setItens(itens);

            PedidoStatusUpdateDTO dto = new PedidoStatusUpdateDTO(StatusPedido.ENVIADO);

            when(pedidoRepository.findById(PEDIDO_ID)).thenReturn(Optional.of(pedidoPago));
            when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoEnviado);

            // Act
            PedidoResponseDTO resultado = pedidoService.atualizarStatus(PEDIDO_ID, dto);

            // Assert
            assertEquals(StatusPedido.ENVIADO, resultado.statusPedido());
            verify(pedidoRepository, times(1)).findById(PEDIDO_ID);
            verify(pedidoRepository, times(1)).save(any(Pedido.class));
        }
    }
}