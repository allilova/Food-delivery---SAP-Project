package com.example.food_delivery_app.repository;

import com.example.food_delivery_app.model.Cart;
import com.example.food_delivery_app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
<<<<<<< HEAD
    Optional<Cart> findByUser(User user);
=======
    Optional<Cart> findByCustomer_Id(Long id);
>>>>>>> 3b97e188d54bd0a20c3391ce1ad1a3d3dc0fb7ca

    Optional<Cart> findByUser_Id(Long id);

    Optional<Cart> findByUser_Email(String email);
}
