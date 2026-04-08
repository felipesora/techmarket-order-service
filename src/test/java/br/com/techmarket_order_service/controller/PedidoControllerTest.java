package br.com.techmarket_order_service.controller;

import br.com.techmarket_order_service.config.SecurityFilter;
import br.com.techmarket_order_service.dto.itemPedido.ItemPedidoResponseDTO;
import br.com.techmarket_order_service.dto.itemPedido.ProdutoPedidoResponseDTO;
import br.com.techmarket_order_service.dto.pedido.PedidoCreateDTO;
import br.com.techmarket_order_service.dto.pedido.PedidoResponseDTO;
import br.com.techmarket_order_service.dto.pedido.PedidoStatusUpdateDTO;
import br.com.techmarket_order_service.model.enums.MetodoPagamento;
import br.com.techmarket_order_service.model.enums.StatusPedido;
import br.com.techmarket_order_service.service.PedidoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PedidoController.class)
@AutoConfigureMockMvc(addFilters = false)
class PedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PedidoService pedidoService;

    @MockitoBean
    private SecurityFilter securityFilter;

    private PedidoResponseDTO pedidoResponseDTO;
    private PedidoCreateDTO pedidoCreateDTO;
    private PedidoStatusUpdateDTO pedidoStatusUpdateDTO;
    private ItemPedidoResponseDTO itemPedidoResponseDTO;
    private ProdutoPedidoResponseDTO produtoPedidoResponseDTO;

    private final Long PEDIDO_ID = 1L;
    private final Long USUARIO_ID = 100L;
    private final Long ITEM_ID = 1L;
    private final String ID_MONGO_PRODUTO = "507f1f77bcf86cd799439011";
    private final String PRODUTO_CODIGO = "PROD001";
    private final String PRODUTO_NOME = "Smartphone Galaxy S23";
    private final BigDecimal VALOR_TOTAL = new BigDecimal("200.00");
    private final Integer QUANTIDADE = 2;
    private final BigDecimal PRECO_UNITARIO = new BigDecimal("100.00");
    private final BigDecimal SUBTOTAL = new BigDecimal("200.00");
    private final OffsetDateTime DATA_CRIACAO = OffsetDateTime.now(ZoneOffset.UTC);

    @BeforeEach
    void setUp() {
        inicializarDTOs();
    }

    private void inicializarDTOs() {
        produtoPedidoResponseDTO = new ProdutoPedidoResponseDTO(
                1L,
                ID_MONGO_PRODUTO,
                PRODUTO_CODIGO,
                PRODUTO_NOME,
                PRECO_UNITARIO
        );

        itemPedidoResponseDTO = new ItemPedidoResponseDTO(
                ITEM_ID,
                QUANTIDADE,
                SUBTOTAL,
                produtoPedidoResponseDTO
        );

        pedidoResponseDTO = new PedidoResponseDTO(
                PEDIDO_ID,
                USUARIO_ID,
                VALOR_TOTAL,
                DATA_CRIACAO,
                MetodoPagamento.CARTAO_CREDITO,
                StatusPedido.AGUARDANDO_PAGAMENTO,
                List.of(itemPedidoResponseDTO)
        );

        pedidoCreateDTO = new PedidoCreateDTO(
                USUARIO_ID,
                MetodoPagamento.CARTAO_CREDITO,
                List.of()
        );

        pedidoStatusUpdateDTO = new PedidoStatusUpdateDTO(StatusPedido.PAGAMENTO_APROVADO);
    }

    @Nested
    @DisplayName("GET /pedidos")
    class ListarTodosPedidosTest {

        @Test
        @DisplayName("Deve retornar lista paginada de pedidos com status 200")
        void deveRetornarListaPaginadaDePedidos() throws Exception {
            Pageable pageable = PageRequest.of(0, 10);
            Page<PedidoResponseDTO> page = new PageImpl<>(
                    List.of(pedidoResponseDTO),
                    pageable,
                    1
            );

            when(pedidoService.obterTodosPedidos(any(Pageable.class))).thenReturn(page);

            mockMvc.perform(get("/pedidos")
                            .param("page", "0")
                            .param("size", "10")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)))
                    .andExpect(jsonPath("$.content[0].id_pedido", is(PEDIDO_ID.intValue())))
                    .andExpect(jsonPath("$.content[0].id_usuario", is(USUARIO_ID.intValue())))
                    .andExpect(jsonPath("$.content[0].valor_total", is(VALOR_TOTAL.doubleValue())))
                    .andExpect(jsonPath("$.content[0].metodo_pagamento", is(MetodoPagamento.CARTAO_CREDITO.toString())))
                    .andExpect(jsonPath("$.content[0].status_pedido", is(StatusPedido.AGUARDANDO_PAGAMENTO.toString())))
                    .andExpect(jsonPath("$.content[0].itens", hasSize(1)))
                    .andExpect(jsonPath("$.content[0].itens[0].id_item_pedido", is(ITEM_ID.intValue())))
                    .andExpect(jsonPath("$.content[0].itens[0].quantidade", is(QUANTIDADE)))
                    .andExpect(jsonPath("$.content[0].itens[0].subtotal", is(SUBTOTAL.doubleValue())))
                    .andExpect(jsonPath("$.content[0].itens[0].produto.id_produto", is(1)))
                    .andExpect(jsonPath("$.content[0].itens[0].produto.id_mongo_produto", is(ID_MONGO_PRODUTO)))
                    .andExpect(jsonPath("$.content[0].itens[0].produto.codigo", is(PRODUTO_CODIGO)))
                    .andExpect(jsonPath("$.content[0].itens[0].produto.nome", is(PRODUTO_NOME)))
                    .andExpect(jsonPath("$.totalElements", is(1)))
                    .andExpect(jsonPath("$.totalPages", is(1)));

            verify(pedidoService, times(1)).obterTodosPedidos(any(Pageable.class));
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não houver pedidos")
        void deveRetornarListaVazia() throws Exception {
            Page<PedidoResponseDTO> paginaVazia = new PageImpl<>(List.of());

            when(pedidoService.obterTodosPedidos(any(Pageable.class))).thenReturn(paginaVazia);

            mockMvc.perform(get("/pedidos")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(0)))
                    .andExpect(jsonPath("$.totalElements", is(0)));

            verify(pedidoService, times(1)).obterTodosPedidos(any(Pageable.class));
        }

        @Test
        @DisplayName("Deve usar paginação padrão (size=10) quando parâmetros não forem fornecidos")
        void deveUsarPaginacaoPadrao() throws Exception {
            Page<PedidoResponseDTO> page = new PageImpl<>(List.of(pedidoResponseDTO));

            when(pedidoService.obterTodosPedidos(any(Pageable.class))).thenReturn(page);

            mockMvc.perform(get("/pedidos")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            verify(pedidoService, times(1)).obterTodosPedidos(any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("GET /pedidos/{id}")
    class BuscarPedidoPorIdTest {

        @Test
        @DisplayName("Deve retornar pedido quando ID existir")
        void deveRetornarPedidoQuandoIdExistir() throws Exception {
            when(pedidoService.buscarPedidoPorId(PEDIDO_ID)).thenReturn(pedidoResponseDTO);

            mockMvc.perform(get("/pedidos/{id}", PEDIDO_ID)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id_pedido", is(PEDIDO_ID.intValue())))
                    .andExpect(jsonPath("$.id_usuario", is(USUARIO_ID.intValue())))
                    .andExpect(jsonPath("$.valor_total", is(VALOR_TOTAL.doubleValue())))
                    .andExpect(jsonPath("$.metodo_pagamento", is(MetodoPagamento.CARTAO_CREDITO.toString())))
                    .andExpect(jsonPath("$.status_pedido", is(StatusPedido.AGUARDANDO_PAGAMENTO.toString())))
                    .andExpect(jsonPath("$.itens", hasSize(1)));

            verify(pedidoService, times(1)).buscarPedidoPorId(PEDIDO_ID);
        }
    }

    @Nested
    @DisplayName("GET /pedidos/usuario/{usuarioId}")
    class BuscarPedidosPorUsuarioTest {

        @Test
        @DisplayName("Deve retornar pedidos do usuário quando existirem")
        void deveRetornarPedidosDoUsuario() throws Exception {
            Pageable pageable = PageRequest.of(0, 10);
            Page<PedidoResponseDTO> page = new PageImpl<>(
                    List.of(pedidoResponseDTO),
                    pageable,
                    1
            );

            when(pedidoService.buscarPorUsuario(eq(USUARIO_ID), any(Pageable.class))).thenReturn(page);

            mockMvc.perform(get("/pedidos/usuario/{usuarioId}", USUARIO_ID)
                            .param("page", "0")
                            .param("size", "10")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)))
                    .andExpect(jsonPath("$.content[0].id_usuario", is(USUARIO_ID.intValue())))
                    .andExpect(jsonPath("$.totalElements", is(1)));

            verify(pedidoService, times(1)).buscarPorUsuario(eq(USUARIO_ID), any(Pageable.class));
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando usuário não tiver pedidos")
        void deveRetornarListaVaziaQuandoUsuarioSemPedidos() throws Exception {
            Page<PedidoResponseDTO> paginaVazia = new PageImpl<>(List.of());

            when(pedidoService.buscarPorUsuario(eq(USUARIO_ID), any(Pageable.class))).thenReturn(paginaVazia);

            mockMvc.perform(get("/pedidos/usuario/{usuarioId}", USUARIO_ID)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(0)))
                    .andExpect(jsonPath("$.totalElements", is(0)));

            verify(pedidoService, times(1)).buscarPorUsuario(eq(USUARIO_ID), any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("POST /pedidos")
    class CadastrarPedidoTest {

        @Test
        @DisplayName("Deve cadastrar pedido com sucesso e retornar 201")
        void deveCadastrarPedidoComSucesso() throws Exception {
            when(pedidoService.cadastrarPedido(any(PedidoCreateDTO.class))).thenReturn(pedidoResponseDTO);

            mockMvc.perform(post("/pedidos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(pedidoCreateDTO)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(header().exists("Location"))
                    .andExpect(header().string("Location", containsString("/pedidos/" + PEDIDO_ID)))
                    .andExpect(jsonPath("$.id_pedido", is(PEDIDO_ID.intValue())))
                    .andExpect(jsonPath("$.id_usuario", is(USUARIO_ID.intValue())))
                    .andExpect(jsonPath("$.status_pedido", is(StatusPedido.AGUARDANDO_PAGAMENTO.toString())));

            verify(pedidoService, times(1)).cadastrarPedido(any(PedidoCreateDTO.class));
        }

        @Test
        @DisplayName("Deve retornar 400 quando dados inválidos forem fornecidos")
        void deveRetornar400QuandoDadosInvalidos() throws Exception {
            PedidoCreateDTO dtoInvalido = new PedidoCreateDTO(
                    null, // idUsuario obrigatório
                    null, // metodoPagamento obrigatório
                    null  // itens obrigatório
            );

            mockMvc.perform(post("/pedidos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dtoInvalido)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());

            verify(pedidoService, never()).cadastrarPedido(any(PedidoCreateDTO.class));
        }
    }

    @Nested
    @DisplayName("PATCH /pedidos/{id}/status")
    class AtualizarStatusPedidoTest {

        @Test
        @DisplayName("Deve atualizar status do pedido com sucesso e retornar 200")
        void deveAtualizarStatusComSucesso() throws Exception {
            PedidoResponseDTO pedidoAtualizado = new PedidoResponseDTO(
                    PEDIDO_ID,
                    USUARIO_ID,
                    VALOR_TOTAL,
                    DATA_CRIACAO,
                    MetodoPagamento.CARTAO_CREDITO,
                    StatusPedido.PAGAMENTO_APROVADO,
                    List.of(itemPedidoResponseDTO)
            );

            when(pedidoService.atualizarStatus(eq(PEDIDO_ID), any(PedidoStatusUpdateDTO.class)))
                    .thenReturn(pedidoAtualizado);

            mockMvc.perform(patch("/pedidos/{id}/status", PEDIDO_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(pedidoStatusUpdateDTO)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id_pedido", is(PEDIDO_ID.intValue())))
                    .andExpect(jsonPath("$.status_pedido", is(StatusPedido.PAGAMENTO_APROVADO.toString())));

            verify(pedidoService, times(1)).atualizarStatus(eq(PEDIDO_ID), any(PedidoStatusUpdateDTO.class));
        }

        @Test
        @DisplayName("Deve retornar 400 quando status for inválido")
        void deveRetornar400QuandoStatusInvalido() throws Exception {
            String payloadInvalido = "{\"status_pedido\": \"STATUS_INEXISTENTE\"}";

            mockMvc.perform(patch("/pedidos/{id}/status", PEDIDO_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(payloadInvalido))
                    .andDo(print())
                    .andExpect(status().isBadRequest());

            verify(pedidoService, never()).atualizarStatus(anyLong(), any(PedidoStatusUpdateDTO.class));
        }

        @Test
        @DisplayName("Deve retornar 400 quando corpo da requisição estiver vazio")
        void deveRetornar400QuandoCorpoVazio() throws Exception {
            mockMvc.perform(patch("/pedidos/{id}/status", PEDIDO_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(""))
                    .andDo(print())
                    .andExpect(status().isBadRequest());

            verify(pedidoService, never()).atualizarStatus(anyLong(), any(PedidoStatusUpdateDTO.class));
        }
    }

    @Nested
    @DisplayName("Validação de parâmetros de requisição")
    class ValidacaoParametrosTest {

        @Test
        @DisplayName("Deve aceitar diferentes valores de paginação")
        void deveAceitarDiferentesValoresPaginacao() throws Exception {
            Page<PedidoResponseDTO> page = new PageImpl<>(List.of(pedidoResponseDTO));

            when(pedidoService.obterTodosPedidos(any(Pageable.class))).thenReturn(page);

            mockMvc.perform(get("/pedidos")
                            .param("page", "2")
                            .param("size", "20")
                            .param("sort", "dataCriacao,desc")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            verify(pedidoService, times(1)).obterTodosPedidos(any(Pageable.class));
        }

        @Test
        @DisplayName("Deve aceitar valores negativos para page (que serão tratados como padrão)")
        void deveAceitarValoresNegativosPage() throws Exception {
            Page<PedidoResponseDTO> page = new PageImpl<>(List.of(pedidoResponseDTO));

            when(pedidoService.obterTodosPedidos(any(Pageable.class))).thenReturn(page);

            mockMvc.perform(get("/pedidos")
                            .param("page", "-1")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            verify(pedidoService, times(1)).obterTodosPedidos(any(Pageable.class));
        }
    }
}