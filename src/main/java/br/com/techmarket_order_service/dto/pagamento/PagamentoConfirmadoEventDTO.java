package br.com.techmarket_order_service.dto.pagamento;

import br.com.techmarket_order_service.model.enums.MetodoPagamento;
import br.com.techmarket_order_service.model.enums.StatusPagamento;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PagamentoConfirmadoEventDTO(
        @JsonProperty("id_pagamento")
        Long id,
        @JsonProperty("id_pedido")
        Long idPedido,
        @JsonProperty("valor_total")
        BigDecimal valorTotal,
        @JsonProperty("metodo_pagamento")
        MetodoPagamento metodoPagamento,
        @JsonProperty("status_pagamento")
        StatusPagamento statusPagamento,
        @JsonProperty("data_criacao")
        LocalDateTime dataCriacao
) {
}
