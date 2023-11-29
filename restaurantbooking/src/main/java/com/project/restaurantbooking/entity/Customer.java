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
@Table(name = "customer")
public class Customer implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private Long userId;

    private String firstName;

    private String lastName;

    private Long phoneNum;

    private String email;

    public Customer(String firstName, String lastName, Long phoneNum, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNum = phoneNum;
        this.email = email;
    }
}
