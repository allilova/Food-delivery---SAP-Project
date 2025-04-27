package com.example.food_delivery_app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantResponseDto {
    private String restaurantId;
    private String name;
    private String imgUrl;
    private String address;
    private double rating;
    private String foodType;
    private String timeDelivery;
    private List<String> menu;
}