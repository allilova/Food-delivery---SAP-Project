package com.example.food_delivery_app.controller;

import com.example.food_delivery_app.model.User;
import com.example.food_delivery_app.request.LoginRequest;
import com.example.food_delivery_app.response.AuthResponse;
import com.example.food_delivery_app.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:4000"})
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerUser(@Valid @RequestBody User user) {
        try {
            logger.info("Attempting to register user: {}", user.getEmail());
            logger.debug("Registration data: name={}, phoneNumber={}, address={}", 
                      user.getName(), user.getPhoneNumber(), user.getAddress());
            
            // Validate required fields
            if (user.getPhoneNumber() == null || user.getPhoneNumber().isEmpty()) {
                logger.warn("Registration failed: Phone number is missing");
                AuthResponse errorResponse = new AuthResponse();
                errorResponse.setMessage("Phone number is required");
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            }
            
            if (user.getAddress() == null || user.getAddress().isEmpty()) {
                logger.warn("Registration failed: Address is missing");
                AuthResponse errorResponse = new AuthResponse();
                errorResponse.setMessage("Address is required");
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            }
            
            AuthResponse authResponse = authenticationService.register(user);
            logger.info("User registered successfully: {}", user.getEmail());
            return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid registration attempt: {}", e.getMessage());
            AuthResponse errorResponse = new AuthResponse();
            errorResponse.setMessage("Invalid registration data: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Registration failed for user: {}", user.getEmail(), e);
            AuthResponse errorResponse = new AuthResponse();
            errorResponse.setMessage("Registration failed: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            logger.info("Login attempt for user: {}", loginRequest.getEmail());
            AuthResponse authResponse = authenticationService.login(loginRequest);
            logger.info("User logged in successfully: {}", loginRequest.getEmail());
            return new ResponseEntity<>(authResponse, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid login attempt: {}", e.getMessage());
            AuthResponse errorResponse = new AuthResponse();
            errorResponse.setMessage("Invalid credentials");
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            logger.error("Login failed for user: {}", loginRequest.getEmail(), e);
            AuthResponse errorResponse = new AuthResponse();
            errorResponse.setMessage("Login failed. Please try again later.");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}