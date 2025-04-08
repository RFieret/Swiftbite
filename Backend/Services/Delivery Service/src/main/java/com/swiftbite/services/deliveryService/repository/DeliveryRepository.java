package com.swiftbite.services.deliveryService.repository;

import com.swiftbite.services.deliveryService.model.Delivery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface DeliveryRepository extends MongoRepository<Delivery, String> {
    @Query("{ 'deliverer.name': ?0, 'status': { $ne: 'Complete' } }")
    List<Delivery> findByDeliverer_Name(String name);

    Delivery findByOrderId(String orderId);
}
