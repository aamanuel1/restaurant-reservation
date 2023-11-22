package com.project.restaurantbooking.repo;

import com.project.restaurantbooking.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    Optional<Restaurant> findByRestaurantId(Long restaurantId);

    Optional<Restaurant> findByName(String name);

    List<Restaurant> findByLocation(String location);

    List<Restaurant> findByPostalCode(String postalCode);

}
