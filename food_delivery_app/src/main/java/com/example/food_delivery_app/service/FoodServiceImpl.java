package com.example.food_delivery_app.service;

import com.example.food_delivery_app.dto.FoodResponseDto;
import com.example.food_delivery_app.model.Category;
import com.example.food_delivery_app.model.Food;
import com.example.food_delivery_app.model.Menu;
import com.example.food_delivery_app.repository.FoodRepository;
import com.example.food_delivery_app.repository.IngredientsItemRepository;
import com.example.food_delivery_app.request.CreateFoodRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
<<<<<<< HEAD
        food.setDescription(req.getFoodDescription());
        food.setName(req.getFoodName());
        food.setPrice(req.getFoodPrice().doubleValue());
        food.setRestaurant(menu.getRestaurant());
        food.setIsAvailable(true);
=======
        food.setFoodDescription(req.getFoodDescription());
        food.setFoodName(req.getFoodName());
        food.setFoodPrice(req.getFoodPrice());
        food.setMenu(menu);
        food.setPreparationTime(req.getPreparationTime());
        food.setAvailable(true);
>>>>>>> 3b97e188d54bd0a20c3391ce1ad1a3d3dc0fb7ca
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
    public Page<Food> getMenuFood(Menu menu, String foodCategory, Pageable pageable) {
        if(foodCategory != null && !foodCategory.equals("")){
            return foodRepository.findByMenuAndCategory_CategoryName(menu, foodCategory, pageable);
        }
        return foodRepository.findByMenu(menu, pageable);
    }

    @Override
    public List<Food> getFoodByCategory(Category category) {
        return foodRepository.findByCategory(category);
    }

    @Override
    public Page<Food> searchFood(String keyword, Pageable pageable) {
        return foodRepository.searchFood(keyword, pageable);
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
<<<<<<< HEAD
<<<<<<< HEAD
        Food food = getFoodById(foodId);
        food.setIsAvailable(!food.getIsAvailable());
=======
=======
>>>>>>> 1db6b8e08a5a54e3b88a36b81c018b3860e2aaf5
        Food food = findById(foodId);
        food.setAvailable(!food.isAvailable());
>>>>>>> 3b97e188d54bd0a20c3391ce1ad1a3d3dc0fb7ca
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
