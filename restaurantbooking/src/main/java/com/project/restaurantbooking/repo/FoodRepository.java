package com.project.restaurantbooking.repo;

import com.project.restaurantbooking.entity.Food;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodRepository extends JpaRepository<Food, Long> {
}
