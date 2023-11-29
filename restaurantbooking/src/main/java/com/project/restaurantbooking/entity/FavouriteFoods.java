package com.project.restaurantbooking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "favourite_foods")
public class FavouriteFoods implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "food_id", nullable = false)
    private Food food;

//    public FavouriteFoods(Customer customer, Food food) {
//        this.customer = customer;
//        this.food = food;
//    }
}
