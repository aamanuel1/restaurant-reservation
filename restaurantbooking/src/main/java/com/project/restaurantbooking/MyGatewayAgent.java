package com.project.restaurantbooking;


import com.project.restaurantbooking.controller.ReservationController;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.wrapper.gateway.GatewayAgent;
import lombok.SneakyThrows;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.ApplicationContext;

public class MyGatewayAgent extends GatewayAgent {
    @Override
    protected void setup() {
        super.setup();
        System.out.println("\n==========Hello! MyGatewayAgent-agent "+getAID().getName()+" is ready.===========\n");
        addBehaviour(new CyclicBehaviour() {
            @SneakyThrows
            @Override
            public void action() {
                System.out.println("\n=== MyGatewayAgent: Receiving Msg ====\n");
                ACLMessage msg = receive();
                if (msg != null) {
                    System.out.println("\n=== MyGatewayAgent: Msg Rcd ====\n"+msg);
                    // Process incoming messages
                    String content = msg.getContent();
                    JSONObject json = new JSONObject(content);
                    String correlationId = json.getString("correlationId");

                    String responseToController = "Reservation request was processed";

                    ApplicationContext ctx = GlobalApplicationContext.getApplicationContext();
                    if (ctx != null) {
                        ReservationController controller = ctx.getBean("reservationController", ReservationController.class);
                        System.out.println("\nApplicationContext is NOT null in MyGatewayAgent\n");
                        // Use the controller as needed
                    } else {
                        System.out.println("\nApplicationContext is null in MyGatewayAgent\n");
                    }

//                    if (GlobalApplicationContext.getApplicationContext() == null) {
//                        System.out.println("ApplicationContext is null in MyGatewayAgent");
//                    } else {
//                        ReservationController reservationController = (ReservationController)
//                                GlobalApplicationContext.getApplicationContext().getBean("reservationController");
//                        reservationController.receiveResponse(correlationId, responseToController);
//                    }


//                    ReservationController reservationController = (ReservationController)
//                            GlobalApplicationContext.getApplicationContext().getBean("reservationController");
//                    reservationController.receiveResponse(correlationId, responseToController);

                } else {
                    block();
                }
            }
        });
    }
    @Override
    protected void processCommand(Object command) {
//        logger.info("GatewayAgent - Command Received");
        if (command instanceof String) {
            System.out.println("GatewayAgent - command: "+ command.toString());
            try {
                String jsonString = (String) command;
                JSONObject json = new JSONObject(jsonString);
                String targetAgent = json.getString("targetAgent");
                JSONObject data = json.getJSONObject("data");

//                String foodName = data.getString("foodName");
//                long waitTime = data.getInt("waitTime");
//                logger.info("GatewayAgent: FoodName: {}", foodName);

                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                msg.addReceiver(new AID(targetAgent, AID.ISLOCALNAME));
                msg.setContent(jsonString); // You may choose to send only the data part
                send(msg);
            } catch (JSONException e) {
//                logger.error("Error parsing JSON command: {}", e.getMessage());
                // Handle error accordingly
                System.out.println("Error parsing JSON command: {}"+ e.getMessage());
            }
        } else {
//            logger.warn("Received command of unrecognized type: {}", command.getClass().getName());
            System.out.println("Received command of unrecognized type: {}"+ command.getClass().getName());
        }
    }

    public void sendResponse(Object response) {
        releaseCommand(response);
    }
}

