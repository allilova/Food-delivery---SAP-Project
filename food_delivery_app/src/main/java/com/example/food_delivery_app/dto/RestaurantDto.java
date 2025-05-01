package com.example.food_delivery_app.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * Lightweight DTO for storing restaurant data in user favorites
 */
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
    private List<String> images = new ArrayList<>();
    
    private String foodType;
    
    /**
     * Creates a deep copy of this DTO
     * @return A new instance with the same properties
     */
    public RestaurantDto copy() {
        RestaurantDto copy = new RestaurantDto();
        copy.setId(this.id);
        copy.setRestaurantName(this.restaurantName);
        copy.setAddress(this.address);
        copy.setEmail(this.email);
        copy.setFoodType(this.foodType);
        
        if (this.images != null) {
            copy.setImages(new ArrayList<>(this.images));
        }
        
        return copy;
    }
}