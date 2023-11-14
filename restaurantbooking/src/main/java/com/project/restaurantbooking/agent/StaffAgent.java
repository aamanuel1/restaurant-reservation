package com.project.restaurantbooking.agent;


import com.project.restaurantbooking.repo.StaffRepository;
import jade.core.Agent;
import org.springframework.stereotype.Service;

@Service
public class StaffAgent extends Agent {

    private final StaffRepository staffRepository;

    public StaffAgent(){
        this.staffRepository = null;
    }

    public StaffAgent(StaffRepository staffRepository){
        this.staffRepository = staffRepository;
    }

    protected void setup(){
        System.out.println("Testing. Placeholder behaviour");
    }
}
