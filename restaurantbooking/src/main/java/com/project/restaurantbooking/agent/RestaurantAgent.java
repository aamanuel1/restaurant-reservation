package com.project.restaurantbooking.agent;

import com.project.restaurantbooking.ApplicationContextProvider;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import lombok.SneakyThrows;
import org.json.JSONObject;
import org.springframework.context.ApplicationContext;

public class RestaurantAgent extends Agent {
    // Consider using a data structure to store restaurant information
    // For example, a list or map of available tables, reservations, etc.

    @Override
    protected void setup() {
        System.out.println("\n=== RestaurantAgent "+getAID().getName()+" is ready. ===\n");
        ApplicationContext context = ApplicationContextProvider.getApplicationContext();
        System.out.println("\n========= RestaurantAgent - setup - Context:"+ context+ "\n");
        // Register the restaurant-reservation service in the yellow pages
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("restaurant-reservation");
        sd.setName(getLocalName());
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        // Add behaviour to handle reservation requests
        addBehaviour(new ReservationRequestsServer());

        // Add other behaviours as needed, for example, handling cancellations
        // addBehaviour(new CancellationServer());
    }

    // Define inner class for handling reservation requests
    private class ReservationRequestsServer extends CyclicBehaviour {
        @SneakyThrows
        @Override
        public void action() {
            System.out.println("\n=== RestaurantAgent -- Receiving msg. ===\n");
            // Message template to listen for reservation requests
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
            ACLMessage msg = myAgent.receive(mt);

            if (msg != null) {
                System.out.println("\n=== RestaurantAgent -- Msg Rcd. ===\n"+ msg);
                // Process the incoming message
                // Extract details, make a reservation, etc.
                String content = msg.getContent();
                JSONObject json = new JSONObject(content);
                String correlationId = json.getString("correlationId");

                String responseToGateway = String.format("""
                        {
                            "correlationId": "%s",
                            "message": "Reservation confirmed"
                        }
                        """, correlationId);

                // Optionally send a reply
                ACLMessage reply = msg.createReply();
                reply.setPerformative(ACLMessage.INFORM);
                reply.setContent(responseToGateway);
                myAgent.send(reply);
                System.out.println("\nReply Sent\n");
            } else {
                System.out.println("\n=== RestaurantAgent -- No Msg. ===\n"+ msg);
                block();
            }
        }
    }

    // Define other inner classes for additional behaviors as needed

    @Override
    protected void takeDown() {
        // Deregister from the yellow pages
        try {
            DFService.deregister(this);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }
}
