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

    @OneToOne
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
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();

    @ElementCollection
    @Column(length = 1000)
    private List<String> images = new ArrayList<>();

    private boolean open = true; // Default to open

    @JsonIgnore
    @OneToOne(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private Menu menu;

    @OneToMany(mappedBy = "restaurant")
    private List<Rating> ratings = new ArrayList<>();
    
    // Convenience method needed for some parts of the application
    public Long getRestaurantID() {
        return id;
    }
    
    @Override
    public String toString() {
        return "Restaurant{" +
                "id=" + id +
                ", restaurantName='" + restaurantName + '\'' +
                ", type='" + type + '\'' +
                ", open=" + open +
                '}';
    }
}