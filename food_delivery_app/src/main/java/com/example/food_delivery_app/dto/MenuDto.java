package com.example.food_delivery_app.dto;

import lombok.Data;
import java.util.List;

@Data
public class MenuDto {
    private Long id;
    private String categoryName;
    private List<Long> foodIds; // Only IDs of foods, not full food objects
}
