package com.example.food_delivery_app.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int paymentID;

    @OneToOne(cascade = CascadeType.ALL)
    private Order order;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Temporal(TemporalType.TIMESTAMP)
    private Date paymentDate;

    private Float paymentAmount;

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

}
