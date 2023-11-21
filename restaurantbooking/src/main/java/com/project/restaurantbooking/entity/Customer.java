package com.project.restaurantbooking.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "customer")
public class Customer implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    private Long userId;

    private String firstName;

    private String lastName;

    private Long phoneNum;

    private String email;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private Set<Reservation> reservations = new HashSet<Reservation>();

    @JsonIgnore
    @OneToMany(mappedBy = "customer")
    private Set<FavouriteFoods> favouriteFoodsSet = new HashSet<FavouriteFoods>();

    public Customer(Long userId, String firstName, String lastName, Long phoneNum, String email) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNum = phoneNum;
        this.email = email;
    }

    public Customer(String firstName, String lastName, Long phoneNum, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNum = phoneNum;
        this.email = email;
    }
}
