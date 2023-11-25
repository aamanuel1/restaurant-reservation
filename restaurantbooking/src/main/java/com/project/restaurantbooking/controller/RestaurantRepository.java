package com.project.restaurantbooking.controller;

import com.project.restaurantbooking.entity.Restaurant;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/restaurant")
public class RestaurantRepository {

    private final RestaurantRepository restaurantRepository;

    public RestaurantRepository(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

//    @GetMapping("/list")
//    public ResponseEntity<List<Restaurant>> allRestaurants() {
//        List<Restaurant> restaurants = restaurantRepository.
//    }

}
