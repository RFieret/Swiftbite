package com.swiftbite.services.orderService.controller;

import com.swiftbite.services.orderService.bean.OrderStatusTracker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;
@RequestMapping("/api/orderService")
@Component
public class OrderController {

    @Autowired
    private OrderStatusTracker tracker;

    @GetMapping("/{orderId}")
    public ResponseEntity<Map<String, String>> getOrderStatus(@PathVariable String orderId) {
        String status = tracker.getStatus(orderId);
        Map<String, String> response = Map.of(
                "orderId", orderId,
                "status", status
        );
        return ResponseEntity.ok(response);
    }
}
