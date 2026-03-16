package br.com.techmarket_order_service.model.enums;

public enum StatusPedido {
    CRIADO,
    AGUARDANDO_PAGAMENTO,
    PAGAMENTO_APROVADO,
    PAGAMENTO_RECUSADO,
    PROCESSANDO,
    ENVIADO,
    ENTREGUE,
    CANCELADO
}
