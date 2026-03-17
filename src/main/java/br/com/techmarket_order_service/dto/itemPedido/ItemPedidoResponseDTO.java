package br.com.techmarket_order_service.dto.itemPedido;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record ItemPedidoResponseDTO (
        @JsonProperty("id_item_pedido")
        Long id,
        Integer quantidade,
        BigDecimal subtotal,
        ProdutoPedidoResponseDTO produto
) {
}
