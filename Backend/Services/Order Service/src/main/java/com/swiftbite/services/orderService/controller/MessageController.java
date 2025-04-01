package com.swiftbite.services.orderService.controller;

import com.swiftbite.services.orderService.model.Order;
import com.swiftbite.services.orderService.service.MessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orderService")
@RequiredArgsConstructor
public class MessageController {

    private final MessageSender messageSender;

    @GetMapping("/sendMessage")
    public ResponseEntity<String> sendMessage() {
        messageSender.sendMessage();
        return ResponseEntity.ok("Message sent to RabbitMQ");
    }

    @GetMapping("/createDelivery")
    public ResponseEntity<String> createDelivery() {
        Order order = new Order();
        order.setId("5");
        order.setAddress("Test");
        order.setStatus("order");
        order.setCustomerName("database");


        messageSender.createDelivery(order);
        return ResponseEntity.ok("Message sent to RabbitMQ");
    }
}