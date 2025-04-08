package com.swiftbite.services.deliveryService.DTO;

import lombok.Data;

@Data
public class OrderMessageDTO {
    private String id;
    private String address;
    private String customerName;
    private String status;
}
