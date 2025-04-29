package com.example.food_delivery_app.repository;

import com.example.food_delivery_app.model.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MenuRepository extends JpaRepository<Menu, Long> {
    Optional<Menu> findByRestaurantRestaurantID(Long restaurantId);
}

