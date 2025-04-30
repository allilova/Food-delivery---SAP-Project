package com.example.food_delivery_app.repository;

import com.example.food_delivery_app.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;



public interface CategoryRepository extends JpaRepository<Category, Long> {
}
