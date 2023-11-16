package com.project.restaurantbooking.agent;


import com.project.restaurantbooking.entity.Staff;
import com.project.restaurantbooking.repo.StaffRepository;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
        //Register the staff agent.
        DFAgentDescription dfDesc = new DFAgentDescription();
        dfDesc.setName((getAID()));
        ServiceDescription servDesc = new ServiceDescription();
        servDesc.setType("Staff");
        servDesc.setName(getLocalName() + "-Staff-agent");
        dfDesc.addServices(servDesc);
        try {
            DFService.register(this, dfDesc);
        }catch (FIPAException e){
            e.printStackTrace();
        }

        //Check for terminate message
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if(msg != null && "terminate".equals(msg.getContent())){
                    doDelete();
                }
                else{
                    block();
                }
            }
        });
    }

    protected void takeDown(){
        //Deregister the staff agent.
        try{
            DFService.deregister(this);
        } catch(FIPAException e) {
            e.printStackTrace();
        }
    }

    public Optional<Staff> authenticate(String username, String password){
        Optional<Staff> authStaff = staffRepository.findByUsername(username);
        if(authStaff.get().getPassword().equals(password))
            return authStaff;
        else
            return null;
    }
}
