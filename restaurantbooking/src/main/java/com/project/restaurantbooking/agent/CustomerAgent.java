package com.project.restaurantbooking.agent;

import com.project.restaurantbooking.SpringContextProvider;
import com.project.restaurantbooking.entity.Rating;
import com.project.restaurantbooking.entity.Restaurant;
import com.project.restaurantbooking.repo.CustomerRepository;
import com.project.restaurantbooking.repo.RatingRepository;
import com.project.restaurantbooking.repo.RestaurantRepository;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import lombok.SneakyThrows;
import org.json.JSONObject;
import org.springframework.context.ApplicationContext;

import java.util.List;

public class CustomerAgent extends Agent {
    CustomerRepository customerRepository;
    RatingRepository ratingRepository;
    RestaurantRepository restaurantRepository;
    @Override
    protected void setup() {
        System.out.println("\n=== CustomerAgent "+getAID().getName()+" is ready. ===\n");

        ApplicationContext context = SpringContextProvider.getApplicationContext();
        customerRepository = context.getBean(CustomerRepository.class);
        ratingRepository = context.getBean(RatingRepository.class);
        restaurantRepository = context.getBean(RestaurantRepository.class);

        System.out.println("\n========= CustomerAgent - setup - Context:"+ context+ "\n");

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("customer");
        sd.setName(getLocalName());
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        // Add behaviour to handle reservation requests
        addBehaviour(new LeaveFeedbackBehaviour());

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

    public class LeaveFeedbackBehaviour extends CyclicBehaviour {
        @SneakyThrows
        @Override
        public void action() {
            System.out.println("\n=== Customer Agent listing for feedback:===\n");
            // Message template to listen for reservation requests
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
            ACLMessage msg = myAgent.receive(mt);

            if (msg != null) {
                System.out.println("\n=== Customer -- Msg Rcd. ===\n"+ msg);
                System.out.println("\nCustomerAgent: Msg Sender"+ msg.getSender() +"\n");
                // Process the incoming message
                // Extract details etc.
                String content = msg.getContent();
                JSONObject json = new JSONObject(content);
                String correlationId = json.getString("correlationId");
                String task = json.getString("task");
                Long rating = json.getJSONObject("data").getLong("rating");
                String feedback = json.getJSONObject("data").getString("feedback");
                String restaurantName = json.getJSONObject("data").getString("restaurantName");

                String responseToGateway = null;

                responseToGateway = leaveFeedback(restaurantName, rating, feedback, correlationId, task);

                // sending reply
                ACLMessage reply = msg.createReply();
                reply.setPerformative(ACLMessage.INFORM);
                reply.setContent(responseToGateway);
                reply.setConversationId("feedback");
                reply.setProtocol("feedback");
                myAgent.send(reply);
                System.out.println("\nReply Sent\n");
            } else {
                System.out.println("\n=== CustomerAgent -- No Msg. ===\n"+ msg);
                block();
            }
        }

        public String leaveFeedback(String restaurantName, Long rating, String feedback, String correlationId, String task){
            Restaurant restaurant = null;
            try {
                // Assuming findRestaurantsByName returns a List<Restaurant>
                List<Restaurant> restaurants = restaurantRepository.findRestaurantsByName(restaurantName);

                if (!restaurants.isEmpty()) {
                    restaurant = restaurants.get(0);
                } else {
                    // Handle the case when no restaurant is found
                    String responseToGateway = String.format("""
                    {
                        "correlationId": "%s",
                        "task": "%s",
                        "status": "failure, Restaurant not found"
                    }
                    """, correlationId, task);
                    return responseToGateway;
                }
            } catch (Exception e) {
                e.printStackTrace();
                // Handle the exception appropriately
                String responseToGateway = String.format("""
                    {
                        "correlationId": "%s",
                        "task": "%s",
                        "status": "failure, reason unknown"
                    }
                    """, correlationId, task);
                return responseToGateway;
            }
            Rating r = new Rating(rating, feedback, restaurant);
            ratingRepository.save(r);
            String responseToGateway = String.format("""
                    {
                        "correlationId": "%s",
                        "task": "%s",
                        "status": "success"
                    }
                    """, correlationId, task);
            return responseToGateway;
        }
    }
}
