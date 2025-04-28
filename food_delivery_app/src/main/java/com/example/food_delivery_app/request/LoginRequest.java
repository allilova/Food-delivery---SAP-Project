package com.example.food_delivery_app.request;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}