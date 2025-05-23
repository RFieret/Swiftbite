package com.e2e.tests;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

        String tokenUrl = authDomain + "/oauth/token";

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
        HttpResponse<String> response;

        try {
            // Request Token
            response = client.send(tokenRequest, HttpResponse.BodyHandlers.ofString());

            // Detailed error output
            if (response.statusCode() != 200) {
                System.err.println("Auth0 token request failed with status code: " + response.statusCode());
                System.err.println("Response body: " + response.body());

                // Print all response headers for debugging
                response.headers().map().forEach((key, values) -> {
                    System.err.println(key + ": " + String.join(", ", values));
                });
                throw new RuntimeException("Failed to get Auth0 token: " + response.body());
            }
        } catch (Exception e) {
            System.err.println("Exception during Auth0 token request: " + e.getMessage());
            e.printStackTrace();
            throw e;
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

        // 2. Create a delivery through Kong API Gateway -> OrderService
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

        // 3. Get created delivery from order ID
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

        // 4. Assign a driver to the delivery through Kong -> DeliveryService
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

        // 5. Complete the delivery through Kong -> DeliveryService
        HttpRequest completeDeliveryRequest = HttpRequest.newBuilder()
                .uri(URI.create(kongGatewayUrl + "/deliveries/api/deliveryservice/completeDelivery/" + DeliveryId))
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> completeResponse = client.send(completeDeliveryRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, completeResponse.statusCode(), "Should be able to complete delivery");

        // 6. Wait for the message to arrive and Verify the delivery was processed correctly
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
