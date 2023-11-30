package com.project.restaurantbooking;


import com.project.restaurantbooking.controller.ReservationController;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.wrapper.gateway.GatewayAgent;
import lombok.SneakyThrows;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class MyGatewayAgent extends GatewayAgent {
//    private static ApplicationContext context;
//    private boolean isCustomInitCalled = false;


//    public static void setApplicationContext(ApplicationContext applicationContext) {
//        System.out.println("\nMyGatewayAgent setApplicationContext - AppContext: "+ applicationContext+"\n");
//        MyGatewayAgent.context = applicationContext;
//    }


    @Override
    protected void setup() {
        System.out.println("MyGatewayAgent - setup - Agent " + getAID().getName() + " is ready.");
        ApplicationContext context = ApplicationContextProvider.getApplicationContext();
        System.out.println("MyGatewayAgent - setup - Context:"+ context);
        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                System.out.println("Performing an initial action that doesn't require ApplicationContext.");
                // Perform some initial actions. For example, you can send an initial message.
                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                msg.addReceiver(new AID("restaurantAgent", AID.ISLOCALNAME));
                String msgJSON = String.format("""
                        {
                            "correlationId": "InitialMessage",
                            "targetAgent": "restaurantAgent",
                            "data": "Initial message from MyGatewayAgent."
                        }
                        """);
                msg.setContent(msgJSON);
                send(msg);
            }
        });

//        addBehaviour(new TickerBehaviour(this, 1000) { // Check every second
//            protected void onTick() {
//                System.out.println("TickerBehaviour tick - Checking ApplicationContext...");
//                if (context != null) {
//                    System.out.println("ApplicationContext is set. Context: " + context);
//                    if (!isCustomInitCalled) {
//                        System.out.println("Calling customInit...");
//                        customInit();
//                        isCustomInitCalled = true;
//                    } else {
//                        System.out.println("customInit has already been called.");
//                    }
//                } else {
//                    System.out.println("ApplicationContext is still null.");
//                }
//            }
//        });

        // You can add more behaviors or initialization code here

        // Note: The behaviors and initializations that require ApplicationContext should be in customInit()
    }


//    public void customInit() {
////        System.out.println("\nMyGatewayAgent - customInit - AppContext: "+ context+"\n");
//        System.out.println("\n==========Hello! MyGatewayAgent-agent "+getAID().getName()+" is ready.===========\n");
//        addBehaviour(new CyclicBehaviour(this) {
//            @SneakyThrows
//            @Override
//            public void action() {
//                System.out.println("\n=== MyGatewayAgent: Receiving Msg ====\n");
//                ACLMessage msg = receive();
//                if (msg != null) {
//                    System.out.println("\n=== MyGatewayAgent: Msg Rcd ====\n"+msg);
//                    // Process incoming messages
//                    String content = msg.getContent();
//                    JSONObject json = new JSONObject(content);
//                    String correlationId = json.getString("correlationId");
//
//                    String responseToController = "Reservation request was processed";
//
////                    ApplicationContext ctx = context;
//
//
//                    if (context != null) {
//                        ReservationController controller = context.getBean(ReservationController.class);
//                        System.out.println("\nApplicationContext is NOT null in MyGatewayAgent\n");
//                        // Use the controller as needed
//                    } else {
//                        System.out.println("\nApplicationContext is null in MyGatewayAgent\n");
//                    }
//
//                } else {
//                    block();
//                }
//            }
//        });
//    }
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
                System.out.println("Error parsing JSON command: "+ e.getMessage());
            }
        } else {
//            logger.warn("Received command of unrecognized type: {}", command.getClass().getName());
            System.out.println("Received command of unrecognized type: {}"+ command.getClass().getName());
        }
    }

    public void sendResponse(Object response) {
        releaseCommand(response);
    }

    @Override
    protected void takeDown() {
        System.out.println("Agent " + getAID().getName() + " is shutting down.");
        ACLMessage shutdownMsg = new ACLMessage(ACLMessage.INFORM);
        shutdownMsg.addReceiver(new AID("restaurantAgent", AID.ISLOCALNAME));
        shutdownMsg.setContent("Agent " + getAID().getName() + " is shutting down.");
        send(shutdownMsg);

        try {
            DFService.deregister(this);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }

        super.takeDown();
    }
}

