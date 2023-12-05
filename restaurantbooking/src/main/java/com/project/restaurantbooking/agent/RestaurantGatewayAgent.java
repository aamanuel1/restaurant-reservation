package com.project.restaurantbooking.agent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.restaurantbooking.messagetemplates.AddStaffRequest;
import com.project.restaurantbooking.messagetemplates.LoginRequest;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.wrapper.gateway.GatewayAgent;

public class RestaurantGatewayAgent extends GatewayAgent {

//    protected void setup(){
    //super(setup())
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
//        if(command instanceof DeleteStaffRequest){
//            DeleteStaffRequest deleteStaffRequest = (DeleteStaffRequest) command;
//            ACLMessage deleteStaffRequestMessage = new ACLMessage(ACLMessage.REQUEST);
//            deleteStaffRequestMessage.addReceiver(new AID("StaffAgent", AID.ISLOCALNAME));
//            String deleteStaffRequestJSON = null;
//            try{
//                deleteStaffRequestJSON = objectMapper.writeValueAsString(deleteStaffRequest);
//                deleteStaffRequestMessage.setConversationId(deleteStaffRequest.getRequestId());
//                deleteStaffRequestMessage.setContent((deleteStaffRequestJSON));
//                deleteStaffRequestMessage.setProtocol(deleteStaffRequest.getOperation());
//            }catch(Exception e){
//                e.printStackTrace();
//            }
//            send(deleteStaffRequestMessage);
//            this.releaseCommand(deleteStaffRequest);
//        }
//        if(command instanceof ChangeStaffRequest){
//            ChangeStaffRequest changeStaffRequest = (ChangeStaffRequest) command;
//            ACLMessage changeStaffRequestMessage = new ACLMessage(ACLMessage.REQUEST);
//            changeStaffRequestMessage.addReceiver(new AID("StaffAgent", AID.ISLOCALNAME));
//            String changeStaffRequestJSON = null;
//            try{
//                changeStaffRequestJSON = objectMapper.writeValueAsString(changeStaffRequest);
//                changeStaffRequestMessage.setConversationId(changeStaffRequest.getRequestId());
//                changeStaffRequestMessage.setContent((changeStaffRequestJSON));
//                changeStaffRequestMessage.setProtocol((changeStaffRequest.getOperation()));
//            }catch(Exception e){
//                e.printStackTrace();
//            }
//            send(changeStaffRequestMessage);
//            this.releaseCommand(changeStaffRequest);
//        }
        else{
            System.out.println("Not working.");
        }
    }
}
