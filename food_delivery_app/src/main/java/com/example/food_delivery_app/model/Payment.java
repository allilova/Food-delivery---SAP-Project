package com.example.food_delivery_app.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Data
@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;

    private PaymentStatus paymentStatus;
    private Double amount;
    private PaymentType paymentType;
    private Date paymentDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
