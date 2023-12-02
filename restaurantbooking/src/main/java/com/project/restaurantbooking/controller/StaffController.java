package com.project.restaurantbooking.controller;

import com.project.restaurantbooking.agent.AdminStaffAgent;
import com.project.restaurantbooking.agent.StaffAgent;
import com.project.restaurantbooking.entity.Shift;
import com.project.restaurantbooking.entity.Staff;
import com.project.restaurantbooking.messagetemplates.AddStaffResponse;
import com.project.restaurantbooking.messagetemplates.DeleteStaffRequest;
import com.project.restaurantbooking.messagetemplates.DeleteStaffResponse;
import com.project.restaurantbooking.service.StaffService;
import jade.core.*;
import jade.core.Agent;
import jade.core.Runtime;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RestController
@CrossOrigin({"*"})
public class StaffController extends Agent{

    @Autowired
    private StaffService staffService;

//    @Autowired
//    private AdminStaffAgent adminStaffAgent;

    @Autowired
    public StaffController(StaffService staffService){
        this.staffService = staffService;
    }

    @GetMapping({"/"})
    public String index(){
        return "Welcome to the restaurant staff portal. Please login to continue.";
    }

    @PostMapping("/api/v1/login")
    public CompletableFuture<Optional<Staff>> login(@RequestParam String username, @RequestParam String password) {
        CompletableFuture<Optional<Staff>> loginStaff = staffService.login(username, password);
        return loginStaff;
    }

    @PostMapping("/api/v1/logout")
    public void logout(@RequestParam String username){
    }

    @PostMapping("api/v1/addstaff")
    public CompletableFuture<AddStaffResponse> addStaff(@RequestParam String username, @RequestBody Staff newStaff){
        return staffService.addStaff(username, newStaff);
    }

    @PostMapping("api/v1/deletestaff")
    public CompletableFuture<DeleteStaffResponse> deleteStaff(@RequestParam(required = false) String username,
                                                              @RequestParam(required = false) Long deleteId,
                                                              @RequestParam(required = false) String deleteUsername){
        if(deleteId != null){
            return this.staffService.deleteStaffById(username, deleteId);
        }
        else if(deleteUsername != null){
            return this.staffService.deleteStaffByUsername(username, deleteUsername);
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
        Staff staffChange = new Staff(newFirstName, newLastName, newUsername, changeAdmin, newPassword);
        // adminStaffAgent.changeStaffAttributes(staffID, staffChange);

    }

    @PostMapping("api/v1/addtable")
    public void createTable(@RequestParam Long restaurantId,
                            @RequestParam int tableOccupancyNum,
                            @RequestParam boolean available,
                            @RequestParam(required = false) ArrayList<Shift> timeslots){
        if(timeslots == null){
            //use empty timeslots
//            adminStaffAgent.createEmptyTable(restaurantId, tableOccupancyNum, available);
        }
        else{
//            adminStaffAgent.createTable(restaurantId, tableOccupancyNum, available, timeslots);
        }
    }

    @PostMapping("api/v1/deletetable")
    public void deleteTable(){

    }

}
