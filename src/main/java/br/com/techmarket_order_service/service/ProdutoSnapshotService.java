package br.com.techmarket_order_service.service;

import br.com.techmarket_order_service.dto.produtoSnapshot.ProdutoSnapshotEventDTO;
import br.com.techmarket_order_service.dto.produtoSnapshot.ProdutoSnapshotResponseDTO;
import br.com.techmarket_order_service.mapper.ProdutoSnapshotMapper;
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
                .map(ProdutoSnapshotMapper::toResponseDTO);
    }

    public ProdutoSnapshotResponseDTO obterProdutoPorId(Long id) {
        ProdutoSnapshot produto = buscarEntidadeProdutoPorId(id);
        return ProdutoSnapshotMapper.toResponseDTO(produto);
    }

    @Transactional
    public ProdutoSnapshotResponseDTO cadastrarProduto(ProdutoSnapshotEventDTO dto) {
        ProdutoSnapshot produto = new ProdutoSnapshot();
        ProdutoSnapshotMapper.toEntity(dto, produto);
        produtoSnapshotRepository.save(produto);

        return ProdutoSnapshotMapper.toResponseDTO(produto);
    }

    @Transactional
    public ProdutoSnapshotResponseDTO atualizarProduto(ProdutoSnapshotEventDTO dto) {
        ProdutoSnapshot produto = buscarPorIdMongo(dto.idMongo());
        ProdutoSnapshotMapper.toEntity(dto, produto);
        produtoSnapshotRepository.save(produto);

        return ProdutoSnapshotMapper.toResponseDTO(produto);
    }

    @Transactional
    public ProdutoSnapshotResponseDTO deletarProduto(ProdutoSnapshotEventDTO dto) {
        var produto = buscarPorIdMongo(dto.idMongo());
        produtoSnapshotRepository.delete(produto);
        return ProdutoSnapshotMapper.toResponseDTO(produto);
    }

    private ProdutoSnapshot buscarEntidadeProdutoPorId(Long id) {
        return produtoSnapshotRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto com id: " + id + " não encontrado"));
    }

    private ProdutoSnapshot buscarPorIdMongo(String idMongo) {
        return produtoSnapshotRepository.findByIdMongo(idMongo)
                .orElseThrow(() -> new EntityNotFoundException("Produto com idMongo: " + idMongo + " não encontrado"));
    }
}
