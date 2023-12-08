package com.project.restaurantbooking.controller;

import com.project.restaurantbooking.entity.RestaurantTable;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    public ResponseEntity<CompletableFuture<Object>> createTable(@RequestParam String adminUsername,
                            @RequestParam Long restaurantId,
                            @RequestParam int tableOccupancyNum,
                            @RequestParam Boolean available){
        try{
            CompletableFuture<Object> createTableResponse = this.staffService.createTable(adminUsername, restaurantId, tableOccupancyNum, available);
            return ResponseEntity.ok(createTableResponse);
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CompletableFuture.completedFuture("Error returning restaurant staff."));
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

    @PostMapping("api/v1/changetable")
    public ResponseEntity<CompletableFuture<Object>> changeTable(@RequestParam String adminUsername,
                                                                 @RequestParam Long tableId,
                                                                 @RequestBody RestaurantTable changeTableAttributes){
        try{
            CompletableFuture<Object> deleteTableResponse = this.staffService.changeTableAttributes(adminUsername, tableId, changeTableAttributes);
            return ResponseEntity.ok(deleteTableResponse);
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CompletableFuture.completedFuture("Error changing table."));
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

    @PostMapping("api/v1/createshift")
    public ResponseEntity<CompletableFuture<Object>> createShift(@RequestParam String adminUsername,
                                                                 @RequestParam Long tableId,
                                                                 @RequestParam LocalDate date,
                                                                 @RequestParam LocalDateTime startTime,
                                                                 @RequestParam LocalDateTime endTime){
        try{
            CompletableFuture<Object> createShiftResponse = this.staffService.createShift(adminUsername, tableId, date, startTime, endTime);
            return ResponseEntity.ok(createShiftResponse);
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CompletableFuture.completedFuture("Error creating shift."));
        }
    }


    @PostMapping("api/v1/deleteshift")
    public ResponseEntity<CompletableFuture<Object>> deleteShift(String adminUsername, Long shiftId){
        try{
            CompletableFuture<Object> deleteShiftResponse = this.staffService.deleteShift(adminUsername, shiftId);
            return ResponseEntity.ok(deleteShiftResponse);
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CompletableFuture.completedFuture("Error deleting shift."));
        }
    }

    @GetMapping("api/v1/searchshift")
    public ResponseEntity<CompletableFuture<Object>> searchShifts(String username, Long shiftId){
        try{
            CompletableFuture<Object> searchShiftResponse = this.staffService.searchShift(username, shiftId);
            return ResponseEntity.ok(searchShiftResponse);
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CompletableFuture.completedFuture("Error searching for shifts."));
        }
    }

    @GetMapping("api/v1/searchshiftbyday")
    public ResponseEntity<CompletableFuture<Object>> searchShiftByDay(String username, Long restaurantId, LocalDate day){
        try{
            CompletableFuture<Object> searchShiftResponse = this.staffService.searchShiftByDay(username, restaurantId, day);
            return ResponseEntity.ok(searchShiftResponse);
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CompletableFuture.completedFuture("Error searching for shifts."));
        }
    }

    @GetMapping("api/v1/returnallshifts")
    public ResponseEntity<CompletableFuture<Object>> returnAllShifts(@RequestParam String username, @RequestParam Long restaurantId){
        try{
            CompletableFuture<Object> returnAllShiftsResponse = this.staffService.returnAllShifts(username, restaurantId);
            return ResponseEntity.ok(returnAllShiftsResponse);
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CompletableFuture.completedFuture("Error searching for shifts."));
        }
    }
}
