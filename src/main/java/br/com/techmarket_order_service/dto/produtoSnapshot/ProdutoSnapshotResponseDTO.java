package br.com.techmarket_order_service.dto.produtoSnapshot;

import br.com.techmarket_order_service.model.enums.StatusProduto;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record ProdutoSnapshotResponseDTO(
        Long id,
        @JsonProperty("id_mongo")
        String idMongo,
        String codigo,
        String nome,
        @JsonProperty("preco_unitario")
        BigDecimal precoUnitario,
        Integer estoque,
        StatusProduto status
) {
}
