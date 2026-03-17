package br.com.techmarket_order_service.amqp;

import br.com.techmarket_order_service.dto.produtoSnapshot.ProdutoSnapshotEventDTO;
import br.com.techmarket_order_service.service.ProdutoSnapshotService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ProdutoListener {

    private final ProdutoSnapshotService produtoSnapshotService;

    public ProdutoListener(ProdutoSnapshotService produtoSnapshotService) {
        this.produtoSnapshotService = produtoSnapshotService;
    }

    @RabbitListener(queues = "produto.criado")
    public void recebeProdutoParaCadastrar(ProdutoSnapshotEventDTO produtoDTO) {
        try {
            System.out.println("Mensagem recebida da fila de produtos criados");
            System.out.println("Conteúdo: " + produtoDTO);

            var salvo = produtoSnapshotService.cadastrarProduto(produtoDTO);
            System.out.println("Produto com id: " + salvo.idMongo() + " salvo no banco com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao cadastrar produto");
            throw new RuntimeException(e);
        }
    }

    @RabbitListener(queues = "produto.atualizado")
    public void recebeProdutoParaAtualizar(ProdutoSnapshotEventDTO produtoDTO) {
        try {
            System.out.println("Mensagem recebida da fila de produtos atualizados");
            System.out.println("Conteúdo: " + produtoDTO);

            var atualizado = produtoSnapshotService.atualizarProduto(produtoDTO);
            System.out.println("Produto com id: " + atualizado.idMongo() + " atualizado no banco com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao atualizar produto");
            throw new RuntimeException(e);
        }
    }

    @RabbitListener(queues = "produto.removido")
    public void recebeProdutoParaRemover(ProdutoSnapshotEventDTO produtoDTO) {
        try {
            System.out.println("Mensagem recebida da fila de produtos removidos");
            System.out.println("Conteúdo: " + produtoDTO);

            var removido = produtoSnapshotService.deletarProduto(produtoDTO);
            System.out.println("Produto com id: " + removido.idMongo() + " removido no banco com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao remover produto");
            throw new RuntimeException(e);
        }
    }
}
