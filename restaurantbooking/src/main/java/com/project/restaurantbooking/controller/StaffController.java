package com.project.restaurantbooking.controller;

import com.project.restaurantbooking.agent.StaffAgent;
import com.project.restaurantbooking.entity.Staff;
import jade.core.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;

import java.util.Optional;

@RestController
@CrossOrigin({"*"})
public class StaffController {

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
    public Optional<Staff> login(@RequestParam String username, @RequestParam String password){
        //Create new staff agent with authenticate ability.
        Runtime runtime = Runtime.instance();
        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.MAIN_HOST, "localhost");
        ContainerController container = runtime.createMainContainer(profile);

        try{
            AgentController agentController = container.createNewAgent("testAgent", "com.project.restaurantbooking.agent.StaffAgent", null);
        } catch (StaleProxyException e){
            e.printStackTrace();
        }
        //Run authenticate function on new staff agent.

        //Return staff agent if successful.
        return null;
    }



}
