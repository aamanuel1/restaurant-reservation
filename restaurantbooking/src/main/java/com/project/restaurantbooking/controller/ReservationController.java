package com.project.restaurantbooking.controller;

import com.project.restaurantbooking.messagetemplates.AgentCommand;
import com.project.restaurantbooking.repo.ReservationRepository;
import jade.wrapper.gateway.JadeGateway;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/reservation")
public class ReservationController {
    private final Map<String, AgentResponseHolder> responseMap = new ConcurrentHashMap<>();
    private final ReservationRepository reservationRepository;


    public ReservationController(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @PostMapping("/create/{food_name}/{wait_time}")
    public ResponseEntity<CompletableFuture<Object>> makeReservation(@PathVariable String food_name, @PathVariable long wait_time) {
        String correlationId = UUID.randomUUID().toString();
        AgentResponseHolder responseHolder = new AgentResponseHolder();
        responseMap.put(correlationId, responseHolder);

        System.out.println("\nReservController: ReservationRequest Received.\n");
        String msgJson = String.format("""
        {
            "correlationId": "%s",
            "targetAgent": "restaurantAgent",
            "data": {
                "foodName": "%s",
                "waitTime": "%d"
            }
        }
        """, correlationId, food_name, wait_time);

        AgentCommand agentCommand = new AgentCommand("restaurantAgent", msgJson, correlationId);
        System.out.println("AC Object: "+ agentCommand.toString());

        try {
            System.out.println("\nReservController: Sending Command to GatewayAgent.\n");
            System.out.println("\nResV Controller- Gateway isActive?:  "+ JadeGateway.isGatewayActive() +"\n");
            System.out.println("\nReservControllerCommand: "+ agentCommand.toString() +"\n");

            JadeGateway.execute(agentCommand);

            CompletableFuture<Object> result = agentCommand.getFutureResult();

            System.out.println("\nReservController: Command Sent to GatewayAgent.");
            System.out.println("ReservController: Result received."+ result +"\n");


            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CompletableFuture.completedFuture("Error sending reservation request"));
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
