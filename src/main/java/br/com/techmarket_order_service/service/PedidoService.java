package br.com.techmarket_order_service.service;

import br.com.techmarket_order_service.repository.ItemPedidoRepository;
import br.com.techmarket_order_service.repository.PedidoRepository;
import br.com.techmarket_order_service.repository.ProdutoSnapshotRepository;
import org.springframework.stereotype.Service;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;

    private final ItemPedidoRepository itemPedidoRepository;

    private final ProdutoSnapshotRepository produtoSnapshotRepository;

    public PedidoService(PedidoRepository pedidoRepository, ItemPedidoRepository itemPedidoRepository, ProdutoSnapshotRepository produtoSnapshotRepository) {
        this.pedidoRepository = pedidoRepository;
        this.itemPedidoRepository = itemPedidoRepository;
        this.produtoSnapshotRepository = produtoSnapshotRepository;
    }
}
