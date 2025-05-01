package com.example.food_delivery_app.repository;

import com.example.food_delivery_app.model.Order;
import com.example.food_delivery_app.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByCustomer(User customer, Pageable pageable);
    Optional<Order> findByIdAndCustomer(Long id, User customer);
} 