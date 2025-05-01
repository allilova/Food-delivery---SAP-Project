package com.example.food_delivery_app.exception;

public class FoodNotFoundException extends RuntimeException {
    public FoodNotFoundException(String message) {
        super(message);
    }
} 