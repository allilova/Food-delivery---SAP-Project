package com.example.food_delivery_app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

<<<<<<< HEAD
<<<<<<< HEAD
import java.util.Objects;
=======
>>>>>>> 3b97e188d54bd0a20c3391ce1ad1a3d3dc0fb7ca
=======
>>>>>>> 1db6b8e08a5a54e3b88a36b81c018b3860e2aaf5

@Entity
@Getter
@Setter
@ToString
@Table(name = "cart_items")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    @JsonIgnore
    @ToString.Exclude
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "food_id", nullable = false)
    private Food food;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Double price;

    @Column(name = "created_at")
    private java.time.LocalDateTime createdAt;

    @Column(name = "updated_at")
    private java.time.LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = java.time.LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = java.time.LocalDateTime.now();
    }

    public void updatePrice() {
        this.price = food.getPrice() * quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CartItem cartItem)) return false;
        return Objects.equals(id, cartItem.id) &&
               Objects.equals(food, cartItem.food) &&
               Objects.equals(quantity, cartItem.quantity) &&
               Objects.equals(price, cartItem.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, food, quantity, price);
    }
}
