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
    private int orderID;

    @ManyToOne
    private User customer;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "restaurant_id", referencedColumnName = "restaurantID")
    private Restaurant restaurant;


    private Long totalAmount;

    private String orderStatus;

    private Date orderDate;

    @ManyToOne
    private Address deliveryAddress;

    @OneToMany
    private List<OrderItem> items;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Payment payment;

    private int itemsQuantity;

    private float totalPrice;

}
