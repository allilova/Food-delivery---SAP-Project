package com.example.food_delivery_app.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
<<<<<<< HEAD
<<<<<<< HEAD
    private Long addressID;

    @Column(nullable = false)
    private String street;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private String country;
=======
    private Long id;
    private String street;
    private String city;
>>>>>>> 3b97e188d54bd0a20c3391ce1ad1a3d3dc0fb7ca
=======
    private Long id;
    private String street;
    private String city;
>>>>>>> 1db6b8e08a5a54e3b88a36b81c018b3860e2aaf5
}
