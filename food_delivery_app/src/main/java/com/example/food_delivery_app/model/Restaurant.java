package com.example.food_delivery_app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Restaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long restaurantID;

    @OneToOne
    private User restaurant;

    private String restaurantName;

    private String type;

    @OneToOne
    private Address restaurantAddress;

    @Embedded
    private ContactInfo contactInfo;

    private String openingHours;

    private String closingHours;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true) //tova e neshto kato foreign key; po tozi nacin sa svurzani orders and restaurants
    private List<Order> orders = new ArrayList<>();

    @ElementCollection
    @Column(length = 1000)
    private List<String>images;

    private boolean open; //kogato e closed , nqma da moje da se porucva

    @OneToOne(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private Menu menu;
}
