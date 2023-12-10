package com.project.restaurantbooking.behaviours;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.restaurantbooking.SpringContextProvider;
import com.project.restaurantbooking.entity.RestaurantTable;
import com.project.restaurantbooking.entity.Staff;
import com.project.restaurantbooking.repo.StaffRepository;
import com.project.restaurantbooking.repo.TableRepository;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import lombok.SneakyThrows;
import org.json.JSONObject;
import org.springframework.context.ApplicationContext;

import java.util.Optional;

public class DeleteTableBehaviour extends CyclicBehaviour {

    private ObjectMapper jsonMapper = new ObjectMapper();

    public DeleteTableBehaviour(Agent agent){
        super(agent);
    }

    @SneakyThrows
    @Override
    public void action() {
        MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
        ACLMessage msg = myAgent.receive(mt);

        if(msg != null){
            String content =msg.getContent();
            JSONObject json = new JSONObject(content);
            String correlationId = json.getString("correlationId");
            String task = json.getString("task");

            String deleteTableResultsToGateway = null;

            if(task.equals("delete-table")){
                String adminUsername = json.getJSONObject("data").getString("username");
                Long tableId = json.getJSONObject("data").getLong("tableId");
                Boolean isStaffAuthorized = this.isStaffAuthorized(adminUsername);
                Boolean isDeleteTableSuccessful = null;
                String message = null;
                if(isStaffAuthorized){
                    isDeleteTableSuccessful = this.deleteTable(tableId);
                }

                if(isStaffAuthorized && isDeleteTableSuccessful){
                    message = "Delete table successful.";
                }
                else if(isStaffAuthorized && !isDeleteTableSuccessful){
                    message = "Delete table unsuccessful";
                }
                else{
                    message = "User not authorized";
                }

                deleteTableResultsToGateway = String.format("""
                    {
                        "correlationId": "%s",
                        "task": "%s",
                        "message": %s,
                    }
                    """, correlationId, task, message);
            }
            else{
                myAgent.putBack(msg);
                block();
                return;
            }
            ACLMessage deleteTableReply = msg.createReply();
            deleteTableReply.setPerformative(ACLMessage.INFORM);
            deleteTableReply.setContent(deleteTableResultsToGateway);
            deleteTableReply.setConversationId(correlationId);
            deleteTableReply.setProtocol("search-staff-response");
            myAgent.send(deleteTableReply);
        }
    }

    public Boolean deleteTable(Long tableId) {
        ApplicationContext context = SpringContextProvider.getApplicationContext();
        TableRepository tableRepository = context.getBean(TableRepository.class);

        Optional<RestaurantTable> tableToDelete = tableRepository.findByTableId(tableId);

        if(tableToDelete.isPresent()){
            try{
                tableRepository.delete(tableToDelete.get());
            }catch(Exception e){
                e.printStackTrace();
                return false;
            }

            return true;
        }
        else{
            return false;
        }
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
