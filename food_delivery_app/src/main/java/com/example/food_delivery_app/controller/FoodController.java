package com.example.food_delivery_app.controller;

import com.example.food_delivery_app.model.Food;
import com.example.food_delivery_app.model.Menu;
import com.example.food_delivery_app.model.User;
import com.example.food_delivery_app.service.FoodService;
import com.example.food_delivery_app.service.RestaurantServiceImpl;
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
    private RestaurantServiceImpl restaurantServiceImpl;

    @GetMapping ("/search")
    public ResponseEntity<List<Food>> searchFood(@RequestParam String foodName,
                                           @RequestHeader("Authorization") String jwtT) throws Exception {
        User user = userService.findUserByJwtToken(jwtT);

        List<Food> foods = foodService.searchFood(foodName);
        return  new ResponseEntity<>(foods, HttpStatus.OK);

    }
    @GetMapping ("/menu/{menuId}")
    public ResponseEntity<List<Food>> getMenuFood(@PathVariable Menu menu,
                                                 @RequestParam(required = false) String food_category,
                                                 @RequestHeader("Authorization") String jwtT) throws Exception {
        User user = userService.findUserByJwtToken(jwtT);

        List<Food> foods = foodService.getMenuFood(menu, food_category);
        return  new ResponseEntity<>(foods, HttpStatus.OK);

    }

}
