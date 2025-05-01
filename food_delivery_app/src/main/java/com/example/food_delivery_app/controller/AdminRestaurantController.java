package com.example.food_delivery_app.controller;

import com.example.food_delivery_app.dto.RestaurantResponseDto;
import com.example.food_delivery_app.model.Restaurant;
import com.example.food_delivery_app.model.USER_ROLE;
import com.example.food_delivery_app.model.User;
import com.example.food_delivery_app.request.CreateRestaurantRequest;
import com.example.food_delivery_app.response.MessageResponse;
import com.example.food_delivery_app.service.RestaurantService;
import com.example.food_delivery_app.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/restaurants")
@Tag(name = "Admin Restaurant API", description = "API for restaurant administration")
public class AdminRestaurantController {
    private static final Logger logger = LoggerFactory.getLogger(AdminRestaurantController.class);

    private final RestaurantService restaurantService;
    private final UserService userService;

    @Autowired
    public AdminRestaurantController(RestaurantService restaurantService, UserService userService) {
        this.restaurantService = restaurantService;
        this.userService = userService;
    }

    @PostMapping
    @Operation(summary = "Create restaurant", description = "Creates a new restaurant")
    @ApiResponse(responseCode = "201", description = "Restaurant created successfully")
    @ApiResponse(responseCode = "403", description = "User lacks necessary permissions")
    public ResponseEntity<RestaurantResponseDto> createRestaurant(
            @Valid @RequestBody CreateRestaurantRequest req,
            @RequestHeader("Authorization") String jwt) {
        try {
            User user = userService.findUserByJwtToken(jwt);
            checkAdminAccess(user);
            
            logger.info("Creating new restaurant with name: {}", req.getRestaurantName());
            Restaurant created = restaurantService.createRestaurant(req, user);
            RestaurantResponseDto dto = restaurantService.convertToDto(created);
            
            return new ResponseEntity<>(dto, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error creating restaurant", e);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update restaurant", description = "Updates an existing restaurant")
    @ApiResponse(responseCode = "200", description = "Restaurant updated successfully")
    @ApiResponse(responseCode = "404", description = "Restaurant not found")
    public ResponseEntity<RestaurantResponseDto> updateRestaurant(
            @Valid @RequestBody CreateRestaurantRequest req,
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long id) {
        try {
            User user = userService.findUserByJwtToken(jwt);
            checkAdminAccess(user);
            
            logger.info("Updating restaurant with ID: {}", id);
            Restaurant restaurant = restaurantService.updateRestaurant(id, req);
            RestaurantResponseDto dto = restaurantService.convertToDto(restaurant);
            
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error updating restaurant with ID: {}", id, e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete restaurant", description = "Deletes an existing restaurant")
    @ApiResponse(responseCode = "200", description = "Restaurant deleted successfully")
    @ApiResponse(responseCode = "404", description = "Restaurant not found")
    public ResponseEntity<MessageResponse> deleteRestaurant(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long id) {
        try {
            User user = userService.findUserByJwtToken(jwt);
            checkAdminAccess(user);
            
            logger.info("Deleting restaurant with ID: {}", id);
            restaurantService.deleteRestaurant(id);
            
            MessageResponse res = new MessageResponse();
            res.setMessage("Restaurant deleted successfully");
            return new ResponseEntity<>(res, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error deleting restaurant with ID: {}", id, e);
            
            MessageResponse res = new MessageResponse();
            res.setMessage("Failed to delete restaurant: " + e.getMessage());
            return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Toggle restaurant status", description = "Opens or closes a restaurant")
    @ApiResponse(responseCode = "200", description = "Restaurant status updated successfully")
    @ApiResponse(responseCode = "404", description = "Restaurant not found")
    public ResponseEntity<RestaurantResponseDto> updateRestaurantStatus(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long id) {
        try {
            User user = userService.findUserByJwtToken(jwt);
            checkAdminAccess(user);
            
            logger.info("Toggling status for restaurant with ID: {}", id);
            Restaurant updated = restaurantService.updateRestaurantStatus(id);
            RestaurantResponseDto dto = restaurantService.convertToDto(updated);
            
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error updating status for restaurant with ID: {}", id, e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    private void checkAdminAccess(User user) throws Exception {
        if (user.getRole() != USER_ROLE.ROLE_ADMIN && user.getRole() != USER_ROLE.ROLE_RESTAURANT) {
            throw new Exception("Access denied: Admin or Restaurant role required.");
        }
    }
}