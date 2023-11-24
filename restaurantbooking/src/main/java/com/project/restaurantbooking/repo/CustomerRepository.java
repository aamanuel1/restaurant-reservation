package com.project.restaurantbooking.repo;

import com.project.restaurantbooking.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {


    Customer findCustomerByEmail(String email);

}
