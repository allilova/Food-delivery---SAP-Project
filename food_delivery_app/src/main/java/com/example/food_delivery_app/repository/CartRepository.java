package com.example.food_delivery_app.repository;

import com.example.food_delivery_app.model.Cart;
import com.example.food_delivery_app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserId(Long id);
    Optional<Cart> findByUserEmail(String email);
    Optional<Cart> findByUser(User user);
}
