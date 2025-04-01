package com.swiftbite.services.deliveryService.Listener;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.swiftbite.services.deliveryService.model.Delivery;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import com.swiftbite.services.deliveryService.config.RabbitMQConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swiftbite.services.deliveryService.service.DeliveryService;

@Component
@RequiredArgsConstructor
public class DeliveryListener {

    private final ObjectMapper objectMapper;
    private final DeliveryService deliveryService;

    @RabbitListener(queues = RabbitMQConstants.DELIVERY_QUEUE)
    public void handleOrder(String message) {
        try {
            Delivery delivery = objectMapper.readValue(message, Delivery.class);
            deliveryService.saveDelivery(delivery);
        } catch (JsonProcessingException e) {
            System.err.println("❌ Invalid JSON: " + message);
        }
    }
}