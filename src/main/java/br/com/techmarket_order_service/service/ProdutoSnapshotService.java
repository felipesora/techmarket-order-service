package br.com.techmarket_order_service.service;

import br.com.techmarket_order_service.dto.produtoSnapshot.ProdutoSnapshotCreateDTO;
import br.com.techmarket_order_service.dto.produtoSnapshot.ProdutoSnapshotResponseDTO;
import br.com.techmarket_order_service.dto.produtoSnapshot.ProdutoSnapshotUpdateDTO;
import br.com.techmarket_order_service.model.ProdutoSnapshot;
import br.com.techmarket_order_service.model.enums.StatusProduto;
import br.com.techmarket_order_service.repository.ProdutoSnapshotRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProdutoSnapshotService {

    private final ProdutoSnapshotRepository produtoSnapshotRepository;

    public ProdutoSnapshotService(ProdutoSnapshotRepository produtoSnapshotRepository) {
        this.produtoSnapshotRepository = produtoSnapshotRepository;
    }

    public Page<ProdutoSnapshotResponseDTO> obterTodosProdutos(Pageable paginacao) {
        return produtoSnapshotRepository
                .findAll(paginacao)
                .map(this::converterParaResponseDTO);
    }

    public ProdutoSnapshotResponseDTO obterProdutoPorId(Long id) {
        ProdutoSnapshot produto = buscarEntidadeProdutoPorId(id);
        return converterParaResponseDTO(produto);
    }

    @Transactional
    public ProdutoSnapshotResponseDTO cadastrarProduto(ProdutoSnapshotCreateDTO dto) {
        ProdutoSnapshot produto = new ProdutoSnapshot();
        produto.setCodigo(dto.codigo());
        produto.setNome(dto.nome());
        produto.setPrecoUnitario(dto.precoUnitario());
        produto.setEstoque(dto.estoque());
        produto.setStatus(StatusProduto.ATIVO);

        produto = produtoSnapshotRepository.save(produto);

        return converterParaResponseDTO(produto);
    }

    @Transactional
    public ProdutoSnapshotResponseDTO atualizarProduto(Long id, ProdutoSnapshotUpdateDTO dto) {
        ProdutoSnapshot produto = buscarEntidadeProdutoPorId(id);

        produto.setCodigo(dto.codigo());
        produto.setNome(dto.nome());
        produto.setPrecoUnitario(dto.precoUnitario());
        produto.setEstoque(dto.estoque());
        produto.setStatus(dto.status());

        produto = produtoSnapshotRepository.save(produto);

        return converterParaResponseDTO(produto);
    }

    @Transactional
    public void deletarProduto(Long id) {
        var produto = buscarEntidadeProdutoPorId(id);
        produtoSnapshotRepository.delete(produto);
    }

    private ProdutoSnapshot buscarEntidadeProdutoPorId(Long id) {
        return produtoSnapshotRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto com id: " + id + " não encontrado"));
    }

    private ProdutoSnapshotResponseDTO converterParaResponseDTO(ProdutoSnapshot produto) {
        return new ProdutoSnapshotResponseDTO(
                produto.getId(),
                produto.getIdMongo(),
                produto.getCodigo(),
                produto.getNome(),
                produto.getPrecoUnitario(),
                produto.getEstoque(),
                produto.getStatus()
        );
    }
}
