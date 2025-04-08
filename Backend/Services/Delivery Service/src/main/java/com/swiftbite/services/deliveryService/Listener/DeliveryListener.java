package com.swiftbite.services.deliveryService.Listener;


import com.swiftbite.services.deliveryService.DTO.OrderMessageDTO;
import com.swiftbite.services.deliveryService.model.Delivery;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import com.swiftbite.services.deliveryService.config.RabbitMQConstants;
import com.swiftbite.services.deliveryService.service.DeliveryService;

@Component
@RequiredArgsConstructor
public class DeliveryListener {

    private final DeliveryService deliveryService;

    @RabbitListener(queues = RabbitMQConstants.DELIVERY_QUEUE)
    public void handleOrder(OrderMessageDTO message) {
        Delivery delivery = new Delivery();
        delivery.setOrderId(message.getId());
        delivery.setCustomerName(message.getCustomerName());
        delivery.setAddress(message.getAddress());
        delivery.setStatus("created");

        deliveryService.saveDelivery(delivery);
    }
}