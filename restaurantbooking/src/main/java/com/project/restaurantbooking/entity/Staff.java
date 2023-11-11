package com.project.restaurantbooking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Getter @Setter
@NoArgsConstructor
@Table(name = "staff")
public class Staff implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "staff_id")
    private Long staffId;

    private Long restaurantID;

    private String firstName;

    private String lastName;

    private String username;

    private boolean isAdmin;

    private String password;


    public Staff(Long staffId, Long restaurantID, String firstName, String lastName, String username, boolean isAdmin, String password){
        this.staffId = staffId;
        this.restaurantID = restaurantID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.isAdmin = isAdmin;
        this.password = password;
    }

    public Staff(Long restaurantID, String firstName, String lastName, String username, boolean isAdmin, String password){
        this.restaurantID = restaurantID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.isAdmin = isAdmin;
        this.password = password;
    }



}
