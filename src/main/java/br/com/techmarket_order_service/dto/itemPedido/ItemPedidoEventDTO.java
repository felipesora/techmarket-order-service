package br.com.techmarket_order_service.dto.itemPedido;

public record ItemPedidoEventDTO(
        String produtoIdMongo,
        Integer quantidade
) {
}
