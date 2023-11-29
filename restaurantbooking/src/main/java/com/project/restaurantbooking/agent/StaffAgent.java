package com.project.restaurantbooking.agent;


import com.project.restaurantbooking.SpringContextProvider;
import com.project.restaurantbooking.behaviours.AddStaffBehaviour;
import com.project.restaurantbooking.behaviours.LoginBehaviour;
import com.project.restaurantbooking.entity.Staff;
import com.project.restaurantbooking.repo.StaffRepository;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StaffAgent extends Agent {

    protected StaffRepository staffRepository;

    protected void setup(){
        System.out.println("Testing. Placeholder behaviour");
        ApplicationContext context = SpringContextProvider.getApplicationContext();
        staffRepository = context.getBean(StaffRepository.class);
        //Register the staff agent.
        DFAgentDescription dfDesc = new DFAgentDescription();
        dfDesc.setName((getAID()));
        ServiceDescription servDesc = new ServiceDescription();
        servDesc.setType("Staff");
        servDesc.setName(getLocalName());
        dfDesc.addServices(servDesc);
        try {
            DFService.register(this, dfDesc);
        }catch (FIPAException e){
            e.printStackTrace();
        }

        setEnabledO2ACommunication(true, 0);
        addBehaviour(new LoginBehaviour(this, staffRepository));
        addBehaviour(new AddStaffBehaviour(this));

    }

    protected void takeDown(){
        //Deregister the staff agent.
        try{
            DFService.deregister(this);
        } catch(FIPAException e) {
            e.printStackTrace();
        }
    }

    public StaffAgent authenticate(String username, String password){
        Optional<Staff> authStaff = staffRepository.findByUsername(username);
        if(authStaff.get().getPassword().equals(password))
            return this;
        else
            return null;
    }
}
