package com.swiftbite.services.deliveryService.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "deliveries")
public class Delivery {
    @Id
    private String id;
    private String orderId;
    private String customerName;
    private String address;
    private String status;
    private Deliverer deliverer;
}
