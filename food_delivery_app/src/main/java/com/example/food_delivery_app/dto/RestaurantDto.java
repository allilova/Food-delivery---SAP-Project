package com.example.food_delivery_app.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Embeddable
public class RestaurantDto {
    @EqualsAndHashCode.Include
    private Long id;

    private String restaurantName;

    private String address;

    private String email;

    @Column(length = 1000)
    private List<String> images;

}
