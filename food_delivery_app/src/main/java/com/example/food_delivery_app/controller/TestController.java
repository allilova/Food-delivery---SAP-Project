package com.example.food_delivery_app.controller;

import com.example.food_delivery_app.model.USER_ROLE;
import com.example.food_delivery_app.model.User;
import com.example.food_delivery_app.repository.RestaurantRepository;
import com.example.food_delivery_app.response.MessageResponse;
import com.example.food_delivery_app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@RestController
public class TestController {
    private static final Logger logger = LoggerFactory.getLogger(TestController.class);
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private RestaurantRepository restaurantRepository;

    @GetMapping("/api/test")
    public String test() {
        return "API is Working";
    }
    
    // Simple debug endpoint with optional header
    @GetMapping("/api/auth/debug")
    public ResponseEntity<?> debugAuth(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            logger.info("Auth debug requested");
            
            Map<String, Object> response = new HashMap<>();
            
            // Basic environment info
            response.put("timestamp", new java.util.Date().toString());
            response.put("apiWorking", true);
            
            // Authentication info
            Authentication auth = null;
            try {
                auth = SecurityContextHolder.getContext().getAuthentication();
            } catch (Exception e) {
                logger.warn("Error getting authentication from SecurityContext", e);
                response.put("authError", e.getMessage());
            }
            
            Map<String, Object> authInfo = new HashMap<>();
            authInfo.put("isAuthenticated", auth != null && auth.isAuthenticated());
            
            if (auth != null) {
                try {
                    authInfo.put("name", auth.getName());
                    authInfo.put("authorities", auth.getAuthorities().toString());
                    
                    
                    try {
                        if (auth.getPrincipal() != null) {
                            authInfo.put("principal", auth.getPrincipal().toString());
                        } else {
                            authInfo.put("principal", "null");
                        }
                    } catch (Exception e) {
                        authInfo.put("principalError", e.getMessage());
                    }
                    
                    response.put("auth", authInfo);
                } catch (Exception e) {
                    authInfo.put("error", e.getMessage());
                    response.put("auth", authInfo);
                }
            } else {
                authInfo.put("message", "No authentication found");
                response.put("auth", authInfo);
            }
            
            // Token info if header is present
            if (authHeader != null && !authHeader.isEmpty()) {
                logger.info("Auth header present");
                try {
                    Map<String, Object> tokenInfo = new HashMap<>();
                    tokenInfo.put("headerLength", authHeader.length());
                    tokenInfo.put("headerStart", authHeader.substring(0, Math.min(15, authHeader.length())));
                    
                    // Extract token
                    String token = authHeader;
                    if (authHeader.startsWith("Bearer ")) {
                        token = authHeader.substring(7);
                        tokenInfo.put("bearerFormat", true);
                    } else {
                        tokenInfo.put("bearerFormat", false);
                    }
                    
                    // Only try to find user from token if token looks valid
                    if (token != null && token.length() > 20) {
                        try {
                            User user = userService.findUserByJwtToken(token);
                            if (user != null) {
                                Map<String, Object> userInfo = new HashMap<>();
                                userInfo.put("found", true);
                                userInfo.put("email", user.getEmail());
                                userInfo.put("name", user.getName());
                                userInfo.put("id", user.getId());
                                userInfo.put("role", user.getRole());
                                
                                // Check if user has restaurant
                                try {
                                    var restaurantOpt = restaurantRepository.findByRestaurant(user);
                                    boolean hasRestaurant = restaurantOpt.isPresent();
                                    userInfo.put("hasRestaurant", hasRestaurant);
                                    
                                    if (hasRestaurant) {
                                        var restaurant = restaurantOpt.get();
                                        userInfo.put("restaurantId", restaurant.getId());
                                        userInfo.put("restaurantName", restaurant.getRestaurantName());
                                    }
                                } catch (Exception e) {
                                    userInfo.put("restaurantError", e.getMessage());
                                }
                                
                                tokenInfo.put("user", userInfo);
                            } else {
                                tokenInfo.put("userFound", false);
                                tokenInfo.put("message", "User not found for token");
                            }
                        } catch (Exception e) {
                            tokenInfo.put("userError", e.getMessage());
                        }
                    } else {
                        tokenInfo.put("validToken", false);
                        tokenInfo.put("message", "Token appears to be invalid format or empty");
                    }
                    
                    response.put("token", tokenInfo);
                } catch (Exception e) {
                    response.put("tokenError", e.getMessage());
                }
            } else {
                response.put("tokenPresent", false);
                response.put("message", "No Authorization header provided");
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error in auth debug endpoint", e);
            
            // Return a simple error message rather than 500
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            errorResponse.put("status", "error");
            errorResponse.put("timestamp", new java.util.Date().toString());
            
            // errorResponse.put("stackTrace", e.getStackTrace()[0].toString());
            
            // Always return 200 OK with error details in the body
            // This prevents 500 errors from propagating to the frontend
            return ResponseEntity.ok(errorResponse);
        }
    }
    
    // Original debug endpoint with required header
    @GetMapping("/api/auth/debug-token")
    public ResponseEntity<?> debugAuthToken(@RequestHeader("Authorization") String authHeader) {
        try {
            logger.info("Auth token debug requested with header: {}", 
                authHeader.length() > 15 ? authHeader.substring(0, 15) + "..." : authHeader);
            
            // Get current authentication
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            logger.info("Current authentication: {}", auth);
            
            if (auth != null) {
                logger.info("Auth name: {}", auth.getName());
                logger.info("Auth authorities: {}", auth.getAuthorities());
                logger.info("Auth principal: {}", auth.getPrincipal());
                
                // Extract token from header
                String token = authHeader;
                if (authHeader.startsWith("Bearer ")) {
                    token = authHeader.substring(7);
                }
                
                // Find user from token
                User user = userService.findUserByJwtToken(token);
                if (user != null) {
                    logger.info("Found user: {} with role: {}", user.getEmail(), user.getRole());
                    
                    // Check if user has restaurant
                    var restaurantOpt = restaurantRepository.findByRestaurant(user);
                    boolean hasRestaurant = restaurantOpt.isPresent();
                    
                    Map<String, Object> response = new HashMap<>();
                    response.put("email", user.getEmail());
                    response.put("name", user.getName());
                    response.put("id", user.getId());
                    response.put("role", user.getRole());
                    response.put("authorities", auth.getAuthorities().toString());
                    response.put("hasRestaurant", hasRestaurant);
                    
                    if (hasRestaurant) {
                        var restaurant = restaurantOpt.get();
                        response.put("restaurantId", restaurant.getId());
                        response.put("restaurantName", restaurant.getRestaurantName());
                    }
                    
                    return ResponseEntity.ok(response);
                } else {
                    return ResponseEntity.status(401).body(new MessageResponse("User not found for token"));
                }
            } else {
                return ResponseEntity.status(401).body(new MessageResponse("No authentication found"));
            }
        } catch (Exception e) {
            logger.error("Error in auth debug endpoint", e);
            return ResponseEntity.status(500).body(new MessageResponse("Error: " + e.getMessage()));
        }
    }
    
    @GetMapping("/api/user/role")
    public ResponseEntity<?> getUserRole(@AuthenticationPrincipal User user) {
        try {
            if (user == null) {
                return ResponseEntity.status(401).body(new MessageResponse("Not authenticated"));
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("email", user.getEmail());
            response.put("role", user.getRole());
            response.put("isRestaurant", user.getRole() == USER_ROLE.ROLE_RESTAURANT);
            response.put("isAdmin", user.getRole() == USER_ROLE.ROLE_ADMIN);
            response.put("isCustomer", user.getRole() == USER_ROLE.ROLE_CUSTOMER);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error in user role endpoint", e);
            return ResponseEntity.status(500).body(new MessageResponse("Error: " + e.getMessage()));
        }
    }
}