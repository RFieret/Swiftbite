package com.swiftbite.services.deliveryService.Listener;


import ch.qos.logback.classic.Logger;
import com.swiftbite.services.deliveryService.DTO.OrderMessageDTO;
import com.swiftbite.services.deliveryService.controller.MessageController;
import com.swiftbite.services.deliveryService.model.Delivery;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import com.swiftbite.services.deliveryService.config.RabbitMQConstants;
import com.swiftbite.services.deliveryService.service.DeliveryService;

@Component
@RequiredArgsConstructor
public class DeliveryListener {

    private final DeliveryService deliveryService;
    private static final Logger logger = (Logger) LoggerFactory.getLogger(DeliveryListener.class);

    @RabbitListener(queues = RabbitMQConstants.DELIVERY_QUEUE)
    public void handleOrder(OrderMessageDTO message) {
        logger.info("Message recieved in queue from delivery service");
        System.out.println("Message recieved in queue from delivery service");


        Delivery delivery = new Delivery();
        delivery.setOrderId(message.getId());
        delivery.setCustomerName(message.getCustomerName());
        delivery.setAddress(message.getAddress());
        delivery.setStatus("created");

        deliveryService.saveDelivery(delivery);
    }
}