package com.example.food_delivery_app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "restaurant_turnover", indexes = {
        @Index(name = "idx_start_date", columnList = "startDate"),
        @Index(name = "idx_end_date", columnList = "endDate")
})
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantTurnover {
    @Id
    @GeneratedValue()
    private Long turnoverID;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    private Long totalTurnover; // oborot na restoranta

    @Temporal(TemporalType.DATE)
    private Date startDate;  // Начална дата на отчетния период

    @Temporal(TemporalType.DATE)
    private Date endDate;  // Крайна дата на отчетния период
}
