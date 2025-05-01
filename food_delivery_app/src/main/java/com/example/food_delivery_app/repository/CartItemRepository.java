package com.example.food_delivery_app.repository;

import com.example.food_delivery_app.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
} 