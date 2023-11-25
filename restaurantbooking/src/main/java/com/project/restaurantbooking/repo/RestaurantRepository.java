package com.project.restaurantbooking.repo;

import com.project.restaurantbooking.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    Restaurant findRestaurantByName(String name);

    List<Restaurant> findRestaurantsByLocation(String location);

    List<Restaurant> findRestaurantsByPostalCode(String postalCode);

}
