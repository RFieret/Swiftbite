package com.swiftbite.services.orderService.model;

import lombok.Data;
import java.time.Instant;

@Data
public class OrderCompletedEvent {
    private String eventType = "DeliveryCompleted";
    private String orderId;
    private String deliveryId;
    private Instant timestamp;

    public OrderCompletedEvent(String orderId, String deliveryId, Instant now) {
        this.orderId = orderId;
        this.deliveryId = deliveryId;
        this.timestamp = now;
    }

    @Override
    public String toString() {
        return "Event{eventType :'"+eventType+"',orderId :'"+orderId+"',deliveryId :'"+deliveryId+"',timestamp :'"+timestamp+"'}";
    }
}

