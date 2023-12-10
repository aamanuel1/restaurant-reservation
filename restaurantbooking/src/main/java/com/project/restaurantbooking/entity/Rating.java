package com.project.restaurantbooking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "rating")
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "rating_id")
    private Long ratingId;
    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;
    private Long rating;
    private String feedback;
    public Rating(Long rating, String feedback, Restaurant restaurant){
        this.rating = rating;
        this.feedback = feedback;
        this.restaurant = restaurant;
    }
}
