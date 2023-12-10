package com.project.restaurantbooking.agent;

import com.project.restaurantbooking.SpringContextProvider;
import com.project.restaurantbooking.entity.Rating;
import com.project.restaurantbooking.entity.Restaurant;
import com.project.restaurantbooking.repo.CustomerRepository;
import com.project.restaurantbooking.repo.RatingRepository;
import com.project.restaurantbooking.repo.RestaurantRepository;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import org.json.JSONObject;
import org.springframework.context.ApplicationContext;
import jade.core.behaviours.CyclicBehaviour;

import java.util.ArrayList;
import java.util.List;

public class RecommendationAgent extends Agent {
    RestaurantRepository restaurantRepository;
    CustomerRepository customerRepository;
    RatingRepository ratingRepository;
    @Override
    protected void setup() {
        System.out.println("\n=== RecommendationAgent "+getAID().getName()+" is ready. ===\n");

        ApplicationContext context = SpringContextProvider.getApplicationContext();
        customerRepository = context.getBean(CustomerRepository.class);
        restaurantRepository = context.getBean(RestaurantRepository.class);
        ratingRepository = context.getBean(RatingRepository.class);

        System.out.println("\n========= RecommendationAgent - setup - Context:"+ context+ "\n");

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("recommend");
        sd.setName(getLocalName());
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        // Add behaviour to handle reservation requests
        addBehaviour(new RecommendBehaviour());

        // Add other behaviours as needed, for example, handling cancellations
        // addBehaviour(new CancellationServer());

    }
    @Override
    protected void takeDown() {
        // Deregister from the yellow pages
        try {
            DFService.deregister(this);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }
    private class RecommendBehaviour extends CyclicBehaviour {
        @SneakyThrows
        @Override
        public void action() {
            System.out.println("\n=== Recommendation Agent listing for feedback:===\n");
            // Message template to listen for reservation requests
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
            ACLMessage msg = myAgent.receive(mt);

            if (msg != null) {
                System.out.println("\n=== Recommendation -- Msg Rcd. ===\n"+ msg);
                System.out.println("\nRecommendationAgent: Msg Sender"+ msg.getSender() +"\n");
                // Process the incoming message
                // Extract details etc.
                String content = msg.getContent();
                JSONObject json = new JSONObject(content);
                String correlationId = json.getString("correlationId");
                String task = json.getString("task");

                String responseToGateway = null;

                if (task.equals("get-all")) {
                    responseToGateway = getRestaurants(correlationId, task);
                } else if (task.equals("recommend")) {
                    double minRating = json.getDouble("minRating");
                    responseToGateway = getRecommended(correlationId, task, minRating);
                }

                // sending reply
                ACLMessage reply = msg.createReply();
                reply.setPerformative(ACLMessage.INFORM);
                reply.setContent(responseToGateway);
                reply.setConversationId("recommend");
                reply.setProtocol("recommend");
                myAgent.send(reply);
                System.out.println("\nReply Sent\n");
            } else {
                System.out.println("\n=== RecommendationAgent -- No Msg. ===\n"+ msg);
                block();
            }
        }
        public String getRecommended(String correlationId, String task, double minRating){
            String responseToGateway = "";
            try{
                List<Restaurant> restaurants = restaurantRepository.findAllWithCuisines();
                ArrayList<String> restaurantNames = new ArrayList<String>();
                
                for (Restaurant r: restaurants){
                    Long restaurantId = r.getRestaurantId();
                    List<Rating> ratings = ratingRepository.findByRestaurantId(restaurantId);
                    double avgRating = 0;
                    for (Rating rating: ratings){
                        avgRating += rating.getRating();
                    }
                    avgRating /= ratings.size();
                    if (avgRating >= minRating) {
                        restaurantNames.add(r.getName());
                    }
                }

                if (!restaurants.isEmpty()){
                    responseToGateway = String.format("""
                        {
                            "correlationId": "%s",
                            "task": "%s",
                            "status": "success",
                            "restaurants:": "%s"
                        }
                        """, correlationId, task, restaurantNames);

                }else {
                    responseToGateway = String.format("""
                        {
                            "correlationId": "%s",
                            "task": "%s",
                            "status": "failure, Restaurant not found"
                        }
                        """, correlationId, task);
                    return responseToGateway;
                }


                return responseToGateway;
            } catch (Exception e){
                e.printStackTrace();
                responseToGateway = String.format("""
                    {
                        "correlationId": "%s",
                        "task": "%s",
                        "status": "failure, reason unknown"
                    }
                    """, correlationId, task);
                return responseToGateway;
            }
        }

        public String getRestaurants(String correlationId, String task){
            String responseToGateway = "";
            try{
                List<Restaurant> restaurants = restaurantRepository.findAllWithCuisines();
                ArrayList<String> restaurantNames = new ArrayList<String>();

                for (Restaurant r: restaurants){
                    restaurantNames.add(r.getName());
                }

                if (!restaurants.isEmpty()){
                    responseToGateway = String.format("""
                        {
                            "correlationId": "%s",
                            "task": "%s",
                            "status": "success",
                            "restaurants:": "%s"
                        }
                        """, correlationId, task, restaurantNames);

                }else {
                    responseToGateway = String.format("""
                        {
                            "correlationId": "%s",
                            "task": "%s",
                            "status": "failure, Restaurant not found"
                        }
                        """, correlationId, task);
                    return responseToGateway;
                }


                return responseToGateway;
            } catch (Exception e){
                e.printStackTrace();
                responseToGateway = String.format("""
                    {
                        "correlationId": "%s",
                        "task": "%s",
                        "status": "failure, reason unknown"
                    }
                    """, correlationId, task);
                return responseToGateway;
            }
        }
    }
}
