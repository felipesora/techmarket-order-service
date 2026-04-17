package br.com.techmarket_order_service.mapper;

import br.com.techmarket_order_service.dto.itemPedido.ItemPedidoCreateDTO;
import br.com.techmarket_order_service.dto.itemPedido.ItemPedidoResponseDTO;
import br.com.techmarket_order_service.dto.itemPedido.ProdutoPedidoResponseDTO;
import br.com.techmarket_order_service.model.ItemPedido;
import br.com.techmarket_order_service.model.Pedido;
import br.com.techmarket_order_service.model.ProdutoSnapshot;

import java.math.BigDecimal;

public final class ItemPedidoMapper {

    private ItemPedidoMapper() {}

    public static ItemPedido toEntity(ItemPedidoCreateDTO dto, Pedido pedido, ProdutoSnapshot produto) {

        BigDecimal precoDoProduto = getPrecoDoProduto(produto);

        BigDecimal subtotal =
                precoDoProduto.multiply(BigDecimal.valueOf(dto.quantidade()));

        ItemPedido item = new ItemPedido();
        item.setPedido(pedido);
        item.setProduto(produto);
        item.setQuantidade(dto.quantidade());
        item.setPrecoUnitario(precoDoProduto);
        item.setSubtotal(subtotal);

        return item;
    }

    public static ItemPedidoResponseDTO toResponseDTO(ItemPedido item) {

        BigDecimal precoUnitario = item.getProduto().getPrecoUnitario();
        BigDecimal precoPromocional = item.getProduto().getPrecoPromocional();
        var precoProduto = precoPromocional != null ? precoPromocional : precoUnitario;

        return new ItemPedidoResponseDTO(
                item.getId(),
                item.getQuantidade(),
                item.getSubtotal(),
                new ProdutoPedidoResponseDTO(
                        item.getProduto().getId(),
                        item.getProduto().getIdMongo(),
                        item.getProduto().getCodigo(),
                        item.getProduto().getNome(),
                        precoProduto
                )
        );
    }

    private static BigDecimal getPrecoDoProduto(ProdutoSnapshot produto) {
        BigDecimal precoUnitario = produto.getPrecoUnitario();
        BigDecimal precoPromocional = produto.getPrecoPromocional();
        return precoPromocional != null ? precoPromocional : precoUnitario;
    }
}
