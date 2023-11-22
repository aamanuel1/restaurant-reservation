package com.project.restaurantbooking.repo;

import com.project.restaurantbooking.entity.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TableRepository extends JpaRepository<RestaurantTable, Long> {

    Optional<RestaurantTable> findByTableId(Long tableId);

    @Query(value = "SELECT * FROM restaurant_table WHERE restaurant_id = :rest_id ORDER BY table_id", nativeQuery = true)
    List<RestaurantTable> findByRestaurantId(@Param("rest_id") Long rest_id);

}
