package br.com.techmarket_order_service.amqp;

import br.com.techmarket_order_service.dto.produtoSnapshot.ProdutoSnapshotCreateDTO;
import br.com.techmarket_order_service.service.ProdutoSnapshotService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ProdutoListener {

    private final ProdutoSnapshotService produtoSnapshotService;

    public ProdutoListener(ProdutoSnapshotService produtoSnapshotService) {
        this.produtoSnapshotService = produtoSnapshotService;
    }

    @RabbitListener(queues = "produtos.criados")
    public void recebeProduto(ProdutoSnapshotCreateDTO produtoDTO) {
        System.out.println("Mensagem recebida da fila de produtos criados");
        System.out.println("Conteúdo: " + produtoDTO);

        var salvo = produtoSnapshotService.cadastrarProduto(produtoDTO);
        System.out.printf("Produto com id: " + salvo.idMongo() + " salvo no banco com sucesso!");
    }
}
