package br.com.techmarket_order_service.dto.itemPedido;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ItemPedidoCreateDTO(
        @NotBlank(message = "O id mongodb do produto é obrigatório")
        @Size(min = 24, max = 24, message = "O id mongodb deve ter 24 caracteres")
        @JsonProperty("id_mongo")
        String idMongo,

        @NotNull(message = "A quantidade é obrigatória")
        @Positive(message = "A quantidade deve ser maior que zero")
        Integer quantidade
) {
}
