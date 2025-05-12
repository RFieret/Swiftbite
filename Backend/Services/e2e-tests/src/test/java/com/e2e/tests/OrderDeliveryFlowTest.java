package com.e2e.tests;

import com.rabbitmq.client.*;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.File;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Tag;

@Testcontainers
@Tag("e2e")
public class OrderDeliveryFlowTest {

    private static final String DELIVERY_QUEUE = "delivery.queue";
    private static Connection rabbitConnection;
    private static Channel rabbitChannel;

    @BeforeAll
    public static void setup() throws Exception {
        // Docker Compose is started automatically by Testcontainers
        System.out.println("Started rabbitmq");

        // Wait for all containers to be up and healthy
        Thread.sleep(5000);

        // Setup RabbitMQ connection for test verification
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");

        rabbitConnection = factory.newConnection();
        rabbitChannel = rabbitConnection.createChannel();

        // Ensure queues are declared
        rabbitChannel.queueDeclare(DELIVERY_QUEUE, true, false, false, null);

        // Additional setup if needed
        Thread.sleep(2000);
    }

    @AfterAll
    public static void tearDown() throws Exception {
        if (rabbitChannel != null && rabbitChannel.isOpen()) {
            rabbitChannel.close();
        }
        if (rabbitConnection != null && rabbitConnection.isOpen()) {
            rabbitConnection.close();
        }
    }

    @Test
    public void testOrderDeliveryFlow() throws Exception {

        // 1. Get Kong Gateway URL
        String kongGatewayUrl = "http://localhost:8000";

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        // 2. Step 1: Create a delivery through Kong API Gateway -> OrderService
        String orderJson = """
                {
                    "id": "1",
                    "customerName": "Michael",
                    "address": "dsadas",
                    "status": "Created"
                  }
                """;

        HttpRequest createDeliveryRequest = HttpRequest.newBuilder()
                .uri(URI.create(kongGatewayUrl + "/orders/api/orderService/createDelivery"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(orderJson))
                .build();

        HttpResponse<String> createResponse = client.send(createDeliveryRequest, HttpResponse.BodyHandlers.ofString());

        // Verify the initial response
        assertEquals(202, createResponse.statusCode(), "Initial createDelivery request should be accepted");

//        // Parse the delivery ID from the response
//        JsonObject responseJson;
//        try (JsonReader jsonReader = Json.createReader(new StringReader(createResponse.body()))) {
//            responseJson = jsonReader.readObject();
//        }
//
//        System.out.println(responseJson);
//
//        String initialDeliveryId = responseJson.getString("id");
//        Assertions.assertNotNull("Initial response should contain a delivery ID", initialDeliveryId);
//
//        // 4. Step 2: Verify the delivery was created through Kong -> DeliveryService
//        HttpRequest getDeliveryRequest = HttpRequest.newBuilder()
//                .uri(URI.create(kongGatewayUrl + "/deliveries/api/deliveryservice/deliveryByOrder" + initialDeliveryId))
//                .GET()
//                .build();
//
//        HttpResponse<String> getResponse = client.send(getDeliveryRequest, HttpResponse.BodyHandlers.ofString());
//
//        assertEquals(200, getResponse.statusCode(), "Should be able to get delivery");
//
//        // 5. Step 3: Assign a driver to the delivery through Kong -> DeliveryService
//        String assignDriverJson = """
//                {
//                    "deliveryId": "%s",
//                    "deliverer": {
//                        "name": "James Smith",
//                        "phone": "555-123-4567"
//                    }
//                }
//                """.formatted(initialDeliveryId);
//
//        HttpRequest assignDriverRequest = HttpRequest.newBuilder()
//                .uri(URI.create(kongGatewayUrl + "/deliveries/api/deliveryservice/assignDeliverer"))
//                .header("Content-Type", "application/json")
//                .POST(HttpRequest.BodyPublishers.ofString(assignDriverJson))
//                .build();
//
//        HttpResponse<String> assignResponse = client.send(assignDriverRequest, HttpResponse.BodyHandlers.ofString());
//
//        assertEquals(200, assignResponse.statusCode(), "Should be able to assign driver");
//
//        // 6. Step 4: Complete the delivery through Kong -> DeliveryService
//        HttpRequest completeDeliveryRequest = HttpRequest.newBuilder()
//                .uri(URI.create(kongGatewayUrl + "/deliveries/api/deliveryservice/completeDelivery/" + initialDeliveryId))
//                .POST(HttpRequest.BodyPublishers.noBody())
//                .build();
//
//        HttpResponse<String> completeResponse = client.send(completeDeliveryRequest, HttpResponse.BodyHandlers.ofString());
//
//        assertEquals(200, completeResponse.statusCode(), "Should be able to complete delivery");
//
//        // 7. Wait for the completion message through RabbitMQ (with timeout)
//        boolean completionReceived = completionLatch.await(30, TimeUnit.SECONDS);
//        assertTrue(completionReceived, "Didn't receive delivery completion message within timeout");
//
//        // 8. Verify the delivery was processed correctly
//        assertEquals(initialDeliveryId, deliveryId[0], "Delivery ID should match");
//        assertEquals("DELIVERED", orderStatus[0], "Final status should be DELIVERED");
    }
}
