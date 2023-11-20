package com.project.restaurantbooking.controller;

import com.project.restaurantbooking.agent.AdminStaffAgent;
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
    private AdminStaffAgent adminStaffAgent;

    @Autowired
    public StaffController(StaffAgent staffAgent){
        this.staffAgent = staffAgent;
    }

    @GetMapping({"/"})
    public String index(){
        return "Welcome to the restaurant staff portal. Please login to continue.";
    }

    @PostMapping("/api/v1/login")
    public StaffAgent login(@RequestParam String username, @RequestParam String password) {
        //Create new staff agent with authenticate ability.
        Runtime runtime = Runtime.instance();
        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.MAIN_HOST, "localhost");
        //change this to refer to restaurant container instead of creating main container.
        ContainerController container = runtime.createMainContainer(profile);
        String agentName = username + "-sa";

        StaffAgent staff = null;
        try {
            AgentController agentController = container.createNewAgent(agentName, "com.project.restaurantbooking.agent.StaffAgent", null);
            //Run authenticate function on new staff agent.
            staff = staffAgent.authenticate(username, password);

            if (staff == null) {
                //Deregister and kill the agent.
                killStaffAgent(agentName);
            }

        } catch (StaleProxyException e) {
            e.printStackTrace();
        }

        //Return staff agent if successful.
        return staff;
    }

    @PostMapping("/api/v1/adminlogin")
    public AdminStaffAgent adminLogin(@RequestParam String username, @RequestParam String password) {
        //Create new staff agent with authenticate ability.
        Runtime runtime = Runtime.instance();
        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.MAIN_HOST, "localhost");
        //change this to refer to restaurant container instead of creating main container.
        ContainerController container = runtime.createMainContainer(profile);
        String agentName = username + "-sa";

        AdminStaffAgent adminStaff = null;
        try {
            AgentController agentController = container.createNewAgent(agentName, "com.project.restaurantbooking.agent.AdminStaffAgent", null);
            //Run authenticate function on new staff agent.
            adminStaff = (AdminStaffAgent) adminStaffAgent.authenticate(username, password);

            if (adminStaff == null) {
                //Deregister and kill the agent.
                killStaffAgent(agentName);
            }

        } catch (StaleProxyException e) {
            e.printStackTrace();
        }

        //Return staff agent if successful.
        return adminStaff;
    }

    @PostMapping("/api/v1/logout")
    public void logout(@RequestParam String username){
        String agentName = username + "-sa";
        killStaffAgent(agentName);
    }

    @PostMapping("api/v1/addstaff")
    public void addStaff(@RequestBody Staff newStaff){
        adminStaffAgent.addStaff(newStaff);
    }

    @PostMapping("api/v1/deletestaff")
    public void deleteStaff(@RequestParam(required = false) Long id, @RequestParam(required = false) String username){
        if(id != null){
            this.adminStaffAgent.deleteStaffById(id);
        }
        else if(username != null){
            this.adminStaffAgent.deleteStaffByUsername(username);
        }
        else {
            throw new IllegalArgumentException("No staff information provided.");
        }
    }

    @PostMapping("api/v1/changestaff")
    public void changeStaff(@RequestParam Long staffID,
                            @RequestParam(required = false) String newFirstName,
                            @RequestParam(required = false) String newLastName,
                            @RequestParam(required = false) String newUsername,
                            @RequestParam(required = false) boolean changeAdmin,
                            @RequestParam(required = false) String newPassword){
        Long tempRestaurantID = Long.valueOf(1);
        Staff staffChange = new Staff(tempRestaurantID,newFirstName, newLastName, newUsername, changeAdmin, newPassword);
        adminStaffAgent.changeStaffAttributes(staffID, staffChange);

    }

    private void killStaffAgent(String agentName){
        ACLMessage killMsg = new ACLMessage((ACLMessage.INFORM));
        killMsg.addReceiver(new AID(agentName, AID.ISLOCALNAME));
        killMsg.setContent("terminate");
        send(killMsg);
    }

}
