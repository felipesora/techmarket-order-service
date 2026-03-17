package br.com.techmarket_order_service.mapper;

import br.com.techmarket_order_service.dto.pedido.PedidoCreateDTO;
import br.com.techmarket_order_service.dto.pedido.PedidoResponseDTO;
import br.com.techmarket_order_service.model.Pedido;
import br.com.techmarket_order_service.model.enums.StatusPedido;

import java.time.LocalDateTime;

public final class PedidoMapper {

    private PedidoMapper() {}

    public static Pedido toEntity(PedidoCreateDTO dto) {

        Pedido pedido = new Pedido();

        pedido.setIdUsuario(dto.idUsuario());
        pedido.setMetodoPagamento(dto.metodoPagamento());
        pedido.setStatusPedido(StatusPedido.CRIADO);
        pedido.setDataCriacao(LocalDateTime.now());

        return pedido;
    }

    public static PedidoResponseDTO toResponseDTO(Pedido pedido) {

        return new PedidoResponseDTO(
                pedido.getId(),
                pedido.getIdUsuario(),
                pedido.getValorTotal(),
                pedido.getDataCriacao(),
                pedido.getMetodoPagamento(),
                pedido.getStatusPedido(),
                pedido.getItens()
                        .stream()
                        .map(ItemPedidoMapper::toResponseDTO)
                        .toList()
        );
    }
}
