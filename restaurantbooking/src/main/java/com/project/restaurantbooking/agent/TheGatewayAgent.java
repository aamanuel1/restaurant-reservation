package com.project.restaurantbooking.agent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.restaurantbooking.SpringContextProvider;
import com.project.restaurantbooking.messagetemplates.AddStaffRequest;
import com.project.restaurantbooking.messagetemplates.AgentCommand;
import com.project.restaurantbooking.messagetemplates.LoginRequest;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.gateway.GatewayAgent;
import lombok.SneakyThrows;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.ApplicationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TheGatewayAgent extends GatewayAgent {
    private Map<String, AgentCommand> pendingCommands = new ConcurrentHashMap<>();

//    @Override
//    protected void setup() {
//        // Your existing setup code here...
//
//        // Register the agent in the Directory Facilitator (DF)
//        System.out.println("\nStarting gateway agent.\n");
//        DFAgentDescription dfd = new DFAgentDescription();
//        dfd.setName(getAID());
//        ServiceDescription sd = new ServiceDescription();
//        sd.setType("gateway-agent");
//        sd.setName(getLocalName() + "-gateway-agent");
//        dfd.addServices(sd);
//
//        try {
//            DFService.register(this, dfd);
//        } catch (FIPAException fe) {
//            fe.printStackTrace();
//        }
//    }




    @Override
    protected void processCommand(Object command){
        //If chain of checking command types given by messagetemplates
        System.out.println("Reaching gateway agent.");
        ObjectMapper objectMapper = new ObjectMapper();

        if(command instanceof LoginRequest){
            System.out.println("Reaching gateway agent.");
            LoginRequest loginRequest = (LoginRequest) command;
            ACLMessage loginMessage = new ACLMessage(ACLMessage.REQUEST);
            loginMessage.addReceiver(new AID("StaffAgent", AID.ISLOCALNAME));
            String loginJSON = null;
            try {
                loginJSON = objectMapper.writeValueAsString(loginRequest);
                loginMessage.setConversationId(loginRequest.getRequestId());
                loginMessage.setContent(loginJSON);
                loginMessage.setProtocol(loginRequest.getOperation());
                } catch(Exception e){
                    e.printStackTrace();
                }
            send(loginMessage);
            this.releaseCommand(loginRequest);
        }
        if(command instanceof AddStaffRequest){
            AddStaffRequest addStaffRequest = (AddStaffRequest) command;
            ACLMessage addStaffRequestMessage = new ACLMessage(ACLMessage.REQUEST);
            addStaffRequestMessage.addReceiver(new AID("StaffAgent", AID.ISLOCALNAME));
            String addStaffRequestJSON = null;
            try{
                addStaffRequestJSON = objectMapper.writeValueAsString(addStaffRequest);
                addStaffRequestMessage.setConversationId(addStaffRequest.getRequestId());
                addStaffRequestMessage.setContent(addStaffRequestJSON);
                addStaffRequestMessage.setProtocol(addStaffRequest.getOperation());
            }catch(Exception e){
                e.printStackTrace();
            }
            send(addStaffRequestMessage);
            this.releaseCommand(addStaffRequest);
        }

//        if (command instanceof AgentCommand) {
//            System.out.println("GatewayAgent - command: "+ command.toString());
//            AgentCommand reserveRequest = (AgentCommand) command;
//            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
//            msg.addReceiver(new AID(reserveRequest.getTargetAgent(), AID.ISLOCALNAME));
//            String reserveRequestJSON = null;
//            try {
//                reserveRequestJSON = objectMapper.writeValueAsString(reserveRequest);
//                msg.setConversationId("reserve");
//                msg.setContent(reserveRequestJSON);
//
//                send(msg);
//
//                ACLMessage reply = blockingReceive(MessageTemplate.MatchInReplyTo("reserve"));
//
//                if (reply != null) {
//                    reserveRequest.setResult(reply.getContent());
//                } else {
//                    System.out.println("\nGatewayAgent: No reply received\n");
//                    reserveRequest.setResult("No reply received");
//                }
//            } catch (Exception e) {
//                System.out.println("Error: " + e.getMessage());
//                reserveRequest.setResult("Error: " + e.getMessage());
//            }
//            this.releaseCommand(command);
//            System.out.println("\n=== GatewayAgent: Response sent back to controller ===\n");
//        }

        if (command instanceof AgentCommand) {
            System.out.println("\nGatewayAgent - command: " + command);
            AgentCommand agentCommand = (AgentCommand) command;
            pendingCommands.put(agentCommand.getCorrelationId(), agentCommand);

            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            msg.addReceiver(new AID("restaurantAgent", AID.ISLOCALNAME));
            String agentCommandJSON;

            try {
                agentCommandJSON = objectMapper.writeValueAsString(agentCommand);
                msg.setConversationId("reserve");
                msg.setProtocol("reserve");
                msg.setContent(agentCommandJSON);
                send(msg);
                System.out.println("\nGateway: Msg Sent to RestAgent\n");
            } catch (Exception e) {
                System.out.println("Error: " + e.getClass().getSimpleName() + " - " + e.getMessage());
                agentCommand.completeFutureResult("Error: " + e.getMessage());
                pendingCommands.remove(agentCommand.getCorrelationId());
                this.releaseCommand(command);
            } finally {
//                agentCommand.completeFutureResult(result);
//                this.releaseCommand(command);
                System.out.println("\nGatewayAgent: Command Released\n");
            }
            System.out.println("\n=== GatewayAgent: Response sent back to controller ===\n");
        }
        else {
            System.out.println("Not working.");
        }
    }


    @Override
    protected void setup() {
        super.setup();
        System.out.println("\nGatewayAgent - setup - Agent " + getAID().getName() + " is ready.\n");
//        ApplicationContext context = ApplicationContextProvider.getApplicationContext();
//        System.out.println("MyGatewayAgent - setup - Context:"+ context);
//        addBehaviour(new OneShotBehaviour() {
//            @Override
//            public void action() {
//                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
//                msg.addReceiver(new AID("restaurantAgent", AID.ISLOCALNAME));
//                String msgJSON = String.format("""
//                        {
//                            "correlationId": "InitialMessage",
//                            "targetAgent": "restaurantAgent",
//                            "data": "Initial message from MyGatewayAgent."
//                        }
//                        """);
//                msg.setContent(msgJSON);
//                send(msg);
//            }
//        });

        addBehaviour(new CyclicBehaviour() {
            @SneakyThrows
            @Override
            public void action() {
                System.out.println("\n=== GatewayAgent: Receiving Msg ====\n");

                ACLMessage msg = receive();
                if (msg != null) {
                    System.out.println("\n=== GatewayAgent: Msg Rcd ====\n"+msg);
                    // Process incoming messages
                    String content = msg.getContent();
                    JSONObject json = new JSONObject(content);
                    String correlationId = json.getString("correlationId");

                    AgentCommand command = pendingCommands.get(correlationId);
                    if (command != null) {
                        command.completeFutureResult(content);
                        pendingCommands.remove(correlationId);
                        TheGatewayAgent.this.releaseCommand(command);
                    } else {
                        System.out.println("No matching command found for correlationId: " + correlationId);
                    }

                } else {
                    System.out.println("\nMessage is null: "+ msg +"\n");
                    block();
                }
            }
        });
    }


}

