package com.project.restaurantbooking.repo;

import com.project.restaurantbooking.entity.Customer;
import com.project.restaurantbooking.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findAllByCustomer(Customer customer);

    Optional<Reservation> findReservationByReservationNumber(long reservationNumber);

//    @Query(value = "SELECT * FROM reservation WHERE restaurant_id = :rest_id", nativeQuery = true)
//    List<Reservation> findByRestaurantId(@Param("rest_id") Long restaurantId);

}
