package br.com.techmarket_order_service.mapper;

import br.com.techmarket_order_service.dto.pedidoEvent.ItemPedidoEventDTO;
import br.com.techmarket_order_service.dto.pedidoEvent.PedidoCanceladoEventDTO;
import br.com.techmarket_order_service.dto.pedidoEvent.PedidoCriadoEventDTO;
import br.com.techmarket_order_service.model.Pedido;

import java.util.List;

public final class PedidoEventMapper {

    private PedidoEventMapper() {}

    public static PedidoCriadoEventDTO toPedidoCriadoEvent(Pedido pedido) {

        List<ItemPedidoEventDTO> itensEvento = pedido.getItens().stream()
                .map(item -> new ItemPedidoEventDTO(
                        item.getProduto().getIdMongo(),
                        item.getQuantidade()
                ))
                .toList();

        return new PedidoCriadoEventDTO(
                pedido.getId(),
                itensEvento
        );
    }

    public static PedidoCanceladoEventDTO toPedidoCanceladoEvent(Pedido pedido) {

        List<ItemPedidoEventDTO> itensEvento = pedido.getItens().stream()
                .map(item -> new ItemPedidoEventDTO(
                        item.getProduto().getIdMongo(),
                        item.getQuantidade()
                ))
                .toList();

        return new PedidoCanceladoEventDTO(
                pedido.getId(),
                itensEvento
        );
    }
}
