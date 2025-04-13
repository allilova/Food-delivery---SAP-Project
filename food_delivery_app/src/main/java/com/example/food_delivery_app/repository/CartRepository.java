package com.example.food_delivery_app.repository;

import com.example.food_delivery_app.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByCustomer_UserID(Long userID);

    Optional<Cart> findByCustomer_Email(String email);
}
