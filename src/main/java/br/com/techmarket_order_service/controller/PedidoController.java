package br.com.techmarket_order_service.controller;

import br.com.techmarket_order_service.dto.pedido.PedidoCreateDTO;
import br.com.techmarket_order_service.dto.pedido.PedidoResponseDTO;
import br.com.techmarket_order_service.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping
    public ResponseEntity<Page<PedidoResponseDTO>> listarTodosPedidos(@PageableDefault(size = 10) Pageable paginacao) {
        Page<PedidoResponseDTO> pedidos = pedidoService.obterTodosPedidos(paginacao);
        return ResponseEntity.ok(pedidos);
    }

    @PostMapping
    public ResponseEntity<PedidoResponseDTO> cadastrarPedido(@RequestBody @Valid PedidoCreateDTO dto, UriComponentsBuilder uriBuilder) {
        PedidoResponseDTO pedido = pedidoService.cadastrarPedido(dto);
        URI endereco = uriBuilder.path("/pedidos/{id}").buildAndExpand(pedido.id()).toUri();

        return ResponseEntity.created(endereco).body(pedido);
    }
}
