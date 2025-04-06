package com.swiftbite.services.orderService.bean;

import java.util.Map;
import org.springframework.stereotype.Component;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class OrderStatusTracker {
    private final Map<String, String> statusMap = new ConcurrentHashMap<>();

    public void markDelivered(String orderId) {
        statusMap.put(orderId, "DELIVERED");
    }

    public String getStatus(String orderId) {
        return statusMap.getOrDefault(orderId, "UNKNOWN");
    }

    public boolean isDelivered(String orderId) {
        return "DELIVERED".equals(statusMap.get(orderId));
    }
}
