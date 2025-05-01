package com.example.food_delivery_app.repository;

import com.example.food_delivery_app.model.Category;
import com.example.food_delivery_app.model.Food;
import com.example.food_delivery_app.model.Menu;
import com.example.food_delivery_app.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FoodRepository extends JpaRepository<Food, Long> {

    List<Food> findByMenu(Menu menu);

    List<Food> findByCategory(Category category);

    @Query("SELECT f FROM Food f WHERE f.name LIKE %:keyword% OR f.category.categoryName LIKE %:keyword%")
    List<Food> searchFood(@Param("keyword")String keyword);

    Optional<Food> findById(Long id);


}
