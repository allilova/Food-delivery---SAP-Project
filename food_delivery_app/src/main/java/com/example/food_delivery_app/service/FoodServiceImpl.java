package com.example.food_delivery_app.service;

import com.example.food_delivery_app.model.Category;
import com.example.food_delivery_app.model.Food;
import com.example.food_delivery_app.model.Menu;
import com.example.food_delivery_app.model.Restaurant;
import com.example.food_delivery_app.repository.FoodRepository;
import com.example.food_delivery_app.repository.IngredientsItemRepository;
import com.example.food_delivery_app.request.CreateFoodRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        food.setFoodDescription(req.getFoodDescription());
        food.setFoodName(req.getFoodName());
        food.setFoodPrice(req.getFoodPrice());
        food.setMenu(menu.getRestaurant().getMenu());
        food.setPreparationTime(req.getPreparationTime());
        food.setAvailable(true);
        Food savedFood = foodRepository.save(food);
        menu.getFoods().add(savedFood);
        return savedFood;
    }

    @Override
    public void deleteFood(Long foodId) throws Exception {
        Food food = getFoodById(foodId);
        foodRepository.delete(food);
    }

    @Override
    public List<Food> getMenuFood(Long menuId, String foodCategory) {

        List<Food> foods = foodRepository.findByMenu(menuId);

        if(foodCategory != null && !foodCategory.equals("")){
            foods = filterByCategory(foods, foodCategory);
        }
        return foods;
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
    public Food getFoodById(Long id) throws Exception {
        Optional<Food> optionalFood = foodRepository.findById(id);

        if (optionalFood.isEmpty()) {
            throw new Exception("Food not found...");
        }
        return optionalFood.get();
    }

    @Override
    public Food updateAvailabilityStatus(Long foodId) throws Exception {
        Food food = getFoodById(foodId);
        food.setAvailable(!food.isAvailable());
        return foodRepository.save(food);
    }
}
