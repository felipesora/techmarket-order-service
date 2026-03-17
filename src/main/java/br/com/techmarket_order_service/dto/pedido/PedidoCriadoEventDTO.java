package br.com.techmarket_order_service.dto.pedido;

import br.com.techmarket_order_service.dto.itemPedido.ItemPedidoEventDTO;

import java.util.List;

public record PedidoCriadoEventDTO(
        Long pedidoId,
        List<ItemPedidoEventDTO> itens
) {
}
