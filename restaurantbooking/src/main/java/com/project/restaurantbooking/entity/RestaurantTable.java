package com.project.restaurantbooking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;

@Entity
@Getter @Setter
@NoArgsConstructor
@Table(name = "table")
public class RestaurantTable implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "table_id")
    private Long tableId;

    private Long restaurantID;

    private int tableOccupancyNum;

    private boolean available;

    private ArrayList<Shift> timeslots;

    public RestaurantTable(Long tableId, Long restaurantID, int tableOccupancyNum, boolean available, ArrayList<Shift> timeslots) {
        this.tableId = tableId;
        this.restaurantID = restaurantID;
        this.tableOccupancyNum = tableOccupancyNum;
        this.available = available;
        this.timeslots = timeslots;
    }

    public RestaurantTable(Long restaurantID, int tableOccupancyNum, boolean available, ArrayList<Shift> timeslots) {
        this.restaurantID = restaurantID;
        this.tableOccupancyNum = tableOccupancyNum;
        this.available = available;
        this.timeslots = timeslots;
    }


}
