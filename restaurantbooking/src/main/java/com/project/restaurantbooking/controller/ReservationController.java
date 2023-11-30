package com.project.restaurantbooking.controller;

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
    public ResponseEntity<CompletableFuture<String>> makeReservation(@PathVariable String food_name, @PathVariable long wait_time) {
        String correlationId = UUID.randomUUID().toString();
        AgentResponseHolder responseHolder = new AgentResponseHolder();
        responseMap.put(correlationId, responseHolder);

        System.out.println("\nReservController: ReservationRequest Received.\n");
        String command = String.format("""
        {
            "correlationId": "%s",
            "targetAgent": "restaurantAgent",
            "data": {
                "foodName": "%s",
                "waitTime": "%d"
            }
        }
        """, correlationId, food_name, wait_time);

//        AgentCommand agentCommand = new AgentCommand("restaurantAgent", command);
        System.out.println("AC Object: "+ command.toString());

        try {
            System.out.println("\nReservController: Sending Command to GatewayAgent.\n");
            System.out.println("\nResV Controller- Gateway isActive?:  "+ JadeGateway.isGatewayActive() +"\n");
            System.out.println("\nReservControllerCommand: "+ command.toString() +"\n");
            // Send the message to the GatewayAgent
            JadeGateway.execute(command);
//            Object result = command.getR
            System.out.println("\nReservController: Command Sent to GatewayAgent.\n");

//            Object result = command.getResult();
            System.out.println("\nReservController: Result received."+ "result" +"\n");


            return ResponseEntity.ok(responseHolder.getResponseFuture());
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
