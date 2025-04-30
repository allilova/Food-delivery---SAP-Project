package com.example.food_delivery_app.controller;

import com.example.food_delivery_app.model.Cart;
import com.example.food_delivery_app.model.User;
import com.example.food_delivery_app.request.CartItemRequest;
import com.example.food_delivery_app.service.CartService;
import com.example.food_delivery_app.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:4000"})
public class CartController {
    private static final Logger logger = LoggerFactory.getLogger(CartController.class);
    private final CartService cartService;
    private final UserService userService;

    public CartController(CartService cartService, UserService userService) {
        this.cartService = cartService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<Cart> getCart(@RequestHeader("Authorization") String jwtToken) {
        try {
            User user = userService.findUserByJwtToken(jwtToken);
            Cart cart = cartService.getCart(user);
            logger.info("Retrieved cart for user: {}", user.getEmail());
            return new ResponseEntity<>(cart, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Failed to retrieve cart", e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/items")
    public ResponseEntity<Cart> addItemToCart(
            @Valid @RequestBody CartItemRequest itemRequest,
            @RequestHeader("Authorization") String jwtToken) {
        try {
            User user = userService.findUserByJwtToken(jwtToken);
            Cart cart = cartService.addItemToCart(user, itemRequest);
            logger.info("Added item to cart for user: {}", user.getEmail());
            return new ResponseEntity<>(cart, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Failed to add item to cart", e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<Cart> updateCartItem(
            @PathVariable Long itemId,
            @Valid @RequestBody CartItemRequest itemRequest,
            @RequestHeader("Authorization") String jwtToken) {
        try {
            User user = userService.findUserByJwtToken(jwtToken);
            Cart cart = cartService.updateCartItem(user, itemId, itemRequest);
            logger.info("Updated cart item: {} for user: {}", itemId, user.getEmail());
            return new ResponseEntity<>(cart, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Failed to update cart item: {}", itemId, e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Cart> removeItemFromCart(
            @PathVariable Long itemId,
            @RequestHeader("Authorization") String jwtToken) {
        try {
            User user = userService.findUserByJwtToken(jwtToken);
            Cart cart = cartService.removeItemFromCart(user, itemId);
            logger.info("Removed item from cart: {} for user: {}", itemId, user.getEmail());
            return new ResponseEntity<>(cart, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Failed to remove item from cart: {}", itemId, e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(@RequestHeader("Authorization") String jwtToken) {
        try {
            User user = userService.findUserByJwtToken(jwtToken);
            cartService.clearCart(user);
            logger.info("Cleared cart for user: {}", user.getEmail());
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            logger.error("Failed to clear cart", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
} 