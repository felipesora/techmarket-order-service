package br.com.techmarket_order_service.dto.pedido;

import br.com.techmarket_order_service.dto.itemPedido.ItemPedidoCreateDTO;
import br.com.techmarket_order_service.model.enums.MetodoPagamento;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record PedidoCreateDTO(
        @NotNull(message = "O id usuario é obrigatório")
        @JsonProperty("id_usuario")
        Long idUsuario,

        @NotNull(message = "O método de pagamento obrigatório")
        @JsonProperty("metodo_pagamento")
        MetodoPagamento metodoPagamento,

        @NotNull(message = "A lista de produtos é obrigatória")
        List<ItemPedidoCreateDTO> itens
) {
}
