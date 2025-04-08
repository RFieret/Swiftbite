package com.swiftbite.services.deliveryService.E2EtestScripts;

import com.swiftbite.services.deliveryService.repository.DeliveryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class DeliveryCreatedTest {

    @Autowired
    DeliveryRepository deliveryRepository;

    @Test
    void deliveryShouldBeCreated() {
        var delivery = deliveryRepository.findByOrderId("123");
        assertNotNull(delivery, "Delivery should not be null");
        assertEquals("created", delivery.getStatus());
    }
}

