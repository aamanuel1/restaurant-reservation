package com.project.restaurantbooking.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.restaurantbooking.controller.AgentResponseHolder;
import com.project.restaurantbooking.entity.RestaurantTable;
import com.project.restaurantbooking.entity.Staff;
import com.project.restaurantbooking.messagetemplates.*;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class StaffService {

    //JsonMapper
    private final ObjectMapper objectMapper = new ObjectMapper();

    //Hashmap for first few methods below, for keeping track of async requests.
    private final ConcurrentHashMap<String, CompletableFuture<?>> pendingRequests = new ConcurrentHashMap<>();

    //
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

    //Note, the next two methods are based on a Data Transfer Object (DTO) method using Jackson, later on,
    //we change the process. The object is sent to TheGatewayAgent which sends it to an instanceof check for the object type.
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

    public CompletableFuture<AddStaffResponse> addStaff(String username, Long restaurantId, Staff newStaff){
        //Store the future request in the pending requests hashmap.
        CompletableFuture<AddStaffResponse> futureAddStaffResponse = new CompletableFuture<>();
        String requestId = this.storeRequest(futureAddStaffResponse);

        //Form the request object for add staff. then send through the gateway.
        AddStaffRequest addStaffRequest = new AddStaffRequest();
        addStaffRequest.setRequestId(requestId);
        addStaffRequest.setOperation("add-staff");
        addStaffRequest.setUsername(username);
        addStaffRequest.setAddStaff(newStaff);
        addStaffRequest.setRestaurantId(restaurantId);

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

    //From here, we format a JSON to send to TheGatewayAgent to route to the right agent using a generalized
    //AgentCommand object, instead of the previous method.
    public CompletableFuture<Object> searchStaff(String adminUsername, String findUsername){
        //Put request into an async response map.
        String correlationId = UUID.randomUUID().toString();
        AgentResponseHolder responseHolder = new AgentResponseHolder();
        responseMap.put(correlationId, responseHolder);

        //Format the string into a json and feed the information from the function call.
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

        //Send to the gateway agent through the JadeGateway API
        AgentCommand searchStaffCommand = new AgentCommand("staffAgent", searchStaffMsgJson, correlationId, "search-staff");
        try{
            JadeGateway.execute(searchStaffCommand);
        }catch(Exception e){
            e.printStackTrace();
        }

        //Wait for the future response, then send it to the controller.
        CompletableFuture<Object> result = searchStaffCommand.getFutureResult();
        return result;
    }

    public CompletableFuture<Object> returnAllStaff(String adminUsername, Long restaurantId){
        //Similar process as above.
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

    public CompletableFuture<Object> createTable(String adminUsername, Long restaurantId, int tableOccupancyNum, Boolean available){
        String correlationId = UUID.randomUUID().toString();
        AgentResponseHolder responseHolder = new AgentResponseHolder();
        responseMap.put(correlationId, responseHolder);
        String createTableMsgJson = String.format("""
        {
            "correlationId": "%s",
            "targetAgent": "staffAgent",
            "task": "create-table",
            "data": {
                "adminUsername": "%s",
                "restaurantId": %d,
                "tableOccupancyNum": %d,
                "available": %s,
            }
        }
        """, correlationId, adminUsername, restaurantId, tableOccupancyNum, available);
        System.out.println(createTableMsgJson);
        AgentCommand createTableCommand = new AgentCommand("staffAgent", createTableMsgJson, correlationId, "create-table");
        try{
            JadeGateway.execute(createTableCommand);
        }catch(Exception e){
            e.printStackTrace();
        }

        CompletableFuture<Object> result = createTableCommand.getFutureResult();
        return result;
    }

    public CompletableFuture<Object> searchTables(Long restaurantId){
        String correlationId = UUID.randomUUID().toString();
        AgentResponseHolder responseHolder = new AgentResponseHolder();
        responseMap.put(correlationId, responseHolder);

        String searchTablesMsgJson = String.format("""
        {
            "correlationId": "%s",
            "targetAgent": "staffAgent",
            "task": "search-tables",
            "data": {
                "restaurantId": %d,
            }
        }
        """, correlationId, restaurantId);
        System.out.println(searchTablesMsgJson);
        AgentCommand returnTablesCommand = new AgentCommand("staffAgent", searchTablesMsgJson, correlationId, "search-tables");
        try{
            JadeGateway.execute(returnTablesCommand);
        }catch(Exception e){
            e.printStackTrace();
        }

        CompletableFuture<Object> result = returnTablesCommand.getFutureResult();
        return result;
    }

    public CompletableFuture<Object> changeTableAttributes(String adminUsername, Long tableId, RestaurantTable tableAttributeChanges){
        String correlationId = UUID.randomUUID().toString();
        AgentResponseHolder responseHolder = new AgentResponseHolder();
        responseMap.put(correlationId, responseHolder);

        String tableAttributeChangesJson = null;
        ObjectMapper jsonMapper = new ObjectMapper();
        try{
            tableAttributeChangesJson = jsonMapper.writeValueAsString(tableAttributeChanges);
        } catch(Exception e){
            e.printStackTrace();
        }

        String ChangeTablesMsgJson = String.format("""
        {
            "correlationId": "%s",
            "targetAgent": "staffAgent",
            "task": "change-table-attributes",
            "data": {
                "adminUsername": "%s",
                "tableId": %d,
                "tableAttributeChanges": %s,
            }
        }
        """, correlationId, adminUsername, tableId, tableAttributeChangesJson);
        System.out.println(ChangeTablesMsgJson);
        AgentCommand changeTablesCommand = new AgentCommand("staffAgent", ChangeTablesMsgJson, correlationId, "change-table-attributes");
        try{
            JadeGateway.execute(changeTablesCommand);
        }catch(Exception e){
            e.printStackTrace();
        }

        CompletableFuture<Object> result = changeTablesCommand.getFutureResult();
        return result;
    }

    public CompletableFuture<Object> deleteTable(String adminUsername, Long tableId){
        String correlationId = UUID.randomUUID().toString();
        AgentResponseHolder responseHolder = new AgentResponseHolder();
        responseMap.put(correlationId, responseHolder);

        String returnStaffMsgJson = String.format("""
        {
            "correlationId": "%s",
            "targetAgent": "staffAgent",
            "task": "delete-table",
            "data": {
                "username": "%s",
                "tableId": "%d",
            }
        }
        """, correlationId, adminUsername, tableId);
        AgentCommand returnStaffCommand = new AgentCommand("staffAgent", returnStaffMsgJson, correlationId, "return-all-staff");
        try{
            JadeGateway.execute(returnStaffCommand);
        }catch(Exception e){
            e.printStackTrace();
        }

        CompletableFuture<Object> result = returnStaffCommand.getFutureResult();
        return result;
    }

    public CompletableFuture<Object> createShift(String adminUsername, Long tableId, LocalDate date, LocalDateTime startTime, LocalDateTime endTime){
        String correlationId = UUID.randomUUID().toString();
        AgentResponseHolder responseHolder = new AgentResponseHolder();
        responseMap.put(correlationId, responseHolder);

        String createShiftMsgJson = String.format("""
        {
            "correlationId": "%s",
            "targetAgent": "staffAgent",
            "task": "create-shift",
            "data": {
                "adminUsername": "%s",
                "tableId": %d,
                "date": "%s",
                "startTime":"%s",
                "endTime": "%s",
            }
        }
        """, correlationId, adminUsername, tableId, date.toString(), startTime.toString(), endTime.toString());

        System.out.println(createShiftMsgJson);
        AgentCommand createStaffCommand = new AgentCommand("staffAgent", createShiftMsgJson, correlationId, "create-shift");
        try{
            JadeGateway.execute(createStaffCommand);
        }catch(Exception e){
            e.printStackTrace();
        }

        CompletableFuture<Object> result = createStaffCommand.getFutureResult();
        return result;
    }

    public CompletableFuture<Object> deleteShift(String adminUsername, Long shiftId){
        String correlationId = UUID.randomUUID().toString();
        AgentResponseHolder responseHolder = new AgentResponseHolder();
        responseMap.put(correlationId, responseHolder);

        String deleteShiftMsgJson = String.format("""
        {
            "correlationId": "%s",
            "targetAgent": "staffAgent",
            "task": "delete-shift",
            "data": {
                "adminUsername": "%s",
                "shiftId": %d,
            }
        }
        """, correlationId, adminUsername, shiftId);

        System.out.println(deleteShiftMsgJson);
        AgentCommand deleteShiftCommand = new AgentCommand("staffAgent", deleteShiftMsgJson, correlationId, "delete-shift");
        try{
            JadeGateway.execute(deleteShiftCommand);
        }catch(Exception e){
            e.printStackTrace();
        }

        CompletableFuture<Object> result = deleteShiftCommand.getFutureResult();
        return result;

    }

    public CompletableFuture<Object> searchShift(String username, Long shiftId){
        String correlationId = UUID.randomUUID().toString();
        AgentResponseHolder responseHolder = new AgentResponseHolder();
        responseMap.put(correlationId, responseHolder);

        String searchShiftMsgJson = String.format("""
        {
            "correlationId": "%s",
            "targetAgent": "staffAgent",
            "task": "search-shift",
            "data": {
                "username": "%s",
                "shiftId": %d,
            }
        }
        """, correlationId, username, shiftId);

        System.out.println(searchShiftMsgJson);
        AgentCommand searchShiftCommand = new AgentCommand("staffAgent", searchShiftMsgJson, correlationId, "search-shift");
        try{
            JadeGateway.execute(searchShiftCommand);
        }catch(Exception e){
            e.printStackTrace();
        }

        CompletableFuture<Object> result = searchShiftCommand.getFutureResult();
        return result;

    }

    public CompletableFuture<Object> searchShiftByDay(String username, Long restaurantId, LocalDate day){
        String correlationId = UUID.randomUUID().toString();
        AgentResponseHolder responseHolder = new AgentResponseHolder();
        responseMap.put(correlationId, responseHolder);

        String searchShiftMsgJson = String.format("""
        {
            "correlationId": "%s",
            "targetAgent": "staffAgent",
            "task": "search-shift-by-day",
            "data": {
                "username": "%s",
                "restaurantId": %d,
                "day": %s,
            }
        }
        """, correlationId, username, restaurantId, day.toString());

        System.out.println(searchShiftMsgJson);
        AgentCommand searchShiftCommand = new AgentCommand("staffAgent", searchShiftMsgJson, correlationId, "search-shift-by-day");
        try{
            JadeGateway.execute(searchShiftCommand);
        }catch(Exception e){
            e.printStackTrace();
        }

        CompletableFuture<Object> result = searchShiftCommand.getFutureResult();
        return result;

    }

    public CompletableFuture<Object> returnAllShifts(String username, Long restaurantId){
        String correlationId = UUID.randomUUID().toString();
        AgentResponseHolder responseHolder = new AgentResponseHolder();
        responseMap.put(correlationId, responseHolder);

        String searchShiftMsgJson = String.format("""
        {
            "correlationId": "%s",
            "targetAgent": "staffAgent",
            "task": "return-all-shifts",
            "data": {
                "username": "%s",
                "restaurantId": %d,
            }
        }
        """, correlationId, username, restaurantId);

        System.out.println(searchShiftMsgJson);
        AgentCommand returnAllShiftsCommand = new AgentCommand("staffAgent", searchShiftMsgJson, correlationId, "return-all-shifts");
        try{
            JadeGateway.execute(returnAllShiftsCommand);
        }catch(Exception e){
            e.printStackTrace();
        }

        CompletableFuture<Object> result = returnAllShiftsCommand.getFutureResult();
        return result;
    }

    private String storeRequest(CompletableFuture<?> request){
        //private helper method for some of the Jackson DTO methods above.
        String requestId = UUID.randomUUID().toString();
        pendingRequests.put(requestId, request);
        return requestId;
    }

}
