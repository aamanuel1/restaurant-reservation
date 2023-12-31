package com.project.restaurantbooking.behaviours;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.restaurantbooking.SpringContextProvider;
import com.project.restaurantbooking.entity.Restaurant;
import com.project.restaurantbooking.entity.RestaurantTable;
import com.project.restaurantbooking.entity.Staff;
import com.project.restaurantbooking.repo.RestaurantRepository;
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

public class CreateTableBehaviour extends CyclicBehaviour {

    private ObjectMapper jsonMapper = new ObjectMapper();

    public CreateTableBehaviour(Agent agent){
        super(agent);
    }

    @SneakyThrows
    @Override
    public void action() {
        MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
        ACLMessage msg = myAgent.receive(mt);

        if(msg != null){
            String content = msg.getContent();
            JSONObject json = new JSONObject(content);
            String correlationId = json.getString("correlationId");
            String task = json.getString("task");

            String createTableReplyToGateway = null;

            if(task.equals("create-table")){
                //Get all of the elements needed to create a table.
                String adminUsername = json.getJSONObject("data").getString("adminUsername");
                Long restaurantId = json.getJSONObject("data").getLong("restaurantId");
                int tableOccupancyNum = json.getJSONObject("data").getInt("tableOccupancyNum");
                Boolean available = json.getJSONObject("data").getBoolean("available");

                Boolean isStaffAuthorized = this.isStaffAuthorized(adminUsername);
                Boolean isCreateTableSuccessful = null;
                String message = null;

                if(isStaffAuthorized){
                    try{
                        isCreateTableSuccessful = this.createTable(restaurantId, tableOccupancyNum, available);
                    }catch(Exception e){
                        e.printStackTrace();
                        isCreateTableSuccessful = false;
                    }
                }

                if(isStaffAuthorized && isCreateTableSuccessful){
                    message = "Table successfully created.";
                }
                else if(isStaffAuthorized && !isCreateTableSuccessful){
                    message = "Table creation unsuccessful";
                }
                else{
                    message = "User not authorized";
                }

                createTableReplyToGateway = String.format("""
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
            ACLMessage createTableReply = msg.createReply();
            System.out.println(createTableReplyToGateway);
            createTableReply.setPerformative(ACLMessage.INFORM);
            createTableReply.setContent(createTableReplyToGateway);
            createTableReply.setConversationId(correlationId);
            createTableReply.setProtocol("create-table-reply");
            myAgent.send(createTableReply);
        }
        else{
            block();
        }
    }

    public boolean createTable(Long restaurantId, int tableOccupancyNum, Boolean available) {
        ApplicationContext context = SpringContextProvider.getApplicationContext();
        TableRepository tableRepository = context.getBean(TableRepository.class);
        Optional<Restaurant> addRestaurant = findRestaurant(restaurantId);

        RestaurantTable newTable = new RestaurantTable();

        if(addRestaurant.isEmpty()){
            //No such restaurant
            return false;
        }
        else if(addRestaurant.isPresent()){
            newTable.setTableOccupancyNum(tableOccupancyNum);
            newTable.setRestaurant(addRestaurant.get());
            newTable.setAvailable(available);
            tableRepository.save(newTable);
            return true;
        }
        else{
            return false;
        }
    }

    public Optional<Restaurant> findRestaurant(Long restaurantId){
        ApplicationContext context = SpringContextProvider.getApplicationContext();
        RestaurantRepository restaurantRepository = context.getBean(RestaurantRepository.class);
        return restaurantRepository.findById(restaurantId);
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
