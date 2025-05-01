package com.example.food_delivery_app.repository;

import com.example.food_delivery_app.model.Payment;
import com.example.food_delivery_app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByIdAndUser(Long id, User user);
    Optional<Payment> findByOrder_IdAndUser(Long orderId, User user);
}
