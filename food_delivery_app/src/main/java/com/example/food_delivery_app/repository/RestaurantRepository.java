package com.example.food_delivery_app.repository;

import com.example.food_delivery_app.model.Restaurant;
import com.example.food_delivery_app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    @Query("SELECT r FROM Restaurant r WHERE lower(r.restaurantName) LIKE lower(concat('%', :query, '%')) " +
            "OR lower(r.type) LIKE lower(concat('%', :query, '%'))")
    List<Restaurant> findBySearchQuery(@Param("query") String query);

    Optional<Restaurant> findById(Long id);
    
    @Query("SELECT r FROM Restaurant r WHERE r.restaurant.id = :#{#restaurantOwner.id}")
    Optional<Restaurant> findByRestaurant(@Param("restaurantOwner") User restaurantOwner);
}