package com.example.food_delivery_app.controller;

import com.example.food_delivery_app.dto.RestaurantDto;
import com.example.food_delivery_app.dto.RestaurantResponseDto;
import com.example.food_delivery_app.model.Restaurant;
import com.example.food_delivery_app.model.User;
import com.example.food_delivery_app.service.RestaurantService;
<<<<<<< HEAD
<<<<<<< HEAD
=======
import com.example.food_delivery_app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
>>>>>>> 3b97e188d54bd0a20c3391ce1ad1a3d3dc0fb7ca
=======
import com.example.food_delivery_app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
>>>>>>> 1db6b8e08a5a54e3b88a36b81c018b3860e2aaf5
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.util.StringUtils;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:4000"}) // Adding explicit CORS configuration
public class RestaurantController {

    private final RestaurantService restaurantService;

<<<<<<< HEAD
    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<RestaurantResponseDto>> getAllRestaurants(
            @RequestHeader("Authorization") String jwt) throws Exception {

        userService.findUserByJwtToken(jwt); // валидация на потребителя

        List<RestaurantResponseDto> response = restaurantService.getAllRestaurantsDto();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

=======
    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<RestaurantResponseDto>> getAllRestaurants(
            @RequestHeader("Authorization") String jwt) throws Exception {

        userService.findUserByJwtToken(jwt); // валидация на потребителя

        List<RestaurantResponseDto> response = restaurantService.getAllRestaurantsDto();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

>>>>>>> 1db6b8e08a5a54e3b88a36b81c018b3860e2aaf5

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantResponseDto> findRestaurantById(@PathVariable Long id,
                                                                    @RequestHeader ("Authorization")String jwt) throws Exception {

        userService.findUserByJwtToken(jwt);
        Restaurant restaurant = restaurantService.findById(id);
        RestaurantResponseDto dto = restaurantService.convertToDto(restaurant); // трябва да бъде public
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping("/search")
<<<<<<< HEAD
<<<<<<< HEAD
    public ResponseEntity<List<RestaurantResponseDto>> searchRestaurants(@RequestParam String query) {
        if (!StringUtils.hasText(query)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(restaurantService.searchRestaurants(query));
=======
=======
>>>>>>> 1db6b8e08a5a54e3b88a36b81c018b3860e2aaf5
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
<<<<<<< HEAD
>>>>>>> 3b97e188d54bd0a20c3391ce1ad1a3d3dc0fb7ca
=======
>>>>>>> 1db6b8e08a5a54e3b88a36b81c018b3860e2aaf5
    }
}