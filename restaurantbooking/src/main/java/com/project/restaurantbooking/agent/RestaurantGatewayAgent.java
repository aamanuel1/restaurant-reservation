package com.project.restaurantbooking.agent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.restaurantbooking.messagetemplates.AddStaffRequest;
import com.project.restaurantbooking.messagetemplates.LoginRequest;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentController;
import jade.wrapper.gateway.GatewayAgent;
import jade.wrapper.gateway.JadeGateway;
import lombok.extern.java.Log;

public class RestaurantGatewayAgent extends GatewayAgent {

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
        else{
            System.out.println("Not working.");
        }
    }
}