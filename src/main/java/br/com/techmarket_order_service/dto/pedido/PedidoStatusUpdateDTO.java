package br.com.techmarket_order_service.dto.pedido;

import br.com.techmarket_order_service.model.enums.StatusPedido;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record PedidoStatusUpdateDTO(
        @NotNull(message = "O status do pedido é obrigatório")
        @JsonProperty("status_pedido")
        StatusPedido statusPedido
) {
}
