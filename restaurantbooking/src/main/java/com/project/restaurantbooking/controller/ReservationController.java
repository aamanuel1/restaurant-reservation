package com.project.restaurantbooking.controller;

import com.project.restaurantbooking.entity.Food;
import com.project.restaurantbooking.entity.Restaurant;
import com.project.restaurantbooking.messagetemplates.AgentCommand;
import com.project.restaurantbooking.repo.FoodRepository;
import com.project.restaurantbooking.repo.ReservationRepository;
import com.project.restaurantbooking.repo.RestaurantRepository;
import jade.wrapper.gateway.JadeGateway;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/reservation")
public class ReservationController {
    private final Map<String, AgentResponseHolder> responseMap = new ConcurrentHashMap<>();
    private final ReservationRepository reservationRepository;
    private final FoodRepository foodRepository;
    private final RestaurantRepository restaurantRepository;


    public ReservationController(ReservationRepository reservationRepository, FoodRepository foodRepository,
                                 RestaurantRepository restaurantRepository) {
        this.reservationRepository = reservationRepository;
        this.foodRepository = foodRepository;
        this.restaurantRepository = restaurantRepository;
    }

    @GetMapping("/fetch/all")
    public ResponseEntity<CompletableFuture<Object>> getAllReservationsForCustomer(@RequestParam String email) {
        String correlationId = UUID.randomUUID().toString();
        AgentResponseHolder responseHolder = new AgentResponseHolder();
        responseMap.put(correlationId, responseHolder);
        String msgJson = String.format("""
        {
            "correlationId": "%s",
            "targetAgent": "restaurantAgent",
            "task": "getAllReservations",
            "data": {
                "email": "%s",
            }
        }
        """, correlationId, email);
        AgentCommand agentCommand = new AgentCommand("restaurantAgent", msgJson, correlationId, "getAllReservations");
        try {
            JadeGateway.execute(agentCommand);
            CompletableFuture<Object> result = agentCommand.getFutureResult();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CompletableFuture.completedFuture("Error fetching customer's reservations"));
        }
    }

    @PostMapping("/create")
    public ResponseEntity<CompletableFuture<Object>> makeReservation(@RequestParam String foodName, @RequestParam long waitTime, @RequestParam String email) {
        String correlationId = UUID.randomUUID().toString();
        AgentResponseHolder responseHolder = new AgentResponseHolder();
        responseMap.put(correlationId, responseHolder);
        String msgJson = String.format("""
        {
            "correlationId": "%s",
            "targetAgent": "restaurantAgent",
            "task": "reserve",
            "data": {
                "foodName": "%s",
                "waitTime": "%d",
                "email": "%s",
            }
        }
        """, correlationId, foodName, waitTime, email);

        AgentCommand agentCommand = new AgentCommand("restaurantAgent", msgJson, correlationId, "reserve");
        try {
            JadeGateway.execute(agentCommand);
            CompletableFuture<Object> result = agentCommand.getFutureResult();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CompletableFuture.completedFuture("Error sending reservation request"));
        }
    }

    @GetMapping("/fetch/one")
    public ResponseEntity<CompletableFuture<Object>> getReservationDetails(@RequestParam long reservationNumber) {
        String correlationId = UUID.randomUUID().toString();
        AgentResponseHolder responseHolder = new AgentResponseHolder();
        responseMap.put(correlationId, responseHolder);
        String msgJson = String.format("""
        {
            "correlationId": "%s",
            "targetAgent": "restaurantAgent",
            "task": "getReservationDetails",
            "data": {
                "reservationNumber": %d,
            }
        }
        """, correlationId, reservationNumber);
        AgentCommand agentCommand = new AgentCommand("restaurantAgent", msgJson, correlationId, "getReservationDetails");
        try {
            JadeGateway.execute(agentCommand);
            CompletableFuture<Object> result = agentCommand.getFutureResult();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CompletableFuture.completedFuture("Error fetching customer's reservation"));
        }
    }

    @GetMapping("/cancel")
    public ResponseEntity<CompletableFuture<Object>> cancelReservation(@RequestParam long reservationNumber) {
        String correlationId = UUID.randomUUID().toString();
        AgentResponseHolder responseHolder = new AgentResponseHolder();
        responseMap.put(correlationId, responseHolder);
        String msgJson = String.format("""
        {
            "correlationId": "%s",
            "targetAgent": "restaurantAgent",
            "task": "cancelReservation",
            "data": {
                "reservationNumber": %d,
            }
        }
        """, correlationId, reservationNumber);
        AgentCommand agentCommand = new AgentCommand("restaurantAgent", msgJson, correlationId, "cancelReservation");
        try {
            JadeGateway.execute(agentCommand);
            CompletableFuture<Object> result = agentCommand.getFutureResult();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CompletableFuture.completedFuture("Error canceling the reservation"));
        }
    }

    public void receiveResponse(String correlationId, String response) {
        AgentResponseHolder responseHolder = responseMap.get(correlationId);
        if (responseHolder != null) {
            responseHolder.complete(response);
            responseMap.remove(correlationId);
        }
    }


}
