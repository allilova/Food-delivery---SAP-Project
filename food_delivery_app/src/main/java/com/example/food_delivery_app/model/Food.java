package com.example.food_delivery_app.model;

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
public class Food {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int foodID;

    private String foodName;

    private String foodDescription;

    private String foodImage;

    private int foodPrice;

    @ManyToOne
    private Category foodCategory;

    @Column(length = 1000)
    @ElementCollection
    private List<String> image;

    private boolean available;

    @ManyToOne
    private Restaurant restaurant;

    @ManyToMany
    private List<IngredientsItem> ingredients = new ArrayList<>();

}
