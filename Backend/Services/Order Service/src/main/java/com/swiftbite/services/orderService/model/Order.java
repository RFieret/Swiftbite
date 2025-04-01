package com.swiftbite.services.orderService.model;

import lombok.Data;

@Data
public class Order {
    private String id;
    private String customerName;
    private String address;
    private String status;
}
