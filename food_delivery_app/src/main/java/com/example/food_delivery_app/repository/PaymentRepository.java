package com.example.food_delivery_app.repository;

import com.example.food_delivery_app.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    Optional<Payment> findByOrder_OrderID(int orderID);

}
