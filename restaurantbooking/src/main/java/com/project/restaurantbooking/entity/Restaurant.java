package com.project.restaurantbooking.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.restaurantbooking.enums.Cuisine;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(exclude = {"cuisines"})
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "restaurant")
public class Restaurant implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "restaurant_id")
    private Long restaurantId;

    private String name;

    private String location;

    private String postalCode;

    @ElementCollection(targetClass = Cuisine.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "restaurant_cuisine", joinColumns = @JoinColumn(name = "restaurant_id"))
    @Column(name = "cuisine")
    private Set<Cuisine> cuisines = new HashSet<>();

    public Restaurant(String name, String location, String postalCode, Set<Cuisine> cuisineSet) {
        this.name = name;
        this.location = location;
        this.postalCode = postalCode;
        this.cuisines = cuisineSet;
    }



}
