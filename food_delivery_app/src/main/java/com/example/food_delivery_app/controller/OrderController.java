package com.example.food_delivery_app.controller;

import com.example.food_delivery_app.model.Order;
import com.example.food_delivery_app.model.User;
import com.example.food_delivery_app.request.CreateOrderRequest;
import com.example.food_delivery_app.service.OrderService;
import com.example.food_delivery_app.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:4000"})
public class OrderController {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    private final OrderService orderService;
    private final UserService userService;

    public OrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(
            @Valid @RequestBody CreateOrderRequest orderRequest,
            @RequestHeader("Authorization") String jwtToken) {
        try {
            if (jwtToken.startsWith("Bearer ")) {
                jwtToken = jwtToken.substring(7);
            }
            User user = userService.findUserByJwtToken(jwtToken);
            Order order = orderService.createOrder(orderRequest, user);
            logger.info("Order created successfully for user: {}", user.getEmail());
            return new ResponseEntity<>(order, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Failed to create order", e);
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserOrders(
            @RequestHeader("Authorization") String jwtToken,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            logger.info("Received request for user orders with token: {}", jwtToken.substring(0, Math.min(20, jwtToken.length())) + "...");
            User user = userService.findUserByJwtToken(jwtToken);
            logger.info("Found user: {}", user.getEmail());
            
            Pageable pageable = PageRequest.of(page, size);
            Page<Order> orders = orderService.getUserOrders(user, pageable);
            logger.info("Retrieved {} orders for user: {}", orders.getContent().size(), user.getEmail());
            
            return new ResponseEntity<>(orders, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Failed to retrieve user orders: {}", e.getMessage());
            e.printStackTrace();
            if (e.getMessage().contains("Invalid JWT token") || e.getMessage().contains("User not found")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized to view these orders");
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve orders due to a server error");
        }
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(
            @PathVariable Long orderId,
            @RequestHeader("Authorization") String jwtToken) {
        try {
            if (jwtToken.startsWith("Bearer ")) {
                jwtToken = jwtToken.substring(7);
            }
            User user = userService.findUserByJwtToken(jwtToken);
            Order order = orderService.getOrderById(orderId, user);
            logger.info("Retrieved order: {}", orderId);
            return new ResponseEntity<>(order, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Failed to retrieve order: {}", orderId, e);
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam String status,
            @RequestHeader("Authorization") String jwtToken) {
        try {
            if (jwtToken.startsWith("Bearer ")) {
                jwtToken = jwtToken.substring(7);
            }
            User user = userService.findUserByJwtToken(jwtToken);
            Order order = orderService.updateOrderStatus(orderId, status, user);
            logger.info("Updated order status: {} to {}", orderId, status);
            return new ResponseEntity<>(order, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Failed to update order status: {}", orderId, e);
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
} 