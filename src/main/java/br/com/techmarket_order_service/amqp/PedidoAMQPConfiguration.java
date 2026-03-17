package br.com.techmarket_order_service.amqp;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PedidoAMQPConfiguration {

    @Bean
    public RabbitAdmin criaRabbitAdmin(ConnectionFactory conn) {
        return new RabbitAdmin(conn);
    }

    @Bean
    public ApplicationListener<ApplicationReadyEvent> inicializaAdmin(RabbitAdmin rabbitAdmin) {
        return event -> rabbitAdmin.initialize();
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter messageConverter) {

        SimpleRabbitListenerContainerFactory factory =
                new SimpleRabbitListenerContainerFactory();

        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);

        factory.setDefaultRequeueRejected(false);

        return factory;
    }

    @Bean
    public Queue filaProdutosCriados() {
        return QueueBuilder
                .durable("produto.criado")
                .withArgument("x-dead-letter-exchange", "produto.dlx")
                .withArgument("x-dead-letter-routing-key", "produto.criado.dlq")
                .build();
    }

    @Bean
    public Queue filaProdutosAtualizados() {
        return QueueBuilder
                .durable("produto.atualizado")
                .withArgument("x-dead-letter-exchange", "produto.dlx")
                .withArgument("x-dead-letter-routing-key", "produto.atualizado.dlq")
                .build();
    }

    @Bean
    public Queue filaProdutosRemovidos() {
        return QueueBuilder
                .durable("produto.removido")
                .withArgument("x-dead-letter-exchange", "produto.dlx")
                .withArgument("x-dead-letter-routing-key", "produto.removido.dlq")
                .build();
    }

    @Bean
    public Queue filaProdutosCriadosDLQ() {
        return QueueBuilder.durable("produto.criado.dlq").build();
    }

    @Bean
    public Queue filaProdutosAtualizadosDLQ() {
        return QueueBuilder.durable("produto.atualizado.dlq").build();
    }

    @Bean
    public Queue filaProdutosRemovidosDLQ() {
        return QueueBuilder.durable("produto.removido.dlq").build();
    }

    @Bean
    public TopicExchange topicExchange() {
        return ExchangeBuilder.topicExchange("produto.exchange").build();
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return ExchangeBuilder.directExchange("produto.dlx").build();
    }

    @Bean
    public Binding bindProdutosCriados(Queue filaProdutosCriados, TopicExchange topicExchange) {
        return BindingBuilder
                .bind(filaProdutosCriados)
                .to(topicExchange)
                .with("produto.criado");
    }

    @Bean
    public Binding bindProdutosAtualizados(Queue filaProdutosAtualizados, TopicExchange topicExchange) {
        return BindingBuilder
                .bind(filaProdutosAtualizados)
                .to(topicExchange)
                .with("produto.atualizado");
    }

    @Bean
    public Binding bindProdutosRemovidos(Queue filaProdutosRemovidos, TopicExchange topicExchange) {
        return BindingBuilder
                .bind(filaProdutosRemovidos)
                .to(topicExchange)
                .with("produto.removido");
    }

    @Bean
    public Binding bindDLQProdutosCriados(Queue filaProdutosCriadosDLQ, DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(filaProdutosCriadosDLQ)
                .to(deadLetterExchange)
                .with("produto.criado.dlq");
    }

    @Bean
    public Binding bindDLQProdutosAtualizados(Queue filaProdutosAtualizadosDLQ, DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(filaProdutosAtualizadosDLQ)
                .to(deadLetterExchange)
                .with("produto.atualizado.dlq");
    }

    @Bean
    public Binding bindDLQProdutosRemovidos(Queue filaProdutosRemovidosDLQ, DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(filaProdutosRemovidosDLQ)
                .to(deadLetterExchange)
                .with("produto.removido.dlq");
    }
}
