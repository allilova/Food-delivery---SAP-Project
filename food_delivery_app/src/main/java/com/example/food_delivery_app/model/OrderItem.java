package com.example.food_delivery_app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Food food;

    private int quantity;

    private double price;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ElementCollection
    @CollectionTable(name = "order_item_ingredients", joinColumns = @JoinColumn(name = "order_item_id"))
    @Column(name = "ingredient")
    private List<String> ingredients;

    @OneToMany(mappedBy = "orderItem")
    private List<Rating> ratings;
}
