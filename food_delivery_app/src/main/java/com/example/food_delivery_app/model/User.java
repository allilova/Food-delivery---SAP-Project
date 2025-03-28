package com.example.food_delivery_app.model;

import com.example.food_delivery_app.dto.RestaurantDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int userID;

    private String name;

    private String email;

    private String password;

    private String phone_number;

    private String address;

    private USER_ROLE role;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "customer")
    private List<Order> orders = new ArrayList<>();

    @ElementCollection
    private List<RestaurantDto>favourites = new ArrayList();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Address> adresses = new ArrayList<>();


}
