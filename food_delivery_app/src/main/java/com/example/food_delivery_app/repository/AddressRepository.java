package com.example.food_delivery_app.repository;

import com.example.food_delivery_app.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {

}
