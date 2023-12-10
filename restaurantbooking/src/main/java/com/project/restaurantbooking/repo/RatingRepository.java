package com.project.restaurantbooking.repo;

import com.project.restaurantbooking.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    @Query(value = "SELECT * FROM rating WHERE restaurant_id = :rest_id", nativeQuery = true)
    List<Rating> findByRestaurantId(@Param("rest_id") Long restaurantId);

}
