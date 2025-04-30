package com.example.food_delivery_app.service;

import com.example.food_delivery_app.model.Cart;
import com.example.food_delivery_app.model.User;
import com.example.food_delivery_app.request.CartItemRequest;

public interface CartService {
    Cart getCart(User user);
    Cart addItemToCart(User user, CartItemRequest itemRequest);
    Cart updateCartItem(User user, Long itemId, CartItemRequest itemRequest);
    Cart removeItemFromCart(User user, Long itemId);
    void clearCart(User user);
} 