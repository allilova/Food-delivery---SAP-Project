package com.example.food_delivery_app.controller;

import com.example.food_delivery_app.model.Menu;
import com.example.food_delivery_app.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/menus")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<Menu> getMenuByRestaurant(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(menuService.getMenuByRestaurant(restaurantId));
    }

    @PostMapping("/restaurant/{restaurantId}")
    public ResponseEntity<Menu> createMenu(@PathVariable Long restaurantId, @RequestBody Menu menuRequest) {
        return ResponseEntity.ok(menuService.createMenu(restaurantId, menuRequest));
    }

    @PutMapping("/{menuId}")
    public ResponseEntity<Menu> updateMenu(@PathVariable Long menuId, @RequestBody Menu menuRequest) {
        return ResponseEntity.ok(menuService.updateMenu(menuId, menuRequest));
    }

    @DeleteMapping("/{menuId}")
    public ResponseEntity<String> deleteMenu(@PathVariable Long menuId) {
        menuService.deleteMenu(menuId);
        return ResponseEntity.ok("Menu deleted successfully.");
    }

    @PostMapping("/{menuId}/foods/{foodId}")
    public ResponseEntity<Menu> addFoodToMenu(@PathVariable Long menuId, @PathVariable int foodId) {
        return ResponseEntity.ok(menuService.addFoodToMenu(menuId, foodId));
    }

    @DeleteMapping("/{menuId}/foods/{foodId}")
    public ResponseEntity<Menu> removeFoodFromMenu(@PathVariable Long menuId, @PathVariable int foodId) {
        return ResponseEntity.ok(menuService.removeFoodFromMenu(menuId, foodId));
    }
}
