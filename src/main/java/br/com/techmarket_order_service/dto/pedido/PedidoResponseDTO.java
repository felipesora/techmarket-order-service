package br.com.techmarket_order_service.dto.pedido;

import br.com.techmarket_order_service.dto.itemPedido.ItemPedidoResponseDTO;
import br.com.techmarket_order_service.model.enums.MetodoPagamento;
import br.com.techmarket_order_service.model.enums.StatusPedido;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PedidoResponseDTO(
        @JsonProperty("id_pedido")
        Long id,
        @JsonProperty("id_usuario")
        Long idUsuario,
        @JsonProperty("valor_total")
        BigDecimal valorTotal,
        @JsonProperty("data_criacao")
        LocalDateTime dataCriacao,
        @JsonProperty("metodo_pagamento")
        MetodoPagamento metodoPagamento,
        @JsonProperty("status_pedido")
        StatusPedido statusPedido,
        List<ItemPedidoResponseDTO> itens
) {
}
