package com.swiftbite.services.orderService.Listener;


import com.swiftbite.services.orderService.model.OrderCompletedEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import com.swiftbite.services.orderService.config.RabbitMQConstants;

@Component
public class OrderListener {

    @RabbitListener(queues = RabbitMQConstants.QUEUE)
    public void handleOrder(String message) {
        System.out.println("Order received: " + message);
    }

    @RabbitListener(queues = RabbitMQConstants.QUEUE)
    public void handleOrderCompletedEvent(OrderCompletedEvent event) {
        System.out.println("✅ Received event for order: " + event);
        // Proceed to mark order as complete
    }
}