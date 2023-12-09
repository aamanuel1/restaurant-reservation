package com.project.restaurantbooking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "reservation")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "reservation_id")
    private Long id;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private long reservationNumber;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;
    public Reservation(LocalDateTime startTime, LocalDateTime endTime, Customer customer) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.customer = customer;
        this.reservationNumber = generateReservationNumber();
    }

    public static long generateReservationNumber() {
        Random random = new Random();
        return 1000 + random.nextLong(9000);
    }
}
