package com.example.food_delivery_app.repository;

import com.example.food_delivery_app.model.Order;
import com.example.food_delivery_app.model.OrderStatus;
import com.example.food_delivery_app.model.Restaurant;
import com.example.food_delivery_app.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    // Customer order methods
    Page<Order> findByCustomer(User customer, Pageable pageable);
    Optional<Order> findByIdAndCustomer(Long id, User customer);
    
    // Restaurant order methods
    Page<Order> findByRestaurant(Restaurant restaurant, Pageable pageable);
    Page<Order> findByRestaurantAndOrderStatus(Restaurant restaurant, OrderStatus status, Pageable pageable);
    Optional<Order> findByIdAndRestaurant(Long id, Restaurant restaurant);
    
    // General status methods
    Page<Order> findByOrderStatus(OrderStatus status, Pageable pageable);
    List<Order> findByOrderStatus(OrderStatus status);
} 