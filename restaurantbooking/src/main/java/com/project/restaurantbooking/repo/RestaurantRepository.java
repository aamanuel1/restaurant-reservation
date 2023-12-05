package com.project.restaurantbooking.repo;

import com.project.restaurantbooking.entity.Restaurant;
import com.project.restaurantbooking.enums.Cuisine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    List<Restaurant> findRestaurantsByName(String name);

    List<Restaurant> findRestaurantsByLocation(String location);

    List<Restaurant> findRestaurantsByPostalCode(String postalCode);
    @Query("SELECT r FROM Restaurant r JOIN r.cuisines c WHERE c = :cuisine")
    List<Restaurant> findRestaurantsByCuisine(@Param("cuisine") Cuisine cuisine);

    Optional<Restaurant> findFirstByCuisinesContains(Cuisine cuisine);


//    @Query("SELECT r FROM Restaurant r JOIN r.cuisines c WHERE c = :cuisine AND r.wait_time.minutes <= :waitTime")
//    List<Restaurant> findRestaurantsByCuisineAndWaitTime(@Param("cuisine") Cuisine cuisine, @Param("waitTime") Long waitTime);

}
