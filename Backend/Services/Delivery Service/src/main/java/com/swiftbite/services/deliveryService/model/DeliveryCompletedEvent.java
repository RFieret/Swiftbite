package com.swiftbite.services.deliveryService.model;

import lombok.Data;
import java.time.Instant;

@Data
public class DeliveryCompletedEvent {
    private String eventType = "DeliveryCompleted";
    private String orderId;
    private String deliveryId;
    private Instant timestamp;

    public DeliveryCompletedEvent(String orderId, String deliveryId, Instant now) {
        this.orderId = orderId;
        this.deliveryId = deliveryId;
        this.timestamp = now;
    }

    @Override
    public String toString() {
        return "Event{eventType'"+eventType+"',orderId'"+orderId+"',deliveryId'"+deliveryId+"',timestamp'"+timestamp+"'}";
    }
}
