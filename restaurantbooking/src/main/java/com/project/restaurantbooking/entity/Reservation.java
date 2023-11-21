package com.project.restaurantbooking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "reservation")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "reservation_id")
    private Long reservationId;

//    private Long userId;

//    private Long restaurantId;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

//    private Long tableId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Customer user;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @OneToOne
    @JoinColumn(name = "table_id")
    private RestaurantTable restaurantTable;

    public Reservation(Long reservationId, LocalDateTime startTime, LocalDateTime endTime, Customer user, Restaurant restaurant, RestaurantTable restaurantTable) {
        this.reservationId = reservationId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.user = user;
        this.restaurant = restaurant;
        this.restaurantTable = restaurantTable;
    }

    public Reservation(LocalDateTime startTime, LocalDateTime endTime, Customer user, Restaurant restaurant, RestaurantTable restaurantTable) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.user = user;
        this.restaurant = restaurant;
        this.restaurantTable = restaurantTable;
    }

    public Reservation(LocalDateTime startTime, LocalDateTime endTime, Restaurant restaurant, RestaurantTable restaurantTable) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.restaurant = restaurant;
        this.restaurantTable = restaurantTable;
    }
}
