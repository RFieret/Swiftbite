package com.swiftbite.services.orderService.Listener;


import com.swiftbite.services.orderService.model.OrderCompletedEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import com.swiftbite.services.orderService.config.RabbitMQConstants;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class OrderListener {

    private final Set<String> receivedEvents = ConcurrentHashMap.newKeySet();

//    @RabbitListener(queues = RabbitMQConstants.QUEUE)
//    public void handleOrder(String message) {
//        System.out.println("Order received: " + message);
//    }

    @RabbitListener(queues = RabbitMQConstants.QUEUE)
    public void handleOrderCompletedEvent(OrderCompletedEvent event) {
        System.out.println("Received " + event.getEventType() +" for order: " + event.getOrderId());
        receivedEvents.add(event.getOrderId());
        // Proceed to mark order as complete
    }

    public boolean wasOrderReceived(String orderId) {
        return receivedEvents.contains(orderId);
    }
}