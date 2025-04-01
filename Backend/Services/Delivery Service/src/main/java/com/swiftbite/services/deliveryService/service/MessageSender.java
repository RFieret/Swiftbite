package com.swiftbite.services.deliveryService.service;

import com.swiftbite.services.deliveryService.model.DeliveryCompletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import java.time.Instant;

@Service
@RequiredArgsConstructor

public class MessageSender {
    private final RabbitTemplate rabbitTemplate;

    public void completeOrder(String orderId, String deliveryId) {
        DeliveryCompletedEvent event = new DeliveryCompletedEvent(
                orderId,
                deliveryId,
                Instant.now()
        );

        System.out.println(event);

        rabbitTemplate.convertAndSend("order.exchange", "order.completed", event);
    }
}
