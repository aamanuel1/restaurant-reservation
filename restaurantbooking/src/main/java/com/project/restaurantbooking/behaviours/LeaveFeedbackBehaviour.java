package com.project.restaurantbooking.behaviours;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import lombok.SneakyThrows;
import org.json.JSONObject;

public class LeaveFeedbackBehaviour extends CyclicBehaviour {
    @SneakyThrows
    @Override
    public void action() {
        System.out.println("Leaving feedback:");
        // Message template to listen for reservation requests
        MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
        ACLMessage msg = myAgent.receive(mt);

        if (msg != null) {
            System.out.println("\n=== Customer -- Msg Rcd. ===\n"+ msg);
            // Process the incoming message
            // Extract details etc.
            String content = msg.getContent();
            JSONObject json = new JSONObject(content);
            String feedback = json.getString("feedback");
            Long restaurantId = json.getLong("id");

            // TODO: WRITE TO DB

            System.out.println("\nSuccessfully left feedback\n");
        } else {
            System.out.println("\n=== CustomerAgent -- No Msg. ===\n"+ msg);
            block();
        }
    }
}
