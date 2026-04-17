package br.com.techmarket_order_service.mapper;


import br.com.techmarket_order_service.dto.produtoSnapshot.ProdutoSnapshotEventDTO;
import br.com.techmarket_order_service.dto.produtoSnapshot.ProdutoSnapshotResponseDTO;
import br.com.techmarket_order_service.model.ProdutoSnapshot;

public final class ProdutoSnapshotMapper {

    private ProdutoSnapshotMapper () {}

    public static ProdutoSnapshot toEntity(ProdutoSnapshotEventDTO dto, ProdutoSnapshot produto) {

        produto.setIdMongo(dto.idMongo());
        produto.setCodigo(dto.codigo());
        produto.setNome(dto.nome());
        produto.setPrecoUnitario(dto.precoUnitario());
        produto.setPrecoPromocional(dto.precoPromocional());
        produto.setEstoque(dto.estoque());
        produto.setStatus(dto.status());;

        return produto;
    }

    public static ProdutoSnapshotResponseDTO toResponseDTO(ProdutoSnapshot produto) {
        return new ProdutoSnapshotResponseDTO(
                produto.getId(),
                produto.getIdMongo(),
                produto.getCodigo(),
                produto.getNome(),
                produto.getPrecoUnitario(),
                produto.getPrecoPromocional(),
                produto.getEstoque(),
                produto.getStatus()
        );
    }
}
