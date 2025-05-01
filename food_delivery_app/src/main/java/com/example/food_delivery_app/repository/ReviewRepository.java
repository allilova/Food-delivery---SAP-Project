package com.example.food_delivery_app.repository;

import com.example.food_delivery_app.model.Review;
import com.example.food_delivery_app.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByRestaurantId(Long restaurantId, Pageable pageable);
    Page<Review> findByFoodId(Long foodId, Pageable pageable);
    Optional<Review> findByIdAndUser(Long id, User user);
} 