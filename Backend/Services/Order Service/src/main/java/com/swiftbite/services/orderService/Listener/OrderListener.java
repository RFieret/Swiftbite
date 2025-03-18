package com.swiftbite.services.orderService.Listener;


import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import com.swiftbite.services.orderService.config.RabbitMQConstants;

@Component
public class OrderListener {

    @RabbitListener(queues = RabbitMQConstants.QUEUE)
    public void handleOrder(String message) {
        System.out.println("Order received: " + message);
    }
}