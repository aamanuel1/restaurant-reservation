package com.project.restaurantbooking.behaviours;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.restaurantbooking.SpringContextProvider;
import com.project.restaurantbooking.entity.RestaurantTable;
import com.project.restaurantbooking.entity.Shift;
import com.project.restaurantbooking.entity.Staff;
import com.project.restaurantbooking.repo.ShiftRepository;
import com.project.restaurantbooking.repo.StaffRepository;
import com.project.restaurantbooking.repo.TableRepository;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import lombok.SneakyThrows;
import org.json.JSONObject;
import org.springframework.context.ApplicationContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public class CreateShiftBehaviour extends CyclicBehaviour {

    private ObjectMapper jsonMapper = new ObjectMapper();

    public CreateShiftBehaviour(Agent agentMan) {
        super(agentMan);
    }

    @SneakyThrows
    @Override
    public void action() {
        MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
        ACLMessage msg = myAgent.receive(mt);

        if (msg != null) {
            String content = msg.getContent();
            JSONObject json = new JSONObject(content);
            String correlationId = json.getString("correlationId");
            String task = json.getString("task");

            String createShiftReplyToGateway = null;

            if (task.equals("create-shift")) {
                String adminUsername = json.getJSONObject("data").getString("adminUsername");
                Long tableId = json.getJSONObject("data").getLong("tableId");
                String dateString = json.getJSONObject("data").getString("date");
                String startTimeString = json.getJSONObject("data").getString("startTime");
                String endTimeString = json.getJSONObject("data").getString("endTime");

                //Change the strings to be LocalDateTimes
                LocalDate date = LocalDate.parse(dateString);
                LocalDateTime startTime = LocalDateTime.parse(startTimeString);
                LocalDateTime endTime = LocalDateTime.parse(endTimeString);

                Boolean isStaffAuthorized = this.isStaffAuthorized(adminUsername);
                Boolean isCreateShiftSuccessful = null;
                String message = null;

                if (isStaffAuthorized) {
                    try {
                        isCreateShiftSuccessful = this.createShift(tableId, date, startTime, endTime);
                    } catch (Exception e) {
                        e.printStackTrace();
                        isCreateShiftSuccessful = false;
                    }
                }

                if (isStaffAuthorized && isCreateShiftSuccessful) {
                    message = "Table successfully created.";
                } else if (isStaffAuthorized && !isCreateShiftSuccessful) {
                    message = "Table creation unsuccessful";
                } else {
                    message = "User not authorized";
                }

                createShiftReplyToGateway = String.format("""
                        {
                            "correlationId": "%s",
                            "task": "%s",
                            "message": %s,
                        }                       
                        """, correlationId, task, message);
            } else {
                myAgent.putBack(msg);
                block();
                return;
            }
            ACLMessage createTableReply = msg.createReply();
            System.out.println(createShiftReplyToGateway);
            createTableReply.setPerformative(ACLMessage.INFORM);
            createTableReply.setContent(createShiftReplyToGateway);
            createTableReply.setConversationId(correlationId);
            createTableReply.setProtocol("create-table-reply");
            myAgent.send(createTableReply);
        }
        else{
            block();
        }
    }

    public Boolean createShift(Long tableId, LocalDate date, LocalDateTime startTime, LocalDateTime endTime){
        ApplicationContext context = SpringContextProvider.getApplicationContext();
        TableRepository tableRepository = context.getBean(TableRepository.class);
        ShiftRepository shiftRepository = context.getBean(ShiftRepository.class);

        Optional<RestaurantTable> tableToAddShift = tableRepository.findByTableId(tableId);
        if(tableToAddShift.isEmpty()){
            return false;
        }

        try{
            Shift newShift = new Shift();
            newShift.setTable(tableToAddShift.get());
            newShift.setDate(date);
            newShift.setStartTime(startTime);
            newShift.setEndTime(endTime);
            shiftRepository.save(newShift);
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean isStaffAuthorized(String username) {
        ApplicationContext context = SpringContextProvider.getApplicationContext();
        StaffRepository staffRepository = context.getBean(StaffRepository.class);
        Optional<Staff> staff = staffRepository.findByUsername(username);
        if (staff.isEmpty()) {
            return false;
        }
        if (!staff.get().getIsAdmin()) {
            return false;
        }
        return true;
    }
}
