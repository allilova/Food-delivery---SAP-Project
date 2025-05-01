package com.example.food_delivery_app.controller;

import com.example.food_delivery_app.dto.RestaurantResponseDto;
import com.example.food_delivery_app.model.Restaurant;
import com.example.food_delivery_app.model.User;
import com.example.food_delivery_app.response.MessageResponse;
import com.example.food_delivery_app.service.RestaurantService;
import com.example.food_delivery_app.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:4000"})
@Tag(name = "Restaurant API", description = "API for restaurant management")
public class RestaurantController {
    private static final Logger logger = LoggerFactory.getLogger(RestaurantController.class);

    private final RestaurantService restaurantService;
    private final UserService userService;

    @Autowired
    public RestaurantController(RestaurantService restaurantService, UserService userService) {
        this.restaurantService = restaurantService;
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "Get all restaurants", description = "Retrieves a list of all restaurants")
    public ResponseEntity<List<RestaurantResponseDto>> getAllRestaurants() {
        logger.info("Fetching all restaurants");
        List<RestaurantResponseDto> response = restaurantService.getAllRestaurantsDto();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get restaurant by ID", description = "Retrieves a specific restaurant by its ID")
    public ResponseEntity<RestaurantResponseDto> findRestaurantById(@PathVariable Long id) {
        try {
            logger.info("Fetching restaurant with ID: {}", id);
            Restaurant restaurant = restaurantService.findById(id);
            RestaurantResponseDto dto = restaurantService.convertToDto(restaurant);
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error fetching restaurant with ID: {}", id, e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/search")
    @Operation(summary = "Search restaurants", description = "Searches for restaurants by name or food type")
    public ResponseEntity<List<RestaurantResponseDto>> searchRestaurants(@RequestParam String keyword) {
        logger.info("Searching restaurants with keyword: {}", keyword);
        List<RestaurantResponseDto> results = restaurantService.searchRestaurantsDto(keyword);
        return new ResponseEntity<>(results, HttpStatus.OK);
    }

    @PutMapping("/{id}/add-favorites")
    @Operation(summary = "Add/remove restaurant from favorites", description = "Toggles a restaurant in the user's favorites list")
    public ResponseEntity<?> toggleFavorite(@PathVariable Long id, @RequestHeader("Authorization") String jwt) {
        try {
            User user = userService.findUserByJwtToken(jwt);
            logger.info("User {} toggling favorite status for restaurant {}", user.getId(), id);
            restaurantService.toggleFavorite(id, user);
            
            MessageResponse response = new MessageResponse();
            response.setMessage("Favorite status updated successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error toggling favorite for restaurant ID: {}", id, e);
            MessageResponse response = new MessageResponse();
            response.setMessage("Error updating favorite status: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
    
    @GetMapping("/favorites")
    @Operation(summary = "Get user's favorite restaurants", description = "Retrieves all favorite restaurants for the current user")
    public ResponseEntity<List<RestaurantResponseDto>> getFavoriteRestaurants(@RequestHeader("Authorization") String jwt) {
        try {
            User user = userService.findUserByJwtToken(jwt);
            logger.info("Fetching favorite restaurants for user ID: {}", user.getId());
            List<RestaurantResponseDto> favorites = restaurantService.getUserFavorites(user);
            return new ResponseEntity<>(favorites, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error fetching favorite restaurants", e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}