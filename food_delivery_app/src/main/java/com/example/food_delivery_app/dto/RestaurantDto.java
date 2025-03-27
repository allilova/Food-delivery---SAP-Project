package com.example.food_delivery_app.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.util.List;

@Data
@Embeddable
public class RestaurantDto {
    private Long id;

    private String restaurantName;

    private String restaurantAddress;

    private String email;

    @Column(length = 1000)
    private List<String> image;

    private String description;
}
