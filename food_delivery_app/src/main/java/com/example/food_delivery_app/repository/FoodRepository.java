package com.example.food_delivery_app.repository;

import com.example.food_delivery_app.model.Category;
import com.example.food_delivery_app.model.Food;
import com.example.food_delivery_app.model.Menu;
import com.example.food_delivery_app.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FoodRepository extends JpaRepository<Food, Long> {

    List<Food> findByMenu(Menu menu);

    List<Food> findByCategory(Category category);

    @Query("SELECT f FROM Food f WHERE f.foodName LIKE %:keyword% OR f.category.categoryName LIKE %:keyword%")
    List<Food> searchFood(@Param("keyword")String keyword);
}
