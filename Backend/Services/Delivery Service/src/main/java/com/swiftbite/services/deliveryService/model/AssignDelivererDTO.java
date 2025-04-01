package com.swiftbite.services.deliveryService.model;

import lombok.Data;

@Data
public class AssignDelivererDTO {
    private String deliveryId;
    private Deliverer deliverer;
}
