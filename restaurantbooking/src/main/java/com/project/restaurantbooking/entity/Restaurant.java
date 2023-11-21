package com.project.restaurantbooking.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "restaurant")
public class Restaurant implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "restaurant_id")
    private Long restaurantId;

    private String name;

    private String location;

    private String postalCode;

    //relationships
    @JsonIgnore
    @OneToMany(mappedBy = "restaurant")
    private Set<Staff> staffMembers = new HashSet<Staff>();

    @JsonIgnore
    @OneToMany(mappedBy = "restaurant")
    private Set<Reservation> reservations = new HashSet<Reservation>();

    @JsonIgnore
    @OneToMany(mappedBy = "restaurant")
    private Set<RestaurantTable> restaurantTables = new HashSet<RestaurantTable>();

    public Restaurant(Long restaurantId, String name, String location, String postalCode) {
        this.restaurantId = restaurantId;
        this.name = name;
        this.location = location;
        this.postalCode = postalCode;
    }

    public Restaurant(String name, String location, String postalCode) {
        this.name = name;
        this.location = location;
        this.postalCode = postalCode;
    }
}
