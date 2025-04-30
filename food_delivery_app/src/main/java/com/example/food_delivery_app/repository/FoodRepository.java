package com.example.food_delivery_app.repository;

import com.example.food_delivery_app.model.Category;
import com.example.food_delivery_app.model.Food;
import com.example.food_delivery_app.model.Menu;
import com.example.food_delivery_app.model.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FoodRepository extends JpaRepository<Food, Long> {

    @Query("SELECT f FROM Food f JOIN f.category c WHERE c.menu = :menu")
    Page<Food> findByMenu(@Param("menu") Menu menu, Pageable pageable);

    @Query("SELECT f FROM Food f JOIN f.category c WHERE c.menu = :menu AND c.categoryName = :categoryName")
    Page<Food> findByMenuAndCategory_CategoryName(@Param("menu") Menu menu, @Param("categoryName") String categoryName, Pageable pageable);

    List<Food> findByCategory(Category category);

<<<<<<< HEAD
    @Query("SELECT f FROM Food f WHERE f.name LIKE %:keyword% OR f.category.categoryName LIKE %:keyword%")
    Page<Food> searchFood(@Param("keyword") String keyword, Pageable pageable);
=======
    @Query("SELECT f FROM Food f WHERE f.foodName LIKE %:keyword% OR f.category.categoryName LIKE %:keyword%")
    List<Food> searchFood(@Param("keyword")String keyword);

    Optional<Food> findById(Long id);


<<<<<<< HEAD
>>>>>>> 3b97e188d54bd0a20c3391ce1ad1a3d3dc0fb7ca
=======
>>>>>>> 1db6b8e08a5a54e3b88a36b81c018b3860e2aaf5
}
