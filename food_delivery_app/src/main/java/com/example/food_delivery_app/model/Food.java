package com.example.food_delivery_app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Food {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long foodID;

    private String foodName;

    private String foodDescription;

    private String foodImage;

    @Column(precision = 10, scale = 2)
    private BigDecimal foodPrice;

    private int preparationTime;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    private boolean available;

    @ManyToOne
    @JoinColumn(name = "menu_id")
    private Menu menu;

    @ManyToMany
    private List<IngredientsItem> ingredients = new ArrayList<>();
}
