package br.com.techmarket_order_service.dto.pedidoEvent;

public record ItemPedidoEventDTO(
        String produtoIdMongo,
        Integer quantidade
) {
}
