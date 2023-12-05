package com.project.restaurantbooking.repo;

import com.project.restaurantbooking.entity.Customer;
import com.project.restaurantbooking.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findAllByCustomer(Customer customer);

//    Optional<Reservation> findByReservationId(Long reservationId);

//    @Query(value = "SELECT * FROM reservation WHERE restaurant_id = :rest_id", nativeQuery = true)
//    List<Reservation> findByRestaurantId(@Param("rest_id") Long restaurantId);

}
