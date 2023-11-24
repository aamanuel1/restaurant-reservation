package com.project.restaurantbooking.repo;

import com.project.restaurantbooking.entity.FavouriteFoods;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavouriteFoodRepository extends JpaRepository<FavouriteFoods, Long> {
}
