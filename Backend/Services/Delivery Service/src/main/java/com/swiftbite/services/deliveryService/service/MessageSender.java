package com.swiftbite.services.deliveryService.service;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class MessageSender {
    private final RabbitTemplate rabbitTemplate;

    public void sendMessage() {
        String message = "Hello World";
        rabbitTemplate.convertAndSend("order.exchange","order.placed", message);

    }
}
