package com.example.food_delivery_app.controller;

import com.example.food_delivery_app.dto.FoodResponseDto;
import com.example.food_delivery_app.model.Food;
import com.example.food_delivery_app.model.Menu;
import com.example.food_delivery_app.model.User;
import com.example.food_delivery_app.service.FoodService;
import com.example.food_delivery_app.service.MenuService;
import com.example.food_delivery_app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/food")
public class FoodController {
    @Autowired
    private FoodService foodService;

    @Autowired
    private UserService userService;

    @Autowired
    private MenuService menuService;


    @GetMapping("/search")
    public ResponseEntity<List<FoodResponseDto>> searchFood(@RequestParam String foodName,
                                                            @RequestHeader("Authorization") String jwt) throws Exception {
        userService.findUserByJwtToken(jwt);
        List<Food> foods = foodService.searchFood(foodName);

        List<FoodResponseDto> response = foods.stream()
                .map(food -> foodService.convertToDto(food)) // ще трябва да направиш convertToDto публичен
                .toList();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/menu/{menuId}")
    public ResponseEntity<List<FoodResponseDto>> getMenuFood(@PathVariable Long menuId,
                                                             @RequestParam(required = false) String foodCategory,
                                                             @RequestHeader("Authorization") String jwt) throws Exception {
        userService.findUserByJwtToken(jwt);
        Menu menu = menuService.getMenuById(menuId);

        List<Food> foods = foodService.getMenuFood(menu, foodCategory);
        List<FoodResponseDto> response = foods.stream()
                .map(food -> foodService.convertToDto(food))
                .toList();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
