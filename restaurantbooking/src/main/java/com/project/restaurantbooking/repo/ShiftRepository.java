package com.project.restaurantbooking.repo;

import com.project.restaurantbooking.entity.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ShiftRepository extends JpaRepository<Shift, Long> {

    Optional<Shift> findByShiftId(Long shiftId);

    @Query(value = "SELECT * FROM shift WHERE staff_id = :staff_id", nativeQuery = true)
    List<Shift> findByStaffId(@Param("staff_id") Long staffId);

    @Query(value = "SELECT * FROM shift WHERE staff_username = :staff_username", nativeQuery = true)
    List<Shift> findByStaffUsername(@Param("staff_username") String staffUsername);

    @Query(value = "SELECT * FROM shift WHERE date = :date", nativeQuery = true)
    List<Shift> findByDate(@Param("date") LocalDate date);

    @Query(value = "SELECT shift.*, restaurant_table.restaurant_id " +
            "FROM (shift JOIN restaurant_table ON shift.table_id=restaurant_table.table_id) " +
            "WHERE shift.date = :date AND restaurant_table.restaurant_id = :restaurant_id", nativeQuery = true)
    List<Shift> findByDateAndRestaurantId(@Param("restaurant_id") Long restaurantId, @Param("date") LocalDate date);

    @Query(value = "SELECT shift.*, restaurant_table.restaurant_id " +
            "FROM (shift JOIN restaurant_table ON shift.table_id=restaurant_table.table_id) " +
            "WHERE restaurant_table.restaurant_id = :restaurant_id", nativeQuery = true)
    List<Shift> findByRestaurantId(@Param("restaurant_id") Long restaurantId);

}
