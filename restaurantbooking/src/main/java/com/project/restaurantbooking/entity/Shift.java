package com.project.restaurantbooking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.sql.Date;

@Entity
@Getter @Setter
@NoArgsConstructor
@Table(name = "shift")
public class Shift implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "shift_id")
    private Long shiftId;

    private Long staffId;

    private Long restaurantId;

    private Long tableId;

    private Date date;

    private DateTimeFormat startTime;

    private DateTimeFormat endTime;

    public Shift(Long shiftId, Long staffId, Long restaurantId, Long tableId, Date date, DateTimeFormat startTime, DateTimeFormat endTime) {
        this.shiftId = shiftId;
        this.staffId = staffId;
        this.restaurantId = restaurantId;
        this.tableId = tableId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Shift(Long restaurantId, Long tableId, Date date, DateTimeFormat startTime, DateTimeFormat endTime) {
        this.staffId = null;
        this.restaurantId = restaurantId;
        this.tableId = tableId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
