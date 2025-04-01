package com.swiftbite.services.deliveryService.controller;

import com.swiftbite.services.deliveryService.service.MessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageController {

    private final MessageSender messageSender;

    public void sendCompleteOrderMessage(String orderId, String deliveryId) {
        messageSender.completeOrder(deliveryId, orderId);
    }
}