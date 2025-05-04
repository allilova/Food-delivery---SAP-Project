package com.example.food_delivery_app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private Long id;

    @ManyToOne
    @JoinColumn(name = "restaurant_user_id", referencedColumnName = "id")
    private User restaurant;

    private String restaurantName;

    private String type;

    @OneToOne
    @JoinColumn(name = "restaurant_address_id", referencedColumnName = "id")
    private Address restaurantAddress;

    @Embedded
    private ContactInfo contactInfo;

    private String openingHours;

    private String closingHours;

    @JsonIgnore
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true) //tova e neshto kato foreign key; po tozi nacin sa svurzani orders and restaurants
    private List<Order> orders = new ArrayList<>();

    @ElementCollection
    @Column(length = 1000)
    private List<String>images;

    private boolean open; //when it is closed, it will not be able to be ordered
    
    private boolean isPublic = false; // Controls visibility in customer catalog

    @JsonIgnore
    @OneToOne(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private Menu menu;

    @OneToMany(mappedBy = "restaurant")
    private List<Rating> ratings;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
    private List<RestaurantTurnover> turnovers = new ArrayList<>();
}
