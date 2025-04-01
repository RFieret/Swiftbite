package com.swiftbite.services.orderService.config;

public class RabbitMQConstants {
    public static final String EXCHANGE = "order.exchange";

    public static final String ORDER_PLACED_ROUTING_KEY = "order.placed";
    public static final String ORDER_CREATE_DELIVERY_ROUTING_KEY = "order.createDelivery";
    public static final String ORDER_COMPLETED_ROUTING_KEY = "order.completed";

    public static final String QUEUE = "order.queue";
}
