package com.example.food_delivery_app.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Getter
@Setter
@ToString
@Entity
@Table(name = "carts")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<CartItem> items = new HashSet<>();

    @Column(nullable = false)
    private Double totalAmount = 0.0;

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

    public void addItem(CartItem item) {
        items.add(item);
        updateTotalAmount();
    }

    public void removeItem(CartItem item) {
        items.remove(item);
        updateTotalAmount();
    }

    public void clear() {
        items.clear();
        totalAmount = 0.0;
    }

    private void updateTotalAmount() {
        totalAmount = items.stream()
                .mapToDouble(CartItem::getPrice)
                .sum();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cart cart = (Cart) o;
        return Objects.equals(id, cart.id) &&
               Objects.equals(user, cart.user) &&
               Objects.equals(totalAmount, cart.totalAmount) &&
               Objects.equals(createdAt, cart.createdAt) &&
               Objects.equals(updatedAt, cart.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, totalAmount, createdAt, updatedAt);
    }
}