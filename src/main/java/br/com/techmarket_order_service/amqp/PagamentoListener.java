package br.com.techmarket_order_service.amqp;

import br.com.techmarket_order_service.dto.pagamento.PagamentoConfirmadoEventDTO;
import br.com.techmarket_order_service.service.PedidoService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PagamentoListener {

    private final PedidoService pedidoService;

    public PagamentoListener(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @RabbitListener(queues = "pagamento.confirmado.fila")
    public void recebePagamentoAprovado(PagamentoConfirmadoEventDTO evento) {
        System.out.println("Mensagem recebida da fila de pagamentos confirmados");
        System.out.println("Conteúdo: " + evento);

        pedidoService.confirmarPagamento(evento.idPedido());
        System.out.println("Pedido com pagamento confirmado!");
    }
}
