package com.swiftbite.services.orderService.controller;

import com.swiftbite.services.orderService.Listener.OrderListener;
import com.swiftbite.services.orderService.model.Order;
import com.swiftbite.services.orderService.service.MessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orderService")
@RequiredArgsConstructor
public class MessageController {

    private final MessageSender messageSender;
    private final OrderListener orderListener;

    @GetMapping("/sendMessage")
    public ResponseEntity<String> sendMessage() {
        messageSender.sendMessage();
        return ResponseEntity.ok("Message sent to RabbitMQ");
    }

    @PostMapping("/createDelivery")
    public ResponseEntity<String> createDelivery(@RequestBody Order order) {
        messageSender.createDelivery(order);
        return ResponseEntity.ok("Message sent to RabbitMQ");
    }

    @GetMapping("order-received/{orderId}")
    public ResponseEntity<String> orderReceived(@PathVariable String orderId) {
        Boolean isRecievecd = orderListener.wasOrderReceived(orderId);

        System.out.println("Order Received: " + isRecievecd);

        if (isRecievecd)
            return ResponseEntity.ok("Order Received: " + orderId);
        else
            return ResponseEntity.badRequest().body("Order not received: " + orderId);
    }
}