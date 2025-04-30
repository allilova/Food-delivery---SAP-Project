package com.example.food_delivery_app.repository;

import com.example.food_delivery_app.model.Order;
import com.example.food_delivery_app.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

<<<<<<< HEAD
<<<<<<< HEAD
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrder(Order order);

    Optional<Payment> findByOrder_Id(Long id);
=======
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    Optional<Payment> findByOrder_Id(Long orderId);
>>>>>>> 3b97e188d54bd0a20c3391ce1ad1a3d3dc0fb7ca
=======
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    Optional<Payment> findByOrder_Id(Long orderId);
>>>>>>> 1db6b8e08a5a54e3b88a36b81c018b3860e2aaf5
}
