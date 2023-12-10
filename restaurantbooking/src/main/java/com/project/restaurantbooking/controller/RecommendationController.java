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
@RequestMapping("/api/recommendation")
public class RecommendationController {
    private final Map<String, AgentResponseHolder> responseMap = new ConcurrentHashMap<>();
    @GetMapping
    public ResponseEntity<CompletableFuture<Object>> getRecommendation(@RequestParam double minRating){
        String correlationId = UUID.randomUUID().toString();
        AgentResponseHolder responseHolder = new AgentResponseHolder();
        responseMap.put(correlationId, responseHolder);
        String msgJson = String.format("""
        {
            "correlationId": "%s",
            "targetAgent": "customerAgent",
            "task": "recommend",
            "minRating": "%f"
        }
        """, correlationId, minRating);

        AgentCommand agentCommand = new AgentCommand("recommendationAgent", msgJson, correlationId, "recommend");
        try {
            JadeGateway.execute(agentCommand);
            CompletableFuture<Object> result = agentCommand.getFutureResult();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CompletableFuture.completedFuture("Error sending recommendation request"));
        }
    }

    @GetMapping("/fetch/all")
    public ResponseEntity<CompletableFuture<Object>> getAllRestaurants(){
        String correlationId = UUID.randomUUID().toString();
        AgentResponseHolder responseHolder = new AgentResponseHolder();
        responseMap.put(correlationId, responseHolder);
        String msgJson = String.format("""
        {
            "correlationId": "%s",
            "targetAgent": "customerAgent",
            "task": "get-all",
        }
        """, correlationId);

        AgentCommand agentCommand = new AgentCommand("recommendationAgent", msgJson, correlationId, "get-all");
        try {
            JadeGateway.execute(agentCommand);
            CompletableFuture<Object> result = agentCommand.getFutureResult();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CompletableFuture.completedFuture("Error sending recommendation request"));
        }
    }
}
