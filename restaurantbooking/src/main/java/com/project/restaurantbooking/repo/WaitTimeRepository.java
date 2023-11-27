package com.project.restaurantbooking.repo;

import com.project.restaurantbooking.entity.Restaurant;
import com.project.restaurantbooking.entity.WaitTime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WaitTimeRepository extends JpaRepository<WaitTime, Long> {
    WaitTime findWaitTimeByRestaurant(Restaurant restaurant);
    List<WaitTime> findWaitTimesByMinutesIsLessThan(Long minutes);
}
