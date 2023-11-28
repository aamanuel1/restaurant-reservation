package com.project.restaurantbooking.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.restaurantbooking.entity.Staff;
import com.project.restaurantbooking.messagetemplates.AddStaffRequest;
import com.project.restaurantbooking.messagetemplates.AddStaffResponse;
import com.project.restaurantbooking.messagetemplates.LoginRequest;
import com.project.restaurantbooking.messagetemplates.LoginResponse;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.wrapper.gateway.JadeGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class StaffService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final ConcurrentHashMap<String, CompletableFuture<?>> pendingRequests = new ConcurrentHashMap<>();

//    private final ContainerController jadeContainer;

    @Autowired
    @Qualifier("StaffAgentRequestChannel")
    private DirectChannel staffAgentRequestChannel;

    @Autowired
    @Qualifier("StaffAgentResponseChannel")
    private DirectChannel staffAgentResponseChannel;

//    @Autowired
//    public StaffService(ContainerController jadeContainer){
//        this.jadeContainer = jadeContainer;
//    }

    @ServiceActivator(inputChannel = "staffAgentRequestChannel")
    public void sendRequestToStaffAgent(String message){
        //Convert to ACLMessage and send to JADE Gateway
        ACLMessage agentMessage = new ACLMessage(ACLMessage.REQUEST);
        agentMessage.addReceiver(new AID("GatewayAgent", AID.ISLOCALNAME));
        agentMessage.setContent(message);
//        try{
//            AgentController gatewayAgent = jadeContainer.getAgent("GatewayAgent");
//            gatewayAgent.putO2AObject(agentMessage, false);
//        } catch(Exception e){
//            e.printStackTrace();
//        }
    }

    @ServiceActivator(inputChannel = "StaffAgentReplyChannel")
    public void receiveResponseFromStaffAgent(Message<String> message){
        //Determine which response type this is and what response we're looking at through hashmap.
        MessageHeaders headers = message.getHeaders();
        String requestId = headers.get("requestId", String.class);
        CompletableFuture<?> futureResponse = pendingRequests.remove(requestId);
        String messageJSON = (String) message.getPayload();

        //Extract the object and complete the async response from the agents.
        if(futureResponse != null){
            Object response = extractObjectFromResponse(messageJSON);
            completeFutureResponse(futureResponse, response);
        }
    }

    private Object extractObjectFromResponse(String message){
//        String unknownJson = message.getContent();
        ObjectMapper objectMapper = new ObjectMapper();

        //Try and catch to guess the response type in messagetemplates until we get the right one.
        //Ignore the exception.
        try{
            return objectMapper.readValue(message, LoginResponse.class);
        } catch(IOException ignore){}

        //default to null.
        return null;
    }

    private void completeFutureResponse(CompletableFuture<?> futurePromise, Object response){
        //We'll have to do a switch statement depending on the thing we want to do.
        if(response instanceof LoginResponse){
            LoginResponse loginResponse = (LoginResponse) response;
            Optional<Staff> staff = Optional.ofNullable(loginResponse.getStaff());
            ((CompletableFuture<Optional<Staff>>) futurePromise).complete((Optional<Staff>) staff);
        }

        if(response instanceof AddStaffResponse){
            AddStaffResponse addStaffResponse = (AddStaffResponse) response;
            ((CompletableFuture<AddStaffResponse>) futurePromise).complete((AddStaffResponse) addStaffResponse);
        }
    }

    public CompletableFuture<Optional<Staff>> login(String username, String password){
        //Store the future request in the pending requests hashmap.
        String requestId = UUID.randomUUID().toString();
        CompletableFuture<Optional<Staff>> futureLoginResponse = new CompletableFuture<>();
        pendingRequests.put(requestId, futureLoginResponse);

        //Form the LoginRequest object for sending through the gateway.
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setRequestId(requestId);
        loginRequest.setOperation("login");
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);

            //Send the request to the JADE container gateway for the staff
//            sendRequestToStaffAgent(loginJSON);
        try{
            JadeGateway.execute(loginRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Return the future response once we receive it in receiveResponseFromStaffAgent()
        return futureLoginResponse;
    }

    public CompletableFuture<AddStaffResponse> addStaff(String username, Staff newStaff){
        //Store the future request in the pending requests hashmap.
        String requestId = UUID.randomUUID().toString();
        CompletableFuture<AddStaffResponse> futureAddStaffResponse = new CompletableFuture<>();
        pendingRequests.put(requestId, futureAddStaffResponse);

        AddStaffRequest addStaffRequest = new AddStaffRequest();
        addStaffRequest.setRequestId(requestId);
        addStaffRequest.setOperation("add-staff");
        addStaffRequest.setUsername(username);
        addStaffRequest.setAddStaff(newStaff);
        try{
            JadeGateway.execute(addStaffRequest);
        }catch(Exception e){
            e.printStackTrace();
        }
        return futureAddStaffResponse;

//        Optional<Staff> staffToAdd = searchStaffByUsername(newStaff.getUsername());
//        if(staffToAdd.isPresent()){
//            throw new IllegalStateException("Username already exists");
//        }
    }

}
