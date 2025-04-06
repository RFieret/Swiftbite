package com.swiftbite.services.deliveryService.service;

import com.swiftbite.services.deliveryService.model.Deliverer;
import com.swiftbite.services.deliveryService.model.Delivery;
import com.swiftbite.services.deliveryService.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;

    public List<Delivery> getDeliveriesForDeliverer(String delivererName) {
        return deliveryRepository.findByDeliverer_Name(delivererName);
    }

    public Delivery saveDelivery(Delivery delivery) {
        return deliveryRepository.save(delivery);
    }

    public void assignDeliverer(String deliveryId, Deliverer deliverer) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery not found"));

        delivery.setDeliverer(deliverer);
        deliveryRepository.save(delivery);
    }

    public String completeDelivery(String deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery not found"));

        delivery.setStatus("Complete");
        deliveryRepository.save(delivery);

        return delivery.getOrderId();
    }

    public Delivery getDeliveryByOrderId(String orderId) {
        return deliveryRepository.findByOrderId(orderId).orElse(null);
    }
}
