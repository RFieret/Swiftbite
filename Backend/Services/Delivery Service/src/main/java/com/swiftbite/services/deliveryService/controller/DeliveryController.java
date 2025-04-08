package com.swiftbite.services.deliveryService.controller;

import com.swiftbite.services.deliveryService.DTO.AssignDelivererDTO;
import com.swiftbite.services.deliveryService.model.Delivery;
import com.swiftbite.services.deliveryService.service.DeliveryService;
import com.swiftbite.services.deliveryService.service.MessageSender;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("api/deliveryservice")
@RequiredArgsConstructor
public class DeliveryController {

    private final MessageSender messageSender;
    private final DeliveryService deliveryService;
    private final MessageController messageController;

    @PostMapping("/createDelivery")
    public Delivery createDelivery(@RequestBody Delivery delivery) {
        return deliveryService.saveDelivery(delivery);
    }

    @PostMapping("/assignDeliverer")
    public void assignDeliverer(@RequestBody AssignDelivererDTO request) {
        deliveryService.assignDeliverer(request.getDeliveryId(), request.getDeliverer());
    }

    @PostMapping("/deliverersDeliveries")
    public ResponseEntity<List<Delivery>> getDeliveriesForDeliverer(@RequestBody Map<String, String> body) {
        String name = body.get("name");
        List<Delivery> deliveries = deliveryService.getDeliveriesForDeliverer(name);
        return ResponseEntity.ok(deliveries);
    }

    @PostMapping("/completeDelivery/{deliveryId}")
    public ResponseEntity<String> completeDelivery(@PathVariable String deliveryId){
        String orderID = deliveryService.completeDelivery(deliveryId);

        messageController.sendCompleteOrderMessage(deliveryId, orderID);
        return ResponseEntity.ok("Message sent to RabbitMQ");
    }

    @GetMapping("/deliveryByOrder/{orderId}")
    public ResponseEntity<Delivery> getDeliveryByOrderId(@PathVariable String orderId) {
        Delivery delivery = deliveryService.findByOrderId(orderId);
        if (delivery == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(delivery);
    }
}
