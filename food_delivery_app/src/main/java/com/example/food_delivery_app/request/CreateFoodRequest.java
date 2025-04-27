package com.example.food_delivery_app.request;

import com.example.food_delivery_app.model.Category;
import com.example.food_delivery_app.model.IngredientsItem;
import com.example.food_delivery_app.model.Restaurant;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateFoodRequest {
    private String foodName;
    private String foodDescription;
    private String foodImage;
    private BigDecimal foodPrice;
    private Long restaurantId;
    private int preparationTime;
    private Long categoryId;
    private List<Long> ingredientIds;
    private boolean available;
    private Long menuId;
}
