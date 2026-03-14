package br.com.techmarket_order_service.controller;

import br.com.techmarket_order_service.dto.produtoSnapshot.ProdutoSnapshotResponseDTO;
import br.com.techmarket_order_service.service.ProdutoSnapshotService;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/produtos-snapshot")
public class ProdutoSnapshotController {

    private final ProdutoSnapshotService produtoSnapshotService;

    public ProdutoSnapshotController(ProdutoSnapshotService produtoSnapshotService) {
        this.produtoSnapshotService = produtoSnapshotService;
    }

    @GetMapping
    public ResponseEntity<Page<ProdutoSnapshotResponseDTO>> listarTodosUsuarios(@PageableDefault(size = 10) Pageable paginacao) {
        Page<ProdutoSnapshotResponseDTO> produtos = produtoSnapshotService.obterTodosProdutos(paginacao);
        return ResponseEntity.ok(produtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoSnapshotResponseDTO> buscarPorId(@PathVariable @NotNull Long id) {
        ProdutoSnapshotResponseDTO produto = produtoSnapshotService.obterProdutoPorId(id);
        return ResponseEntity.ok(produto);
    }
}
