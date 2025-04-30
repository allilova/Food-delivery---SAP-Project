package com.example.food_delivery_app.controller;

import com.example.food_delivery_app.model.*;
import com.example.food_delivery_app.repository.CategoryRepository;
import com.example.food_delivery_app.request.CreateFoodRequest;
import com.example.food_delivery_app.response.AuthResponse;
import com.example.food_delivery_app.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/admin/food")
public class AdminFoodController {

    @Autowired
    private FoodService foodService;

    @Autowired
    private UserService userService;

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private MenuService menuService;

    @Autowired
    private CategoryRepository categoryRepository;

    @PostMapping
    public ResponseEntity<Food> createFood(@RequestBody CreateFoodRequest req,
                                           @RequestHeader("Authorization") String jwtT) throws Exception {
        User user = userService.findUserByJwtToken(jwtT);
        Restaurant restaurant = restaurantService.findById(req.getRestaurantId());
        Menu menu = menuService.getMenuById(req.getMenuId());
        Category category = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Food food = foodService.createFood(req, category, menu);
        return new ResponseEntity<>(food, HttpStatus.CREATED);
    }

    @DeleteMapping ("/{foodId}")
    public ResponseEntity<AuthResponse.MessageResponse> deleteFood(@PathVariable Long foodId,
                                                                   @RequestHeader("Authorization") String jwtT) throws Exception {
        User user = userService.findUserByJwtToken(jwtT);

        foodService.deleteFood(foodId);

        AuthResponse.MessageResponse res = new AuthResponse.MessageResponse();
        res.setMessage("Successfully deleted food");
        return  new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PutMapping("/{foodId}")
    public ResponseEntity<Food> updateFoodAvailability(@PathVariable Long foodId,
                                                      @RequestHeader("Authorization") String jwtT) throws Exception {
        User user = userService.findUserByJwtToken(jwtT);

        Food food = foodService.updateAvailabilityStatus(foodId);

        return  new ResponseEntity<>(food, HttpStatus.OK);
    }

}
