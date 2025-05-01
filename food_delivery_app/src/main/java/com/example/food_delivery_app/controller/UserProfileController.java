package com.example.food_delivery_app.controller;

import com.example.food_delivery_app.model.User;
import com.example.food_delivery_app.request.CreateAddressRequest;
import com.example.food_delivery_app.request.CreateRestaurantRequest;
import com.example.food_delivery_app.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/user/profile")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:4000"})
public class UserProfileController {
    private static final Logger logger = LoggerFactory.getLogger(UserProfileController.class);
    private final UserService userService;

    public UserProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<User> getUserProfile(@RequestHeader("Authorization") String jwtToken) {
        try {
            User user = userService.findUserByJwtToken(jwtToken);
            logger.info("Retrieved profile for user: {}", user.getEmail());
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Failed to retrieve user profile", e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteUserProfile(@RequestHeader("Authorization") String jwtToken) {
        try {
            User user = userService.findUserByJwtToken(jwtToken);
            userService.deleteUser(user);
            logger.info("Deleted profile for user: {}", user.getEmail());
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            logger.error("Failed to delete user profile", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
} 