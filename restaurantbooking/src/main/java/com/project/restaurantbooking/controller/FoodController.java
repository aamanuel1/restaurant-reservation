package com.project.restaurantbooking.controller;

import com.project.restaurantbooking.entity.Customer;
import com.project.restaurantbooking.entity.Food;
import com.project.restaurantbooking.repo.FoodRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/food")
public class FoodController {

    private final FoodRepository foodRepository;

    public FoodController(FoodRepository foodRepository) {
        this.foodRepository = foodRepository;
    }

    @GetMapping("/list")
    public ResponseEntity<List<Food>> getAllFoods() {
        List<Food> foods = foodRepository.findAll();
        if (foods.isEmpty()) throw new IllegalStateException("No Foods in the DB!");
        return ResponseEntity.ok(foods);
    }

    @GetMapping("/item/{name}")
    public ResponseEntity<Food> findFoodByName(@PathVariable String name) {
        Food food = foodRepository.findFoodByName(name);
        if (food == null) throw new IllegalStateException("The Food, "+ name +" doesn't exist in database!");
        return ResponseEntity.ok(food);
    }
}
