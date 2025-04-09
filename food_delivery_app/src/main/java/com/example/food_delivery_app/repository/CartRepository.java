package com.example.food_delivery_app.repository;

import com.example.food_delivery_app.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {

}
