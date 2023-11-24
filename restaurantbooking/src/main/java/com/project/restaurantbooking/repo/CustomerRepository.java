package com.project.restaurantbooking.repo;

import com.project.restaurantbooking.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {


    Optional<Customer> findCustomerByEmail(String email);

}
