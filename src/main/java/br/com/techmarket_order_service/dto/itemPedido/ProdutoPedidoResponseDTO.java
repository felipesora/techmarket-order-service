package br.com.techmarket_order_service.dto.itemPedido;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record ProdutoPedidoResponseDTO(
        @JsonProperty("id_produto")
        Long id,
        @JsonProperty("id_mongo_produto")
        String idMongo,
        String codigo,
        String nome,
        @JsonProperty("preco_produto")
        BigDecimal precoProduto
) {
}
