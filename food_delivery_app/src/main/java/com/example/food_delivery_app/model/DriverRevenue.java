package com.example.food_delivery_app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "driver_revenue", indexes = {
        @Index(name = "idx_driver_revenue_start", columnList = "startDate"),
        @Index(name = "idx_driver_revenue_end", columnList = "endDate")
})
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DriverRevenue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long revenueID;

    @ManyToOne
    @JoinColumn(name = "driver_id")
    private User driver;

    private Long totalRevenue;  // Приходи на доставчика

    @Temporal(TemporalType.DATE)
    private Date startDate;  // Начална дата на отчетния период

    @Temporal(TemporalType.DATE)
    private Date endDate;  // Крайна дата на отчетния период
}
