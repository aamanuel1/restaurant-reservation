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
                Long rating = json.getLong("rating");
                String feedback = json.getString("feedback");
                Long restaurantId = json.getLong("restaurantId");

                String responseToGateway = null;

                responseToGateway = leaveFeedback(restaurantId, rating, feedback, correlationId, task);

                System.out.println("\nSuccessfully left feedback\n");
            } else {
                System.out.println("\n=== CustomerAgent -- No Msg. ===\n"+ msg);
                block();
            }
        }

        public String leaveFeedback(Long restaurantId, Long rating, String feedback, String correlationId, String task){
            Restaurant restaurant = restaurantRepository.findById(restaurantId)
                    .orElseThrow(() -> new RuntimeException("Restaurant not found"));
            Rating r = new Rating(rating, feedback, restaurant);
            ratingRepository.save(r);
            String responseToGateway = String.format("""
                    {
                        "correlationId": "%s",
                        "task": "%s",
                    }
                    """, correlationId, task);
            return responseToGateway;
        }
    }
}
