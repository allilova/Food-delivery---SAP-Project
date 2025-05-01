package com.example.food_delivery_app.service;

import com.example.food_delivery_app.dto.FoodResponseDto;
import com.example.food_delivery_app.exception.ResourceNotFoundException;
import com.example.food_delivery_app.model.Category;
import com.example.food_delivery_app.model.Food;
import com.example.food_delivery_app.model.IngredientsItem;
import com.example.food_delivery_app.model.Menu;
import com.example.food_delivery_app.repository.FoodRepository;
import com.example.food_delivery_app.repository.IngredientsItemRepository;
import com.example.food_delivery_app.request.CreateFoodRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FoodServiceImpl implements FoodService {
    
    @Autowired
    private FoodRepository foodRepository;

    @Autowired
    private IngredientsItemRepository ingredientsItemRepository;


    @Override
    public Food createFood(CreateFoodRequest req, Category category, Menu menu) {
        Food food = new Food();

        food.setCategory(category);
        food.setDescription(req.getFoodDescription());
        food.setName(req.getFoodName());
        food.setPrice(req.getFoodPrice().doubleValue());
        food.setImageUrl(req.getFoodImage());
        food.setMenu(menu);
        food.setIsAvailable(true);
        
        // Create a list to store ingredients if needed
        List<IngredientsItem> ingredients = new ArrayList<>();
        
        // If ingredient IDs are provided, fetch and add them
        if (req.getIngredientIds() != null && !req.getIngredientIds().isEmpty()) {
            for (Long ingredientId : req.getIngredientIds()) {
                ingredientsItemRepository.findById(ingredientId).ifPresent(ingredients::add);
            }
            food.setIngredients(ingredients);
        }
        
        Food savedFood = foodRepository.save(food);
        
        // Update menu's food list if menu exists
        if (menu != null && menu.getFoods() != null) {
            menu.getFoods().add(savedFood);
        }
        
        return savedFood;
    }

    @Override
    public Food deleteFood(Long foodId) throws Exception {
        Food food = findById(foodId);
        foodRepository.delete(food);
        return food;
    }

    @Override
    public List<Food> getMenuFood(Menu menu, String foodCategory) {
        List<Food> foods = foodRepository.findByMenu(menu);

        if (foodCategory != null && !foodCategory.isEmpty()) {
            foods = filterByCategory(foods, foodCategory);
        }
        return foods;
    }
    
    @Override
    public Page<Food> getMenuFood(Menu menu, String foodCategory, Pageable pageable) {
        // This would need implementation with proper pageable repository methods
        // For now, we'll throw an exception indicating it's not implemented
        throw new UnsupportedOperationException("Pageable implementation of getMenuFood not yet implemented");
    }

    @Override
    public List<Food> getFoodByCategory(Category category) {
        return foodRepository.findByCategory(category);
    }

    private List<Food> filterByCategory(List<Food> foods, String foodCategory) {
        return foods.stream().filter(food -> {
            if (food.getCategory() != null) {
                return food.getCategory().getCategoryName().equals(foodCategory);
            }
            return false;
        }).collect(Collectors.toList());
    }

    @Override
    public List<Food> searchFood(String keyword) {
        return foodRepository.searchFood(keyword);
    }
    
    @Override
    public Page<Food> searchFood(String keyword, Pageable pageable) {
        // This would need implementation with proper pageable repository methods
        // For now, we'll throw an exception indicating it's not implemented
        throw new UnsupportedOperationException("Pageable implementation of searchFood not yet implemented");
    }

    @Override
    public Food findById(Long id) throws Exception {
        Optional<Food> optionalFood = foodRepository.findById(id);

        if (optionalFood.isEmpty()) {
            throw new ResourceNotFoundException("Food not found with id: " + id);
        }
        return optionalFood.get();
    }

    @Override
    public Food updateAvailabilityStatus(Long foodId) throws Exception {
        Food food = findById(foodId);
        food.setIsAvailable(!food.getIsAvailable());
        return foodRepository.save(food);
    }
    
    @Override
    public FoodResponseDto convertToDto(Food food) {
        FoodResponseDto dto = new FoodResponseDto();
        dto.setId(food.getId());
        dto.setFoodName(food.getName());
        dto.setFoodDescription(food.getDescription());
        dto.setFoodImage(food.getImageUrl());
        dto.setFoodPrice(BigDecimal.valueOf(food.getPrice()));
        dto.setAvailable(food.getIsAvailable());

        if (food.getCategory() != null) {
            dto.setCategoryName(food.getCategory().getCategoryName());
        }

        if (food.getIngredients() != null) {
            List<String> ingredientNames = food.getIngredients()
                    .stream()
                    .map(IngredientsItem::getIngredientName)
                    .collect(Collectors.toList());
            dto.setIngredients(ingredientNames);
        }

        return dto;
    }
}