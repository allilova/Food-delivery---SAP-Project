package com.example.food_delivery_app.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class FoodResponseDto {
    private Long id;
    private String foodName;
    private String foodDescription;
    private String foodImage;
    private BigDecimal foodPrice;
    private int preparationTime;
    private boolean available;
    private String categoryName;
    private List<String> ingredients;
}
