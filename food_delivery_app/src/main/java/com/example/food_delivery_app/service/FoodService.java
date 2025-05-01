package com.example.food_delivery_app.service;

import com.example.food_delivery_app.dto.FoodResponseDto;
import com.example.food_delivery_app.model.Category;
import com.example.food_delivery_app.model.Food;
import com.example.food_delivery_app.model.Menu;
import com.example.food_delivery_app.model.Restaurant;
import com.example.food_delivery_app.request.CreateFoodRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FoodService {

    public Food createFood(CreateFoodRequest req, Category category, Menu menu);

    Food deleteFood(Long foodId) throws Exception;

    public Page<Food> getMenuFood(Menu menu, String foodCategory, Pageable pageable);

    public List<Food> getFoodByCategory(Category category);

    public Page<Food> searchFood(String keyword, Pageable pageable);

    public Food findById(Long id) throws Exception;

    public Food updateAvailabilityStatus (Long foodId) throws Exception;

    public FoodResponseDto convertToDto(Food food);

    public List<Food> searchFood(String foodName);

    public List<Food> getMenuFood(Menu menu, String foodCategory);
}
