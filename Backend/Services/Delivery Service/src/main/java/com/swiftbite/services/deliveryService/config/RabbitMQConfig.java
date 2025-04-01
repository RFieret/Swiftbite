package com.swiftbite.services.deliveryService.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// RabbitMQConfig.java
@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue deliveryQueue() {
        return new Queue(RabbitMQConstants.DELIVERY_QUEUE, true);
    }

    @Bean
    public DirectExchange deliveryExchange() {
        return new DirectExchange(RabbitMQConstants.DELIVERY_EXCHANGE);
    }


    @Bean
    public Binding deliveryCreateDeliveryBinding(Queue deliveryQueue, DirectExchange deliveryExchange) {
        return BindingBuilder
                .bind(deliveryQueue)
                .to(deliveryExchange)
                .with(RabbitMQConstants.CREATE_DELIVERY_ROUTING_KEY);
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
