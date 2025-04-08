package com.swiftbite.services.deliveryService.E2EtestScripts;

import com.swiftbite.services.deliveryService.repository.DeliveryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class DelivererAssignedTest {

    @Autowired
    DeliveryRepository deliveryRepository;

    @Test
    void deliveryShouldHaveDriverAssigned() {
        var delivery = deliveryRepository.findByOrderId("123");
        assertNotNull(delivery, "Delivery should not be null");
        assertEquals("driver42", delivery.getDeliverer().getName());
    }
}
