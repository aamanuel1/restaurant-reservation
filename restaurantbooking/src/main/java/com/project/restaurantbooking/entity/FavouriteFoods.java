package com.project.restaurantbooking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "favourite_foods")
public class FavouriteFoods implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "food_id")
    private Long foodId;

    private String favouriteFoods;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Customer customer;

    public FavouriteFoods(Long foodId, String favouriteFoods, Customer customer) {
        this.foodId = foodId;
        this.favouriteFoods = favouriteFoods;
        this.customer = customer;
    }

    public FavouriteFoods(String favouriteFoods, Customer customer) {
        this.favouriteFoods = favouriteFoods;
        this.customer = customer;
    }
}
