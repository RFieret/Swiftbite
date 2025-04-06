package com.swiftbite.services.orderService.e2e;

import org.junit.jupiter.api.Test;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.URI;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.UUID;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OrderDeliveryE2ETest {

    private final String baseUrl = System.getenv().getOrDefault("BASE_URL", "http://localhost:8000");
    private final HttpClient client = HttpClient.newHttpClient();

    @Test
    public void fullOrderToDeliveryFlow() throws Exception {
        Thread.sleep(10_000);

        String orderId = UUID.randomUUID().toString();
        String orderJson = """
        {
          "id": "%s",
          "customerName": "Jane Doe",
          "address": "Pakistan",
          "status": "Payed"
        }
        """.formatted(orderId);


        // Step 1: Trigger delivery creation
        sendPostJson(baseUrl + "/api/orderService/createDelivery", orderJson);

        // Step 2: Wait for DeliveryService to create delivery
        String deliveryId = waitForDeliveryId(orderId);

        // Step 3: Assign a deliverer
        String assignJson = """
        {
            "deliveryId": "%s",
            "deliverer": "alice"
        }
        """.formatted(deliveryId);

        sendPostJson(baseUrl + "/deliveries/assign-deliverer", assignJson);

        // Step 4: Complete the delivery
        String completeJson = """
        {
            "deliveryId": "%s"
        }
        """.formatted(deliveryId);

        sendPostJson(baseUrl + "/deliveries/complete-delivery", completeJson);

        // Step 5: Wait for order to be marked as DELIVERED
        boolean delivered = waitFor(() -> {
            try {
                return isOrderDelivered(orderId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, Duration.ofSeconds(15));
        assertTrue(delivered, "Order was not marked as delivered");
    }


    private void sendPostJson(String url, String json) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Failed POST to " + url + " with body: " + json);
    }

    private String waitForDeliveryId(String orderId) throws Exception {
        for (int i = 0; i < 20; i++) {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/deliveries/delivery-by-order/" + orderId))
                    .GET()
                    .build();

            HttpResponse<String> res = HttpClient.newHttpClient().send(req, HttpResponse.BodyHandlers.ofString());
            if (res.statusCode() == 200 && res.body().contains("deliveryId")) {
                return extractDeliveryId(res.body());
            }
            Thread.sleep(500);
        }
        throw new RuntimeException("Delivery not created in time");
    }

    private boolean isOrderDelivered(String orderId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/orders/" + orderId))
                .GET()
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode() == 200 && response.body().contains("\"status\":\"DELIVERED\"");
    }

    private boolean waitFor(Supplier<Boolean> condition, Duration timeout) throws InterruptedException {
        long end = System.currentTimeMillis() + timeout.toMillis();
        while (System.currentTimeMillis() < end) {
            if (condition.get()) return true;
            Thread.sleep(300);
        }
        return false;
    }

    private String extractDeliveryId(String json) {
        // Very basic JSON parsing — recommend replacing with Jackson later
        return json.replaceAll(".*\"deliveryId\"\\s*:\\s*\"(.*?)\".*", "$1");
    }
}
