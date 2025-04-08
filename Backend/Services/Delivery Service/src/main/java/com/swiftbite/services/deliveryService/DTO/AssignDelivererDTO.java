package com.swiftbite.services.deliveryService.DTO;

import com.swiftbite.services.deliveryService.model.Deliverer;
import lombok.Data;

@Data
public class AssignDelivererDTO {
    private String deliveryId;
    private Deliverer deliverer;
}
