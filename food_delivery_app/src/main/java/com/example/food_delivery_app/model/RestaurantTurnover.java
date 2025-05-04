package com.example.food_delivery_app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    private Long totalTurnover;

    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Temporal(TemporalType.DATE)
    private Date endDate;

    @OneToMany(mappedBy = "turnover", cascade = CascadeType.ALL)
    private List<Bonus> bonuses = new ArrayList<>();

}
