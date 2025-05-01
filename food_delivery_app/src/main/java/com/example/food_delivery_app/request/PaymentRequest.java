package com.example.food_delivery_app.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentRequest {
    @NotNull
    private Long orderId;
    
    @NotNull
    private String paymentMethod;
    
    @NotNull
    private Double amount;
} 