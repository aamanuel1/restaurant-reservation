package com.project.restaurantbooking.entity;

import com.project.restaurantbooking.enums.Cuisine;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Food {

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private Cuisine cuisine;

    public Food(String foodName, Cuisine cuisine) {
        this.name = foodName;
        this.cuisine = cuisine;
    }
}
