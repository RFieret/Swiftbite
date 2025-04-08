package com.swiftbite.services.deliveryService.E2EtestScripts;

import com.swiftbite.services.deliveryService.model.Delivery;
import com.swiftbite.services.deliveryService.repository.DeliveryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class DeliveryCompletedTest {

    @Autowired
    DeliveryRepository deliveryRepository;

    @Test
    void deliveryShouldBeCompleted() {
        Delivery delivery = deliveryRepository.findByOrderId("1");
        assertNotNull(delivery, "Delivery should not be null");
        assertEquals("Completed", delivery.getStatus());
    }
}
