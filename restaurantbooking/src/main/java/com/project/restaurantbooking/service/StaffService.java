package com.project.restaurantbooking.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.restaurantbooking.controller.AgentResponseHolder;
import com.project.restaurantbooking.entity.Shift;
import com.project.restaurantbooking.entity.Staff;
import com.project.restaurantbooking.messagetemplates.*;
import com.project.restaurantbooking.repo.StaffRepository;
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
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class StaffService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final ConcurrentHashMap<String, CompletableFuture<?>> pendingRequests = new ConcurrentHashMap<>();

    private final Map<String, AgentResponseHolder> responseMap = new ConcurrentHashMap<>();

    @Autowired
    @Qualifier("StaffAgentRequestChannel")
    private DirectChannel staffAgentRequestChannel;

    @Autowired
    @Qualifier("StaffAgentResponseChannel")
    private DirectChannel staffAgentResponseChannel;

    @ServiceActivator(inputChannel = "staffAgentRequestChannel")
    public void sendRequestToStaffAgent(String message){
        //Convert to ACLMessage and send to JADE Gateway
        ACLMessage agentMessage = new ACLMessage(ACLMessage.REQUEST);
        agentMessage.addReceiver(new AID("GatewayAgent", AID.ISLOCALNAME));
        agentMessage.setContent(message);
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
        ObjectMapper objectMapper = new ObjectMapper();

        //Try and catch to guess the response type in messagetemplates until we get the right one.
        //Ignore the exception.
        try{
            return objectMapper.readValue(message, LoginResponse.class);
        } catch(IOException ignore){}
        try{
            return objectMapper.readValue(message, AddStaffResponse.class);
        } catch(IOException ignore){};
        try{
            return objectMapper.readValue(message, DeleteStaffResponse.class);
        } catch(IOException ignore){};
        try{
            return objectMapper.readValue(message, ChangeStaffResponse.class);
        } catch(IOException ignore){};

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

        if(response instanceof DeleteStaffResponse){
            DeleteStaffResponse deleteStaffResponse = (DeleteStaffResponse) response;
            ((CompletableFuture<DeleteStaffResponse>) futurePromise).complete((DeleteStaffResponse) deleteStaffResponse);
        }

        if(response instanceof ChangeStaffResponse){
            ChangeStaffResponse changeStaffResponse = (ChangeStaffResponse) response;
            ((CompletableFuture<ChangeStaffResponse>) futurePromise).complete((ChangeStaffResponse) changeStaffResponse);
        }
    }

    public CompletableFuture<Optional<Staff>> login(String username, String password){
        //Store the future request in the pending requests hashmap.
        CompletableFuture<Optional<Staff>> futureLoginResponse = new CompletableFuture<>();
        String requestId = this.storeRequest(futureLoginResponse);

        //Form the LoginRequest object for sending through the gateway.
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setRequestId(requestId);
        loginRequest.setOperation("login");
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);

        //Send through the JadeGateway API
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
        CompletableFuture<AddStaffResponse> futureAddStaffResponse = new CompletableFuture<>();
        String requestId = this.storeRequest(futureAddStaffResponse);

        //Form the request object for add staff. then send through the gateway.
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
        //Return the future.
        return futureAddStaffResponse;

    }

    public CompletableFuture<DeleteStaffResponse> deleteStaffById(String username, Long deleteId){
        //Similar to other methods.
        CompletableFuture<DeleteStaffResponse> futureDeleteStaffResponse = new CompletableFuture<>();
        String requestId = storeRequest(futureDeleteStaffResponse);

        DeleteStaffRequest deleteStaffRequest = new DeleteStaffRequest();
        deleteStaffRequest.setRequestId(requestId);
        deleteStaffRequest.setOperation("delete-staff");
        deleteStaffRequest.setUsername(username);
        deleteStaffRequest.setDeleteByStaffId(deleteId);
        deleteStaffRequest.setDeleteByStaffUsername("");
        try{
            JadeGateway.execute(deleteStaffRequest);
        }catch(Exception e){
            e.printStackTrace();
        }

        return futureDeleteStaffResponse;
    }


    public CompletableFuture<DeleteStaffResponse> deleteStaffByUsername(String username, String deleteUsername){
        CompletableFuture<DeleteStaffResponse> futureDeleteStaffResponse = new CompletableFuture<>();
        String requestId = storeRequest(futureDeleteStaffResponse);

        DeleteStaffRequest deleteStaffRequest = new DeleteStaffRequest();
        deleteStaffRequest.setRequestId(requestId);
        deleteStaffRequest.setOperation("delete-staff");
        deleteStaffRequest.setUsername(username);
        deleteStaffRequest.setDeleteByStaffId(Long.valueOf(-1));
        deleteStaffRequest.setDeleteByStaffUsername(deleteUsername);
        try{
            JadeGateway.execute(deleteStaffRequest);
        }catch(Exception e){
            e.printStackTrace();
        }

        return futureDeleteStaffResponse;
    }

    public CompletableFuture<ChangeStaffResponse> changeStaff(String adminUsername, Long staffId, Staff changeStaffAttributes){
        CompletableFuture<ChangeStaffResponse> futureChangeStaffResponse = new CompletableFuture<>();
        String requestId = storeRequest(futureChangeStaffResponse);

        ChangeStaffRequest changeStaffRequest = new ChangeStaffRequest();
        changeStaffRequest.setRequestId(requestId);
        changeStaffRequest.setOperation("change-staff");
        changeStaffRequest.setUsername(adminUsername);
        changeStaffRequest.setChangeStaffId(staffId);
        changeStaffRequest.setChangeStaff(changeStaffAttributes);
        try{
            JadeGateway.execute(changeStaffRequest);
        }catch(Exception e){
            e.printStackTrace();
        }

        return futureChangeStaffResponse;

    }

    public CompletableFuture<Object> searchStaff(String adminUsername, String findUsername){
        String correlationId = UUID.randomUUID().toString();
        AgentResponseHolder responseHolder = new AgentResponseHolder();
        responseMap.put(correlationId, responseHolder);

        String searchStaffMsgJson = String.format("""
        {
            "correlationId": "%s",
            "targetAgent": "staffAgent",
            "task": "search-staff",
            "data": {
                "username": "%s",
                "searchUsername": "%s",
            }
        }
        """, correlationId, adminUsername, findUsername);
        AgentCommand searchStaffCommand = new AgentCommand("staffAgent", searchStaffMsgJson, correlationId, "search-staff");
        try{
            JadeGateway.execute(searchStaffCommand);
        }catch(Exception e){
            e.printStackTrace();
        }

        CompletableFuture<Object> result = searchStaffCommand.getFutureResult();
        return result;
    }

    public CompletableFuture<Object> returnAllStaff(String adminUsername, Long restaurantId){
        String correlationId = UUID.randomUUID().toString();
        AgentResponseHolder responseHolder = new AgentResponseHolder();
        responseMap.put(correlationId, responseHolder);

        String returnStaffMsgJson = String.format("""
        {
            "correlationId": "%s",
            "targetAgent": "staffAgent",
            "task": "return-all-staff",
            "data": {
                "username": "%s",
                "restaurantId": "%d",
            }
        }
        """, correlationId, adminUsername, restaurantId);
        AgentCommand returnStaffCommand = new AgentCommand("staffAgent", returnStaffMsgJson, correlationId, "return-all-staff");
        try{
            JadeGateway.execute(returnStaffCommand);
        }catch(Exception e){
            e.printStackTrace();
        }

        CompletableFuture<Object> result = returnStaffCommand.getFutureResult();
        return result;
    }


    public void createEmptyTable(Long restaurantId, int tableOccupancyNum, Boolean available){

    }

    public void createTable(Long restaurantId, int tableOccupancyNum, Boolean available, List<Shift> timeslots){

    }

    public void deleteTable(){

    }

    public void changeTable(){

    }

    private String storeRequest(CompletableFuture<?> request){
        String requestId = UUID.randomUUID().toString();
        pendingRequests.put(requestId, request);
        return requestId;
    }

}
