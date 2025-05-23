package com.swiftbite.services.orderService.service;

import com.swiftbite.services.orderService.model.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class MessageSender {
    private final RabbitTemplate rabbitTemplate;

    public void sendMessage() {
        String message = "Hello World";
        rabbitTemplate.convertAndSend("delivery.exchange","delivery.placed", message);
    }

    public void createDelivery(Order order){
        rabbitTemplate.convertAndSend("delivery.exchange","delivery.createDelivery", order);
        System.out.println("Delivery sent");
    }
}
