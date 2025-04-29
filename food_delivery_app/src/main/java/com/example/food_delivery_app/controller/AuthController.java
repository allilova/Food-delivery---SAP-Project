package com.example.food_delivery_app.controller;

import com.example.food_delivery_app.model.User;
import com.example.food_delivery_app.request.LoginRequest;
import com.example.food_delivery_app.response.AuthResponse;
import com.example.food_delivery_app.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:4000"})
public class AuthController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerUser(@RequestBody User user) {
        try {
            AuthResponse authResponse = authenticationService.register(user);
            return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
        } catch (Exception e) {
            // Return a proper error response instead of throwing an exception
            AuthResponse errorResponse = new AuthResponse();
            errorResponse.setMessage(e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(@RequestBody LoginRequest loginRequest) {
        try {
            AuthResponse authResponse = authenticationService.login(loginRequest);
            return new ResponseEntity<>(authResponse, HttpStatus.OK);
        } catch (Exception e) {
            // Return a proper error response instead of throwing an exception
            AuthResponse errorResponse = new AuthResponse();
            errorResponse.setMessage(e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        }
    }
}