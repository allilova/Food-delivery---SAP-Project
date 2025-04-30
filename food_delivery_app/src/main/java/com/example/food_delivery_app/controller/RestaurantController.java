package com.example.food_delivery_app.controller;

import com.example.food_delivery_app.dto.RestaurantDto;
import com.example.food_delivery_app.dto.RestaurantResponseDto;
import com.example.food_delivery_app.model.Restaurant;
import com.example.food_delivery_app.model.User;
import com.example.food_delivery_app.service.RestaurantService;
import com.example.food_delivery_app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:4000"}) // Adding explicit CORS configuration
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<RestaurantResponseDto>> getAllRestaurants(
            @RequestHeader("Authorization") String jwt) throws Exception {

        userService.findUserByJwtToken(jwt); // валидация на потребителя

        List<RestaurantResponseDto> response = restaurantService.getAllRestaurantsDto();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping("/{id}")
    public ResponseEntity<RestaurantResponseDto> findRestaurantById(@PathVariable Long id,
                                                                    @RequestHeader ("Authorization")String jwt) throws Exception {

        userService.findUserByJwtToken(jwt);
        Restaurant restaurant = restaurantService.findById(id);
        RestaurantResponseDto dto = restaurantService.convertToDto(restaurant); // трябва да бъде public
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<RestaurantResponseDto>> searchRestaurants(@RequestParam String keyword,
                                                                         @RequestHeader("Authorization") String jwt) throws Exception {
        userService.findUserByJwtToken(jwt); // за валидация
        List<RestaurantResponseDto> results = restaurantService.searchRestaurantsDto(keyword);
        return new ResponseEntity<>(results, HttpStatus.OK);
    }

    @PutMapping("/{id}/add-favourites")
    public ResponseEntity<RestaurantDto> addToFavourites(@PathVariable Long id,
                                                      @RequestHeader ("Authorization")String jwt) throws Exception {
        User user = userService.findUserByJwtToken(jwt);
        RestaurantDto restaurant = restaurantService.addToFavourites(id, user);

        return new ResponseEntity<>(restaurant, HttpStatus.OK);
    }
}