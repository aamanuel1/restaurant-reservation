package com.project.restaurantbooking.repo;

import com.project.restaurantbooking.entity.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ShiftRepository extends JpaRepository<Shift, Long> {

    Optional<Shift> findByShiftId(Long shiftId);

    @Query(value = "SELECT * FROM shift WHERE staff_id = :staff_id", nativeQuery = true)
    List<Shift> findByStaffId(@Param("staff_id") Long staffId);

    @Query(value = "SELECT * FROM shift WHERE staff_username = :staff_username", nativeQuery = true)
    List<Shift> findByStaffUsername(@Param("staff_username") String staffUsername);

    //TODO: Implement find by date.

}
