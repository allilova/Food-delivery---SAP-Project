package com.example.food_delivery_app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO for restaurant data to be sent to clients
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantResponseDto {
    private Long id;
    private String name;
    private String imgUrl;
    private String address;
    private Double rating;
    private String foodType;
    private String timeDelivery;
    private List<String> menu = new ArrayList<>();
    private boolean open;
    private String openingHours;
    private String closingHours;
    private String phone;
    private String email;
    private boolean isFavorite;
}