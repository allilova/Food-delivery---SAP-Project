package com.example.food_delivery_app.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderItemRequest {
    @NotNull(message = "Food ID is required")
    private Long foodId;
    
    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;
} 