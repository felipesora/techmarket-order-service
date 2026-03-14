package br.com.techmarket_order_service.amqp;

import org.springframework.amqp.core.*;
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
    public Queue filaProdutosCriados() {
        return QueueBuilder.nonDurable("produto.criado").build();
    }

    @Bean
    public Queue filaProdutosAtualizados() {
        return QueueBuilder.nonDurable("produto.atualizado").build();
    }

    @Bean
    public Queue filaProdutosRemovidos() {
        return QueueBuilder.nonDurable("produto.removido").build();
    }

    @Bean
    public TopicExchange topicExchange() {
        return ExchangeBuilder.topicExchange("produto.exchange").build();
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
    public RabbitAdmin criaRabbitAdmin(ConnectionFactory conn) {
        return new RabbitAdmin(conn);
    }

    @Bean
    public ApplicationListener<ApplicationReadyEvent> inicializaAdmin(RabbitAdmin rabbitAdmin) {
        return event -> rabbitAdmin.initialize();
    }
}
