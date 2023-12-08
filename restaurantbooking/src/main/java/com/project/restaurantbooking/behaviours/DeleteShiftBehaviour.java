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

public class DeleteShiftBehaviour extends CyclicBehaviour {

    private ObjectMapper jsonMapper = new ObjectMapper();

    public DeleteShiftBehaviour(Agent agent) {
        super(agent);
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

            String deleteShiftReplyToGateway = null;

            if (task.equals("delete-shift")) {
                String adminUsername = json.getJSONObject("data").getString("adminUsername");
                Long shiftId = json.getJSONObject("data").getLong("shiftId");

                Boolean isStaffAuthorized = this.isStaffAuthorized(adminUsername);
                Boolean isDeleteShiftSuccessful = null;
                String message = null;

                if (isStaffAuthorized) {
                    try {
                        isDeleteShiftSuccessful = this.deleteShift(shiftId);
                    } catch (Exception e) {
                        e.printStackTrace();
                        isDeleteShiftSuccessful = false;
                    }
                }

                if (isStaffAuthorized && isDeleteShiftSuccessful) {
                    message = "Shift successfully deleted.";
                } else if (isStaffAuthorized && !isDeleteShiftSuccessful) {
                    message = "Shift deletion unsuccessful";
                } else {
                    message = "User not authorized";
                }

                deleteShiftReplyToGateway = String.format("""
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
            System.out.println(deleteShiftReplyToGateway);
            createTableReply.setPerformative(ACLMessage.INFORM);
            createTableReply.setContent(deleteShiftReplyToGateway);
            createTableReply.setConversationId(correlationId);
            createTableReply.setProtocol("delete-shift-reply");
            myAgent.send(createTableReply);
        }
        else{
            block();
        }
    }

    public Boolean deleteShift(Long shiftId){
        ApplicationContext context = SpringContextProvider.getApplicationContext();
        ShiftRepository shiftRepository = context.getBean(ShiftRepository.class);

        Optional<Shift> shiftToDelete = shiftRepository.findById(shiftId);
        if(shiftToDelete.isEmpty()){
            return false;
        }

        try{
            shiftRepository.delete(shiftToDelete.get());
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
