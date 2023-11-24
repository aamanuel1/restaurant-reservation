package com.project.restaurantbooking.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.restaurantbooking.entity.Staff;
import com.project.restaurantbooking.messagetemplates.LoginRequest;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StaffService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private AgentController staffAgentController;

    public Optional<Staff> login(String username, String password){
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setOperation("login");
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);

        String loginJSON = null;
        try{
            loginJSON = objectMapper.writeValueAsString(loginRequest);
        } catch(Exception e){
            e.printStackTrace();
        }

        try{
            ACLMessage loginMsg = new ACLMessage(ACLMessage.REQUEST);
            loginMsg.setContent(loginJSON);
            AID staffAgentAID = new AID("StaffAgent@locahost", AID.ISGUID);
            staffAgentAID.addAddresses("http://locahost:1099/acc");
            loginMsg.addReceiver(staffAgentAID);
            staffAgentController.putO2AObject(loginMsg, AgentController.ASYNC);

        } catch(Exception e){
            e.printStackTrace();
        }

        return Optional.empty();
    }
}
