package com.example.food_delivery_app.service;

import com.example.food_delivery_app.model.Category;
import com.example.food_delivery_app.model.Food;
import com.example.food_delivery_app.model.Menu;
import com.example.food_delivery_app.model.Restaurant;
import com.example.food_delivery_app.request.CreateFoodRequest;

import java.util.List;

public interface FoodService {

    public Food createFood(CreateFoodRequest req, Category category, Menu menu);

    Food deleteFood(Long foodId) throws Exception;

    public List<Food> getMenuFood(Menu menu, String foodCategory);

    public List<Food> getFoodByCategory(Category category);

    public List<Food>searchFood(String keyword);

    public Food findById(Long id) throws Exception;

    public Food updateAvailabilityStatus (Long foodId) throws Exception;
}
