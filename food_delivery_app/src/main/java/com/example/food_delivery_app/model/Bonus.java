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
public class Bonus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bonuusID;

    @ManyToOne
    @JoinColumn(name = "driver_id")
    private User driver;

    private Long bonusAmount;

    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Temporal(TemporalType.DATE)
    private Date endDate;

    private boolean isApproved;

    @ManyToOne
    @JoinColumn(name = "turnover_id")
    private RestaurantTurnover turnover;  // Съответния оборот, който е довел до бонуса
}
