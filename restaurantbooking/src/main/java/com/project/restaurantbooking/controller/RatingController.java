package com.project.restaurantbooking.controller;

import com.project.restaurantbooking.messagetemplates.AgentCommand;
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
@RequestMapping("/api/rating")
public class RatingController {
    private final Map<String, AgentResponseHolder> responseMap = new ConcurrentHashMap<>();
    @PostMapping
    public ResponseEntity<CompletableFuture<Object>> createRating(@RequestParam String restaurantName, @RequestParam long rating, @RequestParam String feedback) {
        String correlationId = UUID.randomUUID().toString();
        AgentResponseHolder responseHolder = new AgentResponseHolder();
        responseMap.put(correlationId, responseHolder);
        String msgJson = String.format("""
        {
            "correlationId": "%s",
            "targetAgent": "customerAgent",
            "task": "feedback",
            "data": {
                "rating": "%d",
                "feedback": "%s",
                "restaurantName": "%s",
            }
        }
        """, correlationId, rating, feedback, restaurantName);

        AgentCommand agentCommand = new AgentCommand("customerAgent", msgJson, correlationId, "feedback");
        try {
            JadeGateway.execute(agentCommand);
            CompletableFuture<Object> result = agentCommand.getFutureResult();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CompletableFuture.completedFuture("Error sending feedback request"));
        }
    }

}
