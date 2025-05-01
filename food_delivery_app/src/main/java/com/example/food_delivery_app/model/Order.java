package com.example.food_delivery_app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private User customer;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "restaurant_id", referencedColumnName = "id")
    private Restaurant restaurant;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    private Date orderDate;

    @ManyToOne (cascade = CascadeType.PERSIST)
    private Address deliveryAddress;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Payment payment;

    private int itemsQuantity;

    private float totalPrice;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private DeliveryInfo deliveryInfo;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
}
