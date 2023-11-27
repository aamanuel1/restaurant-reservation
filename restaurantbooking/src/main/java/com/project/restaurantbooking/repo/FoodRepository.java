package com.project.restaurantbooking.repo;

import com.project.restaurantbooking.entity.Food;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FoodRepository extends JpaRepository<Food, Long> {

    Food findFoodByName(String name);

    Optional<Food> findByName(String foodName);
}
