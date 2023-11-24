package com.project.restaurantbooking.repo;

import com.project.restaurantbooking.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StaffRepository extends JpaRepository<Staff, Long> {

//    Optional<Staff> findUserByStaffId(Long staffId);

    Optional<Staff> findByUsername(String username);
}
