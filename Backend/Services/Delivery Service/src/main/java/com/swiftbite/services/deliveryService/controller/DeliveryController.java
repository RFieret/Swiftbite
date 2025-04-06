package com.swiftbite.services.deliveryService.controller;

import com.swiftbite.services.deliveryService.model.AssignDelivererDTO;
import com.swiftbite.services.deliveryService.model.Delivery;
import com.swiftbite.services.deliveryService.service.DeliveryService;
import com.swiftbite.services.deliveryService.service.MessageSender;
import com.swiftbite.services.deliveryService.controller.MessageController;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

    private final MessageSender messageSender;
    private final DeliveryService deliveryService;
    private final MessageController messageController;

    @PostMapping("/create-delivery")
    public Delivery createDelivery(@RequestBody Delivery delivery) {
        return deliveryService.saveDelivery(delivery);
    }

    @PostMapping("/assign-deliverer")
    public void assignDeliverer(@RequestBody AssignDelivererDTO request) {
        deliveryService.assignDeliverer(request.getDeliveryId(), request.getDeliverer());
    }

    @PostMapping("/deliverers-deliveries")
    public ResponseEntity<List<Delivery>> getDeliveriesForDeliverer(@RequestBody Map<String, String> body) {
        String name = body.get("name");
        List<Delivery> deliveries = deliveryService.getDeliveriesForDeliverer(name);
        return ResponseEntity.ok(deliveries);
    }

    @PostMapping("/complete-delivery")
    public ResponseEntity<String> completeDelivery(@RequestBody Map<String, String> body){
        String deliveryID = body.get("deliveryId");
        String orderID = deliveryService.completeDelivery(deliveryID);

        messageController.sendCompleteOrderMessage(deliveryID, orderID);
        return ResponseEntity.ok("Message sent to RabbitMQ");
    }

    @GetMapping("/delivery-by-order/{orderId}")
    public ResponseEntity<Delivery> getDeliveryByOrderId(@PathVariable String orderId) {
        Delivery delivery = deliveryService.getDeliveryByOrderId(orderId);
        if (delivery != null) {
            return ResponseEntity.ok(delivery);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
