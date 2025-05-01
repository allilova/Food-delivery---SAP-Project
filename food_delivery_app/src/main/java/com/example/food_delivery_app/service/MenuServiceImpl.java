package com.example.food_delivery_app.service;

import com.example.food_delivery_app.model.Food;
import com.example.food_delivery_app.model.Menu;
import com.example.food_delivery_app.model.Restaurant;
import com.example.food_delivery_app.repository.FoodRepository;
import com.example.food_delivery_app.repository.MenuRepository;
import com.example.food_delivery_app.repository.RestaurantRepository;
import com.example.food_delivery_app.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MenuServiceImpl implements MenuService {

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private FoodRepository foodRepository;

    @Override
    public Menu getMenuByRestaurant(Long restaurantId) {
        return menuRepository.findByRestaurantId(restaurantId)
                .orElseThrow(() -> new RuntimeException("Menu not found for restaurant id: " + restaurantId));
    }

    @Override
    public Menu createMenu(Long restaurantId, Menu menuRequest) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with id: " + restaurantId));

        menuRequest.setRestaurant(restaurant);
        return menuRepository.save(menuRequest);
    }

    @Override
    public Menu updateMenu(Long menuId, Menu menuRequest) {
        Menu existingMenu = menuRepository.findById(menuId)
                .orElseThrow(() -> new RuntimeException("Menu not found with id: " + menuId));

        existingMenu.setCategoryName(menuRequest.getCategoryName());
        existingMenu.getCategories().clear();
        if (menuRequest.getCategories() != null) {
            existingMenu.getCategories().addAll(menuRequest.getCategories());
        }
        return menuRepository.save(existingMenu);
    }

    @Override
    public void deleteMenu(Long menuId) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new RuntimeException("Menu not found with id: " + menuId));

        menuRepository.delete(menu);
    }

    @Override
    public Menu addFoodToMenu(Long menuId, Long foodId) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new RuntimeException("Menu not found with id: " + menuId));
        Food food = foodRepository.findById((long) foodId)
                .orElseThrow(() -> new RuntimeException("Food not found with id: " + foodId));

        menu.getFoods().add(food);
        return menuRepository.save(menu);
    }

    @Override
    public Menu removeFoodFromMenu(Long menuId,Long foodId) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new RuntimeException("Menu not found with id: " + menuId));
        Food food = foodRepository.findById((long) foodId)
                .orElseThrow(() -> new RuntimeException("Food not found with id: " + foodId));

        menu.getFoods().remove(food);
        return menuRepository.save(menu);
    }

    @Override
    public Menu getMenuById(Long menuId) throws Exception {
        return menuRepository.findById(menuId)
                .orElseThrow(() -> new Exception("Menu not found with id: " + menuId));
    }

}
