package com.project.restaurantbooking.agent;


import com.project.restaurantbooking.repo.StaffRepository;
import org.springframework.stereotype.Service;

@Service
public class StaffAgent {

    private final StaffRepository staffRepository;

    public StaffAgent(StaffRepository staffRepository){
        this.staffRepository = staffRepository;
    }
}
