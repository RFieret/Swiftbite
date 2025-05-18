package com.e2e.tests;

import com.rabbitmq.client.*;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import org.json.JSONObject;
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

    String OrderID = "1";

    private String getAuth0AccessToken() throws Exception {
        String authDomain = System.getenv("OAUTH2_JWT_ISSUER_URI");
        String clientId = System.getenv("AUTH0_CLIENT_ID");
        String clientSecret = System.getenv("AUTH0_CLIENT_SECRET");
        String audience = System.getenv("OAUTH2_JWT_AUDIENCES"); // Must match API audience in Auth0

        System.out.println("Auth0 domain: " + authDomain);
        System.out.println("Auth0 client ID: " + clientId);
        System.out.println("Auth0 client secret: " + clientSecret);
        System.out.println("Auth0 audience: " + audience);

        String tokenUrl = authDomain + "/oauth/token";

        System.out.println("Token URL: " + tokenUrl);

        String requestBody = """
        {
          "client_id": "%s",
          "client_secret": "%s",
          "audience": "%s",
          "grant_type": "client_credentials"
        }
        """.formatted(clientId, clientSecret, audience);

        System.out.println("Request body: " + requestBody);

        HttpRequest tokenRequest = HttpRequest.newBuilder()
                .uri(URI.create(tokenUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(tokenRequest, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to get Auth0 token: " + response.body());
        }

        // Extract access_token from the response
        String responseBody = response.body();

        return new JSONObject(responseBody).getString("access_token");
    }

    @Test
    public void testOrderDeliveryFlow() throws Exception {

        // 1. Get Kong Gateway URL
        String kongGatewayUrl = "http://kong:8000";

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        // 2. Step 1: Create a delivery through Kong API Gateway -> OrderService
        String orderJson = """
                {
                    "id": "%s",
                    "customerName": "Michael",
                    "address": "dsadas",
                    "status": "Created"
                  }
                """.formatted(OrderID);

        String token = getAuth0AccessToken();

        HttpRequest createDeliveryRequest = HttpRequest.newBuilder()
                .uri(URI.create(kongGatewayUrl + "/orders/api/orderService/createDelivery"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString(orderJson))
                .build();

        HttpResponse<String> createResponse = client.send(createDeliveryRequest, HttpResponse.BodyHandlers.ofString());

        // Verify the initial response
        assertEquals(200, createResponse.statusCode(), "Initial createDelivery request should be accepted");

//        // Parse the delivery ID from the response
//        JsonObject responseJson;
//        try (JsonReader jsonReader = Json.createReader(new StringReader(createResponse.body()))) {
//            responseJson = jsonReader.readObject();
//        }
//
//        System.out.println(responseJson);

        // Get created delivery from order ID
        HttpRequest getDeliveryIDbyOrder = HttpRequest.newBuilder()
                .uri(URI.create(kongGatewayUrl + "/deliveries/api/deliveryservice/deliveryByOrder/"+OrderID))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        HttpResponse<String> getResponse = client.send(getDeliveryIDbyOrder, HttpResponse.BodyHandlers.ofString());

        // Parse the delivery ID from the response
        JsonObject responseJson;
        try (JsonReader jsonReader = Json.createReader(new StringReader(getResponse.body()))) {
            responseJson = jsonReader.readObject();
        }

        System.out.println(responseJson);
        String DeliveryId = responseJson.getString("id");

//        // 4. Step 2: Verify the delivery was created through Kong -> DeliveryService
//        HttpRequest getDeliveryRequest = HttpRequest.newBuilder()
//                .uri(URI.create(kongGatewayUrl + "/deliveries/api/deliveryservice/deliveryByOrder" + DeliveryId))
//                .GET()
//                .build();
//
//        HttpResponse<String> getResponse = client.send(getDeliveryRequest, HttpResponse.BodyHandlers.ofString());
//
//        assertEquals(200, getResponse.statusCode(), "Should be able to get delivery");
//
        // 5. Step 3: Assign a driver to the delivery through Kong -> DeliveryService
        String assignDriverJson = """
                {
                    "deliveryId": "%s",
                    "deliverer": {
                        "name": "James Smith",
                        "phone": "555-123-4567"
                    }
                }
                """.formatted(DeliveryId);

        HttpRequest assignDriverRequest = HttpRequest.newBuilder()
                .uri(URI.create(kongGatewayUrl + "/deliveries/api/deliveryservice/assignDeliverer"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString(assignDriverJson))
                .build();

        HttpResponse<String> assignResponse = client.send(assignDriverRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, assignResponse.statusCode(), "Should be able to assign driver");

        // 6. Step 4: Complete the delivery through Kong -> DeliveryService
        HttpRequest completeDeliveryRequest = HttpRequest.newBuilder()
                .uri(URI.create(kongGatewayUrl + "/deliveries/api/deliveryservice/completeDelivery/" + DeliveryId))
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> completeResponse = client.send(completeDeliveryRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, completeResponse.statusCode(), "Should be able to complete delivery");
//
//        // 7. Wait for the completion message through RabbitMQ (with timeout)
//        boolean completionReceived = completionLatch.await(30, TimeUnit.SECONDS);
//        assertTrue(completionReceived, "Didn't receive delivery completion message within timeout");
//
//        // 8. Verify the delivery was processed correctly
//        assertEquals(initialDeliveryId, deliveryId[0], "Delivery ID should match");
//        assertEquals("DELIVERED", orderStatus[0], "Final status should be DELIVERED");
        // 8. Verify the delivery was processed correctly
        Thread.sleep(5000);

        HttpRequest checkHandler = HttpRequest.newBuilder()
                .uri(URI.create(kongGatewayUrl + "/orders/api/orderService/order-received/"+OrderID))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        HttpResponse<String> response = client.send(checkHandler, HttpResponse.BodyHandlers.ofString());
        assertEquals("Order Received: "+ OrderID, response.body());
    }
}
