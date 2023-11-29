package com.project.restaurantbooking.controller;

import com.project.restaurantbooking.entity.Customer;
import com.project.restaurantbooking.repo.CustomerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/customer")
public class CustomerController {
    private final CustomerRepository customerRepository;

    public CustomerController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @GetMapping("/list")
    public ResponseEntity<List<Customer>> allCustomers() {
        List<Customer> customers = customerRepository.findAll();
        if (customers.isEmpty()) throw new IllegalStateException("No Customer in the DB!");
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/profile/{email}")
    public ResponseEntity<Customer> findCustomerByEmail(@PathVariable String email) {
        Customer customer = customerRepository.findCustomerByEmail(email);
        if (customer == null) throw new IllegalStateException("Customer doesn't exist in database!");
        return ResponseEntity.ok(customer);
    }

    @GetMapping("/profile/id/{id}")
    public ResponseEntity<Customer> findCustomerById(@PathVariable Long id) {
        Customer customer = customerRepository.findById(id).orElse(null);
        if (customer == null) throw new IllegalStateException("Customer doesn't exist in database!");
        return ResponseEntity.ok(customer);
    }

    @PostMapping("/add")
    public ResponseEntity<Customer> addCustomer(@RequestBody Customer newCustomer) {
        Customer customer =customerRepository.save(Customer
                .builder()
                .email(newCustomer.getEmail())
                .firstName(newCustomer.getFirstName())
                .lastName(newCustomer.getLastName())
                .phoneNum(newCustomer.getPhoneNum())
                .build());
        return ResponseEntity.ok(customer);
    }





}
