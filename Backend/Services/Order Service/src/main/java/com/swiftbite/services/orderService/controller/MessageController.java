package com.swiftbite.services.orderService.controller;

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
}