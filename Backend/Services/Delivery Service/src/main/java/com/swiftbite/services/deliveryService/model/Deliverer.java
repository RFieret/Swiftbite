package com.swiftbite.services.deliveryService.model;

import lombok.Data;

@Data
public class Deliverer {
    private String name;
    private String phone;

    @Override
    public String toString() {
        return "Deliverer{name='" + name + "', phone='" + phone + "'}";
    }

}
