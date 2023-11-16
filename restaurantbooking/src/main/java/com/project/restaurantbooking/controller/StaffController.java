package com.project.restaurantbooking.controller;

import com.project.restaurantbooking.agent.StaffAgent;
import com.project.restaurantbooking.entity.Staff;
import jade.core.*;
import jade.core.Agent;
import jade.core.Runtime;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@CrossOrigin({"*"})
public class StaffController extends Agent{

    @Autowired
    private StaffAgent staffAgent;

    @Autowired
    public StaffController(StaffAgent staffAgent){
        this.staffAgent = staffAgent;
    }

    @GetMapping({"/"})
    public String index(){
        return "Welcome to the restaurant staff portal. Please login to continue.";
    }

    @PostMapping("/api/v1/login")
    public Optional<Staff> login(@RequestParam String username, @RequestParam String password) {
        //Create new staff agent with authenticate ability.
        Runtime runtime = Runtime.instance();
        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.MAIN_HOST, "localhost");
        //change this to refer to restaurant container instead of creating main container.
        ContainerController container = runtime.createMainContainer(profile);
        String agentName = username + "-sa";

        Optional<Staff> staff = null;
        try {
            AgentController agentController = container.createNewAgent(agentName, "com.project.restaurantbooking.agent.StaffAgent", null);
            //Run authenticate function on new staff agent.
            staff = staffAgent.authenticate(username, password);

            if (staff == null) {
                //Deregister and kill the agent.
                ACLMessage killMsg = new ACLMessage((ACLMessage.INFORM));
                killMsg.addReceiver(new AID(agentName, AID.ISLOCALNAME));
                killMsg.setContent("terminate");
                send(killMsg);
            }
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }

        //Return staff agent if successful.
        return staff;
    }

}
