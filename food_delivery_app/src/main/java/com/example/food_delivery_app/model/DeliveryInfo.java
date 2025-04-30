package com.example.food_delivery_app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

public class DeliveryInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus deliveryStatus;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "driver_id")
    private User driver;

    @Temporal(TemporalType.TIMESTAMP)
    private Date deliveryDate;
}
