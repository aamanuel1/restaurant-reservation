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
/**Staff entity class for establishing the database.
 *
 */
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


    /**
     * Constructor with manual staff ID argument
     * Parameters are self commenting.
     * @param staffId
     * @param restaurantID
     * @param firstName
     * @param lastName
     * @param username
     * @param isAdmin
     * @param password
     */
    public Staff(Long staffId, Long restaurantID, String firstName, String lastName, String username, boolean isAdmin, String password){
        this.staffId = staffId;
        this.restaurantID = restaurantID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.isAdmin = isAdmin;
        this.password = password;
    }

    /**
     * Constructor for automatic sequential staff ID assignment by the database.
     * @param restaurantID
     * @param firstName
     * @param lastName
     * @param username
     * @param isAdmin
     * @param password
     */
    public Staff(Long restaurantID, String firstName, String lastName, String username, boolean isAdmin, String password){
        this.restaurantID = restaurantID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.isAdmin = isAdmin;
        this.password = password;
    }

}
