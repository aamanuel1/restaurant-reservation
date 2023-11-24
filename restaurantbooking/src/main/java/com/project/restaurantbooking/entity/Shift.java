package com.project.restaurantbooking.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "shift")
public class Shift implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "shift_id")
    private Long shiftId;

    @ManyToOne
    @JoinColumn(name = "staff_id", nullable = true)
    private Staff staff;

    @ManyToOne
    @JoinColumn(name = "table_id", nullable = true)
    private RestaurantTable table;

    private Date date;

    private LocalDate startTime;

    private LocalDate endTime;

    public Shift(Staff staff, RestaurantTable table, Date date, LocalDate startTime, LocalDate endTime) {
        this.staff = staff;
        this.table = table;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
