package com.project.restaurantbooking.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter @Setter
@NoArgsConstructor
@Table(name = "restaurant_table")
public class RestaurantTable implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "table_id")
    private Long tableId;

    private int tableOccupancyNum;

    private boolean available;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @JsonIgnore
    @OneToMany(mappedBy = "table")
    private Set<Shift> timeslots = new HashSet<Shift>();

    public RestaurantTable(Long tableId, Restaurant restaurant, int tableOccupancyNum, boolean available) {
        this.tableId = tableId;
        this.restaurant = restaurant;
        this.tableOccupancyNum = tableOccupancyNum;
        this.available = available;
    }

    public RestaurantTable(Restaurant restaurant, int tableOccupancyNum, boolean available, ArrayList<Shift> timeslots) {
        this.restaurant = restaurant;
        this.tableOccupancyNum = tableOccupancyNum;
        this.available = available;
    }

    public RestaurantTable(Restaurant restaurant, int tableOccupancyNum, boolean available) {
        this.restaurant = restaurant;
        this.tableOccupancyNum = tableOccupancyNum;
        this.available = available;
    }
}
