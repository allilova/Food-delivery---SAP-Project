package com.example.food_delivery_app.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data

public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int paymentID;


}
