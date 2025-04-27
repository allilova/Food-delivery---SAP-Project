package com.example.food_delivery_app.repository;

import com.example.food_delivery_app.model.IngredientsItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IngredientsItemRepository extends JpaRepository<IngredientsItem, Long>{
    Optional<IngredientsItem> findByName(String name);

}
