package com.example.food_delivery_app.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.List;

@Data
public class CreateOrderRequest {
    @NotNull(message = "Restaurant ID is required")
    private Long restaurantId;

    @Valid
    @NotNull(message = "Order items are required")
    @Size(min = 1, message = "At least one item is required")
    private List<OrderItemRequest> items;

    @NotBlank(message = "Delivery address is required")
    private String deliveryAddress;

    @NotBlank(message = "Contact number is required")
    private String contactNumber;

    private String specialInstructions;

    @Data
    public static class OrderItemRequest {
        @NotNull(message = "Food ID is required")
        private Long foodId;

        @NotNull(message = "Quantity is required")
        private Integer quantity;
    }
} 