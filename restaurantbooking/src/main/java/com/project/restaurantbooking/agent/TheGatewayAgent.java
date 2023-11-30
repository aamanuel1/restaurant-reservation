package com.project.restaurantbooking.agent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.restaurantbooking.messagetemplates.AddStaffRequest;
import com.project.restaurantbooking.messagetemplates.LoginRequest;
import jade.core.AID;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.gateway.GatewayAgent;

public class TheGatewayAgent extends GatewayAgent {

//    protected void setup(){
//        System.out.println("Restaurant agent started.");
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

        if (command instanceof String) {
            System.out.println("GatewayAgent - command: "+ command.toString());
            String reserveRequest = (String) command;
            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            msg.addReceiver(new AID("restaurantAgent", AID.ISLOCALNAME));
//            String reserveRequestJSON = null;
            try {
//                reserveRequestJSON = objectMapper.writeValueAsString(reserveRequest);
                msg.setConversationId("reserve");
                msg.setContent(reserveRequest);

                send(msg);

                ACLMessage reply = blockingReceive(MessageTemplate.MatchInReplyTo("reserve"));

                if (reply != null) {
                    System.out.println("\nReply from RestAgent to Gateway\n"+ reply);
                } else {
                    System.out.println("\nGatewayAgent: No reply received\n");
//                    reserveRequest.setResult("No reply received");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
//                reserveRequest.setResult("Error: " + e.getMessage());
            }
            this.releaseCommand(command);
            System.out.println("\n=== GatewayAgent: Response sent back to controller ===\n");
        }
        else{
            System.out.println("Not working.");
        }
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
