package com.example.food_delivery_app.controller;

import com.example.food_delivery_app.dto.MessageResponse;
import com.example.food_delivery_app.model.Food;
import com.example.food_delivery_app.model.Restaurant;
import com.example.food_delivery_app.model.User;
import com.example.food_delivery_app.request.CreateFoodRequest;
import com.example.food_delivery_app.service.FoodService;
import com.example.food_delivery_app.service.RestaurantService;
import com.example.food_delivery_app.service.UserService;
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

    @PostMapping
    public ResponseEntity<Food> createFood(@RequestBody CreateFoodRequest req,
                                           @RequestHeader("Authorization") String jwtT) throws Exception {
        User user = userService.findUserByJwtToken(jwtT);
        //Restaurant restaurant = restaurantService.getRestaurantById(req.getRestaurantId());
        //Menu menu = menuService.
        //Food food = foodService.createFood(req, req.getCategoryId(),menu);
        //return  new ResponseEntity<>(food, HttpStatus.CREATED);
        return null;
    }

    @DeleteMapping ("/{foodId}")
    public ResponseEntity<MessageResponse> deleteFood(@PathVariable Long foodId,
                                           @RequestHeader("Authorization") String jwtT) throws Exception {
        User user = userService.findUserByJwtToken(jwtT);

        foodService.deleteFood(foodId);

        MessageResponse res = new MessageResponse();
        res.setMessage("Successfully deleted food");
        return  new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Food> updateFoodAvailability(@PathVariable Long foodId,
                                                      @RequestHeader("Authorization") String jwtT) throws Exception {
        User user = userService.findUserByJwtToken(jwtT);

        Food food = foodService.updateAvailabilityStatus(foodId);

        return  new ResponseEntity<>(food, HttpStatus.CREATED);
    }

}
