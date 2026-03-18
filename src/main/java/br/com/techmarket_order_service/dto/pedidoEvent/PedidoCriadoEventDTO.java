package br.com.techmarket_order_service.dto.pedidoEvent;

import java.util.List;

public record PedidoCriadoEventDTO(
        Long pedidoId,
        List<ItemPedidoEventDTO> itens
) {
}
