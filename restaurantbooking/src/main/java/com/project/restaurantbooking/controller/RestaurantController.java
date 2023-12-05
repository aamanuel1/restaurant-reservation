package com.project.restaurantbooking.controller;

import com.project.restaurantbooking.entity.Food;
import com.project.restaurantbooking.entity.Restaurant;
import com.project.restaurantbooking.messagetemplates.AgentCommand;
import com.project.restaurantbooking.repo.FoodRepository;
import com.project.restaurantbooking.repo.RestaurantRepository;
import jade.wrapper.gateway.JadeGateway;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/restaurant")
public class RestaurantController {

    private final RestaurantRepository restaurantRepository;
    private final FoodRepository foodRepository;
    private final Map<String, AgentResponseHolder> responseMap = new ConcurrentHashMap<>();

    public RestaurantController(RestaurantRepository restaurantRepository, FoodRepository foodRepository) {
        this.restaurantRepository = restaurantRepository;
        this.foodRepository = foodRepository;
    }

    @GetMapping("/list")
    public ResponseEntity<List<Restaurant>> allRestaurants() {
        List<Restaurant> restaurants = restaurantRepository.findAll();
        if (restaurants.isEmpty()) throw new IllegalStateException("No Restaurant in the DB!");
        return ResponseEntity.ok(restaurants);
    }

    @GetMapping("/inquire")
    public ResponseEntity<CompletableFuture<Object>> inquiryFoodServed(@RequestParam String foodName) {
        String correlationId = UUID.randomUUID().toString();
        AgentResponseHolder responseHolder = new AgentResponseHolder();
        responseMap.put(correlationId, responseHolder);

        System.out.println("\nRestController: Sending inquiry.\n");

        String msgJson = String.format("""
        {
            "correlationId": "%s",
            "targetAgent": "restaurantAgent",
            "task": "inquire",
            "data": {
                "foodName": "%s",
            }
        }
        """, correlationId, foodName);
        AgentCommand inquiryCommand = new AgentCommand("restaurantAgent", msgJson, correlationId, "inquire");

        System.out.println("AC Object: "+ inquiryCommand.toString());

        try {
            System.out.println("\nRestController- Gateway isActive?:  "+ JadeGateway.isGatewayActive() +"\n");
            System.out.println("\nRestControllerCommand: "+ inquiryCommand +"\n");

            JadeGateway.execute(inquiryCommand);

            CompletableFuture<Object> result = inquiryCommand.getFutureResult();

            System.out.println("\nRestController: Command Sent to GatewayAgent.");
            System.out.println("RestController: Result received."+ result +"\n");

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CompletableFuture.completedFuture("Error sending inquiry request"));
        }

        /*==============*/

//        Food food = foodRepository.findFoodByName(foodName);
//        if (food == null) {
//            return (ResponseEntity<List<Restaurant>>) ResponseEntity.notFound();
//        } else {
//            List<Restaurant> restaurantList = restaurantRepository.findRestaurantsByCuisine(food.getCuisine());
//            return ResponseEntity.ok(restaurantList);
//        }
    }



}
