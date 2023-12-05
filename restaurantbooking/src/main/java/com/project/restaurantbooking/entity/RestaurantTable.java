package com.project.restaurantbooking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "restaurant_table")
public class RestaurantTable implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "table_id")
    private Long tableId;

    private int tableOccupancyNum;

    private Boolean available;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    public RestaurantTable(int tableOccupancyNum, boolean available, Restaurant restaurant) {
        this.tableOccupancyNum = tableOccupancyNum;
        this.available = available;
        this.restaurant = restaurant;
    }


}
