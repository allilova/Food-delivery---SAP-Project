package com.example.food_delivery_app.response;

import com.example.food_delivery_app.model.USER_ROLE;
import lombok.Data;

@Data
public class AuthResponse {
    private String jwt;

    private String message;

    private USER_ROLE role;
}
