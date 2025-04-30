package com.example.food_delivery_app.service;

import com.example.food_delivery_app.dto.FoodResponseDto;
import com.example.food_delivery_app.model.Category;
import com.example.food_delivery_app.model.Food;
import com.example.food_delivery_app.model.Menu;
import com.example.food_delivery_app.repository.FoodRepository;
import com.example.food_delivery_app.repository.IngredientsItemRepository;
import com.example.food_delivery_app.request.CreateFoodRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        food.setMenu(menu);
        food.setPreparationTime(req.getPreparationTime());
        food.setAvailable(true);
        Food savedFood = foodRepository.save(food);
        menu.getFoods().add(savedFood);
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
    public Food findById(Long id) throws Exception {
        Optional<Food> optionalFood = foodRepository.findById(id);

        if (optionalFood.isEmpty()) {
            throw new Exception("Food not found...");
        }
        return optionalFood.get();
    }

    @Override
    public Food updateAvailabilityStatus(Long foodId) throws Exception {
        Food food = findById(foodId);
        food.setAvailable(!food.isAvailable());
        return foodRepository.save(food);
    }
    public FoodResponseDto convertToDto(Food food) {
        FoodResponseDto dto = new FoodResponseDto();
        dto.setId(food.getId());
        dto.setFoodName(food.getFoodName());
        dto.setFoodDescription(food.getFoodDescription());
        dto.setFoodImage(food.getFoodImage());
        dto.setFoodPrice(food.getFoodPrice());
        dto.setPreparationTime(food.getPreparationTime());
        dto.setAvailable(food.isAvailable());

        if (food.getCategory() != null) {
            dto.setCategoryName(food.getCategory().getCategoryName());
        }

        if (food.getIngredients() != null) {
            List<String> ingredientNames = food.getIngredients()
                    .stream()
                    .map(i -> i.getIngredientName())
                    .toList();
            dto.setIngredients(ingredientNames);
        }

        return dto;
    }

}
