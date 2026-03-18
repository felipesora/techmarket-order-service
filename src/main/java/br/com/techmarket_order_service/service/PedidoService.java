package br.com.techmarket_order_service.service;

import br.com.techmarket_order_service.dto.itemPedido.ItemPedidoCreateDTO;
import br.com.techmarket_order_service.dto.pedido.PedidoCreateDTO;
import br.com.techmarket_order_service.dto.pedidoEvent.PedidoCanceladoEventDTO;
import br.com.techmarket_order_service.dto.pedidoEvent.PedidoCriadoEventDTO;
import br.com.techmarket_order_service.dto.pedido.PedidoResponseDTO;
import br.com.techmarket_order_service.dto.pedido.PedidoStatusUpdateDTO;
import br.com.techmarket_order_service.exception.RegraNegocioException;
import br.com.techmarket_order_service.mapper.ItemPedidoMapper;
import br.com.techmarket_order_service.mapper.PedidoEventMapper;
import br.com.techmarket_order_service.mapper.PedidoMapper;
import br.com.techmarket_order_service.model.ItemPedido;
import br.com.techmarket_order_service.model.Pedido;
import br.com.techmarket_order_service.model.ProdutoSnapshot;
import br.com.techmarket_order_service.model.enums.StatusPedido;
import br.com.techmarket_order_service.repository.PedidoRepository;
import br.com.techmarket_order_service.repository.ProdutoSnapshotRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;

    private final ProdutoSnapshotRepository produtoSnapshotRepository;

    private final RabbitTemplate rabbitTemplate;

    public PedidoService(PedidoRepository pedidoRepository, ProdutoSnapshotRepository produtoSnapshotRepository, RabbitTemplate rabbitTemplate) {
        this.pedidoRepository = pedidoRepository;
        this.produtoSnapshotRepository = produtoSnapshotRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    public Page<PedidoResponseDTO> obterTodosPedidos(Pageable paginacao) {
        return pedidoRepository
                .findAll(paginacao)
                .map(PedidoMapper::toResponseDTO);
    }

    public PedidoResponseDTO buscarPedidoPorId(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido com id: " + id + " não encontrado"));

        return PedidoMapper.toResponseDTO(pedido);
    }

    public Page<PedidoResponseDTO> buscarPorUsuario(Long usuarioId, Pageable paginacao) {
        Page<Pedido> pedidos = pedidoRepository.findByIdUsuario(usuarioId, paginacao);
        return pedidos.map(PedidoMapper::toResponseDTO);
    }

    @Transactional
    public PedidoResponseDTO cadastrarPedido(PedidoCreateDTO pedidoDTO) {

        Pedido pedido = PedidoMapper.toEntity(pedidoDTO);

        BigDecimal valorTotalPedido = BigDecimal.ZERO;

        List<ItemPedido> itensDoPedido = new ArrayList<>();

        for (ItemPedidoCreateDTO itemDTO : pedidoDTO.itens()) {

            ProdutoSnapshot produto = produtoSnapshotRepository
                    .findByIdMongo(itemDTO.idMongo())
                    .orElseThrow(() -> new EntityNotFoundException("Produto com idMongo: " + itemDTO.idMongo() + " não encontrado"));

            validarProduto(produto, itemDTO);

            ItemPedido itemPedido = ItemPedidoMapper.toEntity(itemDTO, pedido, produto);

            valorTotalPedido = valorTotalPedido.add(itemPedido.getSubtotal());

            itensDoPedido.add(itemPedido);
        }

        pedido.setValorTotal(valorTotalPedido);
        pedido.setItens(itensDoPedido);

        var salvo = pedidoRepository.save(pedido);

        PedidoCriadoEventDTO evento = PedidoEventMapper.toPedidoCriadoEvent(salvo);

        System.out.println("Enviando pedido cadastrado: " + evento);
        rabbitTemplate.convertAndSend("pedido.exchange", "pedido.criado", evento);

        return PedidoMapper.toResponseDTO(salvo);
    }

    @Transactional
    public PedidoResponseDTO atualizarStatus(Long id, PedidoStatusUpdateDTO dto) {

        if (dto.statusPedido().name().equals("CANCELADO")) {
            throw new RegraNegocioException("Use o endpoint específico para cancelamento de pedido");
        }

        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido com id: " + id + " não encontrado"));

        if (pedido.getStatusPedido() == StatusPedido.CANCELADO) {
            throw new RegraNegocioException("Pedido já cancelado não pode ter o status alterado");
        }

        pedido.setStatusPedido(dto.statusPedido());

        pedidoRepository.save(pedido);

        return PedidoMapper.toResponseDTO(pedido);
    }

    @Transactional
    public PedidoResponseDTO cancelarPedido(Long id) {

        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido com id: " + id + " não encontrado"));

        if (pedido.getStatusPedido() == StatusPedido.ENTREGUE) {
            throw new RegraNegocioException("Pedido finalizado não pode ser cancelado");
        }

        if (pedido.getStatusPedido() == StatusPedido.CANCELADO) {
            throw new RegraNegocioException("Pedido já está cancelado");
        }

        pedido.setStatusPedido(StatusPedido.CANCELADO);

        var salvo = pedidoRepository.save(pedido);

        PedidoCanceladoEventDTO evento = PedidoEventMapper.toPedidoCanceladoEvent(salvo);

        System.out.println("Enviando pedido cancelado: " + evento);
        rabbitTemplate.convertAndSend("pedido.exchange", "pedido.cancelado", evento);

        return PedidoMapper.toResponseDTO(pedido);
    }

    private void validarProduto(ProdutoSnapshot produto, ItemPedidoCreateDTO itemDTO) {

        if (!produto.getStatus().name().equals("ATIVO")) {
            throw new RegraNegocioException(
                    "Produto " + produto.getNome() + " não está ativo"
            );
        }

        if (itemDTO.quantidade() > produto.getEstoque()) {
            throw new RegraNegocioException(
                    "Estoque insuficiente para o produto " + produto.getNome() +
                            ". Disponível: " + produto.getEstoque() +
                            ", solicitado: " + itemDTO.quantidade()
            );
        }
    }
}
