package com.project.restaurantbooking.controller;

import com.project.restaurantbooking.entity.Shift;
import com.project.restaurantbooking.entity.Staff;
import com.project.restaurantbooking.messagetemplates.AddStaffResponse;
import com.project.restaurantbooking.messagetemplates.ChangeStaffResponse;
import com.project.restaurantbooking.messagetemplates.DeleteStaffResponse;
import com.project.restaurantbooking.service.StaffService;
import jade.core.Agent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RestController
@CrossOrigin({"*"})
public class StaffController extends Agent{

    @Autowired
    private StaffService staffService;

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
        //Delete staff based on whether Id is sent or username.
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

    @GetMapping("api/v1/searchstaff")
    public ResponseEntity<CompletableFuture<Object>> searchStaff(@RequestParam String adminUsername, @RequestParam String findUsername){
        try{
            CompletableFuture<Object> searchStaffResult = this.staffService.searchStaff(adminUsername, findUsername);
            return ResponseEntity.ok(searchStaffResult);
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CompletableFuture.completedFuture("Error sending search staff request."));
        }
    }

    @GetMapping("api/v1/returnallstaff")
    public ResponseEntity<CompletableFuture<Object>> returnAllStaff(@RequestParam String adminUsername, @RequestParam Long restaurantId){
        try{
            CompletableFuture<Object> returnAllStaffResult = this.staffService.returnAllStaff(adminUsername, restaurantId);
            return ResponseEntity.ok(returnAllStaffResult);
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CompletableFuture.completedFuture("Error returning restaurant staff."));
        }
    }

    @PostMapping("api/v1/changestaff")
    public CompletableFuture<ChangeStaffResponse> changeStaff(@RequestParam String adminUsername,
                                                              @RequestParam Long staffID,
                                                              @RequestBody Staff changeStaffAttributes){
        Long tempRestaurantID = Long.valueOf(1);
        return staffService.changeStaff(adminUsername, staffID, changeStaffAttributes);

    }

    @PostMapping("api/v1/addtable")
    public void createTable(@RequestParam Long restaurantId,
                            @RequestParam int tableOccupancyNum,
                            @RequestParam Boolean available,
                            @RequestParam(required = false) ArrayList<Shift> timeslots){
        if(timeslots == null){
            //use empty timeslots
            staffService.createEmptyTable(restaurantId, tableOccupancyNum, available);
        }
        else{
            staffService.createTable(restaurantId, tableOccupancyNum, available, timeslots);
        }
    }

    @GetMapping("api/v1/searchtables")
    public ResponseEntity<CompletableFuture<Object>> searchTables(Long restaurantId){
        try{
            CompletableFuture<Object> deleteTableResponse = this.staffService.searchTables(restaurantId);
            return ResponseEntity.ok(deleteTableResponse);
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CompletableFuture.completedFuture("Error returning restaurant staff."));
        }
    }

    @PostMapping("api/v1/deletetable")
    public ResponseEntity<CompletableFuture<Object>> deleteTable(@RequestParam String adminUsername,
                                                                 @RequestParam Long tableId){
        try{
            CompletableFuture<Object> deleteTableResponse = this.staffService.deleteTable(adminUsername, tableId);
            return ResponseEntity.ok(deleteTableResponse);
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(CompletableFuture.completedFuture("Error deleting table."));
        }
    }
}
