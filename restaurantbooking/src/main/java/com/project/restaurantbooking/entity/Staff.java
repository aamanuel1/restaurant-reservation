package com.project.restaurantbooking.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.Table;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter @Setter
@NoArgsConstructor
@Table(name = "staff")
/**Staff entity class for establishing the database.
 *
 */
public class Staff implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "staff_id")
    private Long staffId;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @JsonIgnore
    @OneToMany(mappedBy = "staff")
    private Set<Shift> shifts = new HashSet<Shift>();

    private String firstName;

    private String lastName;

    private String username;

    private boolean isAdmin;

    private String password;


    /**
     * Constructor with manual staff ID argument
     * Parameters are self commenting.
     * @param staffId
     * @param restaurant
     * @param firstName
     * @param lastName
     * @param username
     * @param isAdmin
     * @param password
     */
    public Staff(Long staffId, Restaurant restaurant, String firstName, String lastName, String username, boolean isAdmin, String password){
        this.staffId = staffId;
        this.restaurant = restaurant;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.isAdmin = isAdmin;
        this.password = password;
    }

    /**
     * Constructor for automatic sequential staff ID assignment by the database.
     * @param restaurant
     * @param firstName
     * @param lastName
     * @param username
     * @param isAdmin
     * @param password
     */
    public Staff(Restaurant restaurant, String firstName, String lastName, String username, boolean isAdmin, String password){
        this.restaurant = restaurant;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.isAdmin = isAdmin;
        this.password = password;
    }

}
