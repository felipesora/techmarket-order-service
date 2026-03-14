package br.com.techmarket_order_service.dto.produtoSnapshot;

import br.com.techmarket_order_service.model.enums.StatusProduto;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProdutoSnapshotUpdateDTO (
        @NotBlank(message = "O id mongodb do produto é obrigatório")
        @Size(min = 24, max = 24, message = "O id mongodb deve ter 24 caracteres")
        @JsonProperty("id_mongo")
        String idMongo,

        @NotBlank(message = "O código do produto é obrigatório")
        @Size(min = 3, max = 50, message = "O código deve ter entre 3 e 50 caracteres")
        String codigo,

        @NotBlank(message = "O nome do produto é obrigatório")
        @Size(min = 3, max = 150, message = "O nome deve ter entre 3 e 150 caracteres")
        String nome,

        @NotNull(message = "O preço é obrigatório")
        @Positive(message = "O preço deve ser maior que zero")
        @JsonProperty("preco_unitario")
        BigDecimal precoUnitario,

        @NotNull(message = "O estoque é obrigatório")
        @PositiveOrZero(message = "O estoque não pode ser negativo")
        Integer estoque,

        @NotNull(message = "O status do produto é obrigatório")
        StatusProduto status
){
}
