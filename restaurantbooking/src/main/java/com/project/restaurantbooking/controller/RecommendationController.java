package com.project.restaurantbooking.controller;

import com.project.restaurantbooking.messagetemplates.AgentCommand;
import jade.wrapper.gateway.JadeGateway;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<CompletableFuture<Object>> getRecommendation(){
        String correlationId = UUID.randomUUID().toString();
        AgentResponseHolder responseHolder = new AgentResponseHolder();
        responseMap.put(correlationId, responseHolder);
        String msgJson = String.format("""
        {
            "correlationId": "%s",
            "targetAgent": "customerAgent",
            "task": "recommend",
        }
        """, correlationId);

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
}
