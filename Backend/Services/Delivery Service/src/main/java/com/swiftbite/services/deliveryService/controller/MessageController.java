package com.swiftbite.services.deliveryService.controller;

import com.swiftbite.services.deliveryService.service.MessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/deliveryService")
@RequiredArgsConstructor
public class MessageController {

    private final MessageSender messageSender;

    @GetMapping("/sendMessage")
    public ResponseEntity<String> sendMessage() {
        messageSender.sendMessage();
        return ResponseEntity.ok("Message sent to RabbitMQ");
    }
}