package com.project.restaurantbooking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;

@Entity
@Getter @Setter
@NoArgsConstructor
@Table(name = "shift")
public class Shift implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "shift_id")
    private Long shiftId;

    @ManyToOne
    @JoinColumn(name = "staff_id")
    private Staff staff;

    @ManyToOne
    @JoinColumn(name = "table_id")
    private RestaurantTable table;

    private Date date;

    private LocalDate startTime;

    private LocalDate endTime;

    public Shift(Long shiftId, Staff staff, RestaurantTable restaurantTable, Date date, LocalDate startTime, LocalDate endTime) {
        this.shiftId = shiftId;
        this.staff = staff;
        this.table = table;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Shift(RestaurantTable table, Date date, LocalDate startTime, LocalDate endTime) {
        this.staff = null;
        this.table = table;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
