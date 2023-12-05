package com.project.restaurantbooking.agent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.restaurantbooking.messagetemplates.*;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.wrapper.gateway.GatewayAgent;
import lombok.SneakyThrows;
import org.json.JSONObject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TheGatewayAgent extends GatewayAgent {
    private Map<String, AgentCommand> pendingCommands = new ConcurrentHashMap<>();

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
        if(command instanceof DeleteStaffRequest){
            DeleteStaffRequest deleteStaffRequest = (DeleteStaffRequest) command;
            ACLMessage deleteStaffRequestMessage = new ACLMessage(ACLMessage.REQUEST);
            deleteStaffRequestMessage.addReceiver(new AID("StaffAgent", AID.ISLOCALNAME));
            String deleteStaffRequestJSON = null;
            try{
                deleteStaffRequestJSON = objectMapper.writeValueAsString(deleteStaffRequest);
                deleteStaffRequestMessage.setConversationId(deleteStaffRequest.getRequestId());
                deleteStaffRequestMessage.setContent((deleteStaffRequestJSON));
                deleteStaffRequestMessage.setProtocol(deleteStaffRequest.getOperation());
            }catch(Exception e){
                e.printStackTrace();
            }
            send(deleteStaffRequestMessage);
            this.releaseCommand(deleteStaffRequest);
        }
        if(command instanceof ChangeStaffRequest){
            ChangeStaffRequest changeStaffRequest = (ChangeStaffRequest) command;
            ACLMessage changeStaffRequestMessage = new ACLMessage(ACLMessage.REQUEST);
            changeStaffRequestMessage.addReceiver(new AID("StaffAgent", AID.ISLOCALNAME));
            String changeStaffRequestJSON = null;
            try{
                changeStaffRequestJSON = objectMapper.writeValueAsString(changeStaffRequest);
                changeStaffRequestMessage.setConversationId(changeStaffRequest.getRequestId());
                changeStaffRequestMessage.setContent((changeStaffRequestJSON));
                changeStaffRequestMessage.setProtocol((changeStaffRequest.getOperation()));
            }catch(Exception e){
                e.printStackTrace();
            }
            send(changeStaffRequestMessage);
            this.releaseCommand(changeStaffRequest);
        }

        if (command instanceof AgentCommand) {
            System.out.println("\nGatewayAgent - command: " + command);
            AgentCommand agentCommand = (AgentCommand) command;
            pendingCommands.put(agentCommand.getCorrelationId(), agentCommand);

            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            msg.addReceiver(new AID(agentCommand.getTargetAgent(), AID.ISLOCALNAME));
            String agentCommandJSON;

            try {
                agentCommandJSON = agentCommand.getContent(); //objectMapper.writeValueAsString(agentCommand);
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
                    System.out.println("BackGW: "+ content);
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
