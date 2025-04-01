package com.swiftbite.services.orderService.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// RabbitMQConfig.java
@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue orderQueue() {
        return new Queue(RabbitMQConstants.QUEUE, true);
    }

    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange(RabbitMQConstants.EXCHANGE);
    }

    @Bean
    public Binding orderPlacedBinding(Queue orderQueue, DirectExchange orderExchange) {
        return BindingBuilder
                .bind(orderQueue)
                .to(orderExchange)
                .with(RabbitMQConstants.ORDER_PLACED_ROUTING_KEY);
    }

    @Bean
    public Binding orderCreateDeliveryBinding(Queue orderQueue, DirectExchange orderExchange) {
        return BindingBuilder
                .bind(orderQueue)
                .to(orderExchange)
                .with(RabbitMQConstants.ORDER_CREATE_DELIVERY_ROUTING_KEY);
    }

    @Bean
    public Binding orderCompletedBinding(Queue orderQueue, DirectExchange orderExchange) {
        return BindingBuilder
                .bind(orderQueue)
                .to(orderExchange)
                .with(RabbitMQConstants.ORDER_COMPLETED_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }

}
