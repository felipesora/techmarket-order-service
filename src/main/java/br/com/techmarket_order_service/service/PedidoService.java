package br.com.techmarket_order_service.service;

import br.com.techmarket_order_service.dto.itemPedido.ItemPedidoCreateDTO;
import br.com.techmarket_order_service.dto.pedido.PedidoCreateDTO;
import br.com.techmarket_order_service.dto.pedido.PedidoResponseDTO;
import br.com.techmarket_order_service.dto.pedido.PedidoStatusUpdateDTO;
import br.com.techmarket_order_service.mapper.ItemPedidoMapper;
import br.com.techmarket_order_service.mapper.PedidoMapper;
import br.com.techmarket_order_service.model.ItemPedido;
import br.com.techmarket_order_service.model.Pedido;
import br.com.techmarket_order_service.model.ProdutoSnapshot;
import br.com.techmarket_order_service.repository.ItemPedidoRepository;
import br.com.techmarket_order_service.repository.PedidoRepository;
import br.com.techmarket_order_service.repository.ProdutoSnapshotRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
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

    public PedidoService(PedidoRepository pedidoRepository, ProdutoSnapshotRepository produtoSnapshotRepository) {
        this.pedidoRepository = pedidoRepository;
        this.produtoSnapshotRepository = produtoSnapshotRepository;
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

            ItemPedido itemPedido = ItemPedidoMapper.toEntity(itemDTO, pedido, produto);

            valorTotalPedido = valorTotalPedido.add(itemPedido.getSubtotal());

            itensDoPedido.add(itemPedido);
        }

        pedido.setValorTotal(valorTotalPedido);
        pedido.setItens(itensDoPedido);

        var salvo = pedidoRepository.save(pedido);

        return PedidoMapper.toResponseDTO(salvo);
    }

    public PedidoResponseDTO atualizarStatus(Long id, PedidoStatusUpdateDTO dto) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido com id: " + id + " não encontrado"));

        pedido.setStatusPedido(dto.statusPedido());

        pedidoRepository.save(pedido);

        return PedidoMapper.toResponseDTO(pedido);
    }
}
