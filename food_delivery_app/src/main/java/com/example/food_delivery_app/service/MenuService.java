package com.example.food_delivery_app.service;

import com.example.food_delivery_app.model.Menu;

public interface MenuService {
    Menu getMenuByRestaurant(Long restaurantId);
    Menu createMenu(Long restaurantId, Menu menu);
    Menu updateMenu(Long menuId, Menu menu);
    void deleteMenu(Long menuId);
    Menu addFoodToMenu(Long menuId, Long foodId);
    Menu removeFoodFromMenu(Long menuId, Long foodId);
    Menu getMenuById(Long menuId) throws Exception;

}
