package com.project.restaurantbooking.controller;

import com.project.restaurantbooking.entity.Food;
import com.project.restaurantbooking.entity.Restaurant;
import com.project.restaurantbooking.repo.FoodRepository;
import com.project.restaurantbooking.repo.RestaurantRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/restaurant")
public class RestaurantController {

    private final RestaurantRepository restaurantRepository;
    private final FoodRepository foodRepository;

    public RestaurantController(RestaurantRepository restaurantRepository, FoodRepository foodRepository) {
        this.restaurantRepository = restaurantRepository;
        this.foodRepository = foodRepository;
    }

    @GetMapping("/list")
    public ResponseEntity<List<Restaurant>> allRestaurants() {
        List<Restaurant> restaurants = restaurantRepository.findAll();
        if (restaurants.isEmpty()) throw new IllegalStateException("No Restaurant in the DB!");
        return ResponseEntity.ok(restaurants);
    }

//    @GetMapping("/byFoodAndWaitTime")
//    public ResponseEntity<List<Restaurant>> findRestaurantsByFoodAndWaitTime(@RequestParam String foodName, @RequestParam Long maxWaitTime) {
//        Optional<Food> food = foodRepository.findByName(foodName);
//        if (food.isPresent()) {
//            // Fetch restaurants that serve this cuisine and meet the wait time criteria
//            List<Restaurant> restaurants = restaurantRepository.findRestaurantsByCuisineAndWaitTime(food.get().getCuisine(), maxWaitTime);
//            return ResponseEntity.ok(restaurants);
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }

}
