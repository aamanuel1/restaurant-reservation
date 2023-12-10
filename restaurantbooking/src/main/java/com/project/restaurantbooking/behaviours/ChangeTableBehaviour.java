package com.project.restaurantbooking.behaviours;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.restaurantbooking.SpringContextProvider;
import com.project.restaurantbooking.entity.Restaurant;
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

import java.util.List;
import java.util.Optional;

public class ChangeTableBehaviour extends CyclicBehaviour {

    private ObjectMapper jsonMapper = new ObjectMapper();

    public ChangeTableBehaviour(Agent agent){
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

            String changeTableReplyToGateway = null;

            if (task.equals("change-table-attributes")) {
                String adminUsername = json.getJSONObject("data").getString("adminUsername");
                Long tableId = json.getJSONObject("data").getLong("tableId");
                JSONObject newTableAttributes = json.getJSONObject("data").getJSONObject("tableAttributeChanges");
                Optional<RestaurantTable> tableToChange = this.searchTablesByTableId(tableId);

                Boolean isStaffAuthorized = this.isStaffAuthorized(adminUsername);
                Boolean isChangeTableSuccessful = null;
                String message = null;

                JSONObject tableChanges = json.getJSONObject("data").getJSONObject("tableAttributeChanges");
                String tableToChangeJson = null;
                if(!tableToChange.isEmpty()){
                    tableToChangeJson = jsonMapper.writeValueAsString(tableToChange);
                    System.out.println(tableToChangeJson);
                }

                if(isStaffAuthorized){
                    isChangeTableSuccessful = this.changeTable(tableId, newTableAttributes);
                }

                if(isStaffAuthorized && isChangeTableSuccessful){
                    message = "Table changes successful.";
                }
                else if(isStaffAuthorized && !isChangeTableSuccessful){
                    message = "Table changes unsuccessful";
                }
                else{
                    message = "User not authorized";
                }

                changeTableReplyToGateway = String.format("""
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
            ACLMessage searchTablesReply = msg.createReply();
            System.out.println(changeTableReplyToGateway);
            searchTablesReply.setPerformative(ACLMessage.INFORM);
            searchTablesReply.setContent(changeTableReplyToGateway);
            searchTablesReply.setConversationId(correlationId);
            searchTablesReply.setProtocol("change-table-attributes-reply");
            myAgent.send(searchTablesReply);
        }
        else{
            block();
        }
    }

    private Optional<RestaurantTable> searchTablesByTableId(Long tableId) {
        ApplicationContext context = SpringContextProvider.getApplicationContext();
        TableRepository tableRepository = context.getBean(TableRepository.class);
        return tableRepository.findByTableId(tableId);
    }

    public Boolean changeTable(Long tableId, JSONObject newTableAttributes) {
        ApplicationContext context = SpringContextProvider.getApplicationContext();
        TableRepository tableRepository = context.getBean(TableRepository.class);

        Optional<RestaurantTable> tableToChange = tableRepository.findByTableId(tableId);
        RestaurantTable newTableAttributesObj = null;
        try{
            newTableAttributesObj = jsonMapper.readValue(newTableAttributes.toString(), RestaurantTable.class);
        } catch(Exception e){
            e.printStackTrace();
            return false;
        }

        if(tableToChange.isEmpty()){
            return false;
        }

        if(newTableAttributesObj.getAvailable() != tableToChange.get().getAvailable() && newTableAttributesObj.getAvailable() != null){
            tableToChange.get().setAvailable(newTableAttributesObj.getAvailable());
        }
        if(newTableAttributesObj.getTableOccupancyNum() != 0){
            tableToChange.get().setTableOccupancyNum(newTableAttributesObj.getTableOccupancyNum());
        }
        tableRepository.save(tableToChange.get());

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
