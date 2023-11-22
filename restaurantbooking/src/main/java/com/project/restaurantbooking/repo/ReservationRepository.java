package com.project.restaurantbooking.repo;

import com.project.restaurantbooking.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Optional<Reservation> findByReservationId(Long reservationId);

    @Query(value = "SELECT * FROM reservation WHERE restaurant_id = :rest_id", nativeQuery = true)
    List<Reservation> findByRestaurantId(@Param("rest_id") Long restaurantId);

}
