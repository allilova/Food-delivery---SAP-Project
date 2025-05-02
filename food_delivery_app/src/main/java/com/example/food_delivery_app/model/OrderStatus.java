package com.example.food_delivery_app.model;

public enum OrderStatus {
    PENDING,          // New order, not yet confirmed
    CONFIRMED,        // Order confirmed by restaurant
    PREPARING,        // Order is being prepared
    READY,            // Order is ready for pickup
    OUT_FOR_DELIVERY, // Order is being delivered
    DELIVERED,        // Order has been delivered
    CANCELLED,        // Order has been cancelled
    
    // For backward compatibility with existing code
    IN_PROGRESS       // General status for active orders (being processed)
}
