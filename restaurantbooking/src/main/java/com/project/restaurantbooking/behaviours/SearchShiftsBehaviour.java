package com.project.restaurantbooking.behaviours;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate5.jakarta.Hibernate5JakartaModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.project.restaurantbooking.SpringContextProvider;
import com.project.restaurantbooking.entity.RestaurantTable;
import com.project.restaurantbooking.entity.Shift;
import com.project.restaurantbooking.repo.ShiftRepository;
import com.project.restaurantbooking.repo.TableRepository;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import lombok.SneakyThrows;
import org.json.JSONObject;
import org.springframework.context.ApplicationContext;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class SearchShiftsBehaviour extends CyclicBehaviour {

    //Note, for the json mapper, we need to include some modules to deal with a lazy loading
    //issue with some of the linked foreign keys in restaurant and table.
    private ObjectMapper jsonMapper = new ObjectMapper();
    public SearchShiftsBehaviour(Agent agent) {
        super(agent);
        Hibernate5JakartaModule module = new Hibernate5JakartaModule();
        module.disable(Hibernate5JakartaModule.Feature.FORCE_LAZY_LOADING);
        module.enable(Hibernate5JakartaModule.Feature.WRITE_MISSING_ENTITIES_AS_NULL);
        jsonMapper.registerModule(module);
        jsonMapper.registerModule(new JavaTimeModule());
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

            String searchShiftResultsToGateway = null;

            //match the task with the helper methods
            if (task.equals("search-shift")) {
                //get the information from the JSON. Perform database operations with help
                //of helper functions with access to database (e.g searchShiftById())
                Long shiftId = json.getJSONObject("data").getLong("shiftId");
                Optional<Shift> returnedShift = this.searchShiftById(shiftId);
                String shiftInfoJson = null;
                //Perform check on the database operation.
                if(!returnedShift.isEmpty()){
                    shiftInfoJson = jsonMapper.writeValueAsString(returnedShift.get());
                    System.out.println(shiftInfoJson);
                }

                //format the response json.
                searchShiftResultsToGateway = String.format("""
                    {
                        "correlationId": "%s",
                        "task": "%s",
                        "shiftInfo": %s,
                    }                       
                    """, correlationId, task, shiftInfoJson);
            }
            else if (task.equals("search-shift-by-day")) {
                String dayString = json.getJSONObject("data").getString("day");
                Long restaurantId = json.getJSONObject("data").getLong("restaurantId");
                LocalDate day = LocalDate.parse(dayString);
                List<Shift> returnedShiftsByDay = this.searchShiftByDay(restaurantId, day);
                String shiftInfoJson = null;
                if(!returnedShiftsByDay.isEmpty()){
                    shiftInfoJson = jsonMapper.writeValueAsString(returnedShiftsByDay);
                    System.out.println(shiftInfoJson);
                }

                searchShiftResultsToGateway = String.format("""
                    {
                        "correlationId": "%s",
                        "task": "%s",
                        "shiftInfo": %s,
                    }                       
                    """, correlationId, task, shiftInfoJson);
            }
            else if (task.equals("return-all-shifts")) {
                Long restaurantId = json.getJSONObject("data").getLong("restaurantId");
                List<Shift> allShifts = this.returnAllShifts(restaurantId);
                String shiftInfoJson = null;
                if(!allShifts.isEmpty()){
                    shiftInfoJson = jsonMapper.writeValueAsString(allShifts);
                    System.out.println(shiftInfoJson);
                }

                searchShiftResultsToGateway = String.format("""
                    {
                        "correlationId": "%s",
                        "task": "%s",
                        "shiftInfo": %s,
                    }                       
                    """, correlationId, task, shiftInfoJson);
            }
            //otherwise, put the message back and let another behaviour read it.
            else{
                myAgent.putBack(msg);
                block();
                return;
            }
            //Send the message back to the Jade Gateway Agent.
            ACLMessage searchTablesReply = msg.createReply();
            System.out.println(searchShiftResultsToGateway);
            searchTablesReply.setPerformative(ACLMessage.INFORM);
            searchTablesReply.setContent(searchShiftResultsToGateway);
            searchTablesReply.setConversationId(correlationId);
            searchTablesReply.setProtocol("search-shift-response");
            myAgent.send(searchTablesReply);
        }
        else{
            block();
        }
    }

    //Helper methods, for database access.
    private Optional<Shift> searchShiftById(Long shiftId) {
        ApplicationContext context = SpringContextProvider.getApplicationContext();
        ShiftRepository shiftRepository = context.getBean(ShiftRepository.class);
        return shiftRepository.findByShiftId(shiftId);
    }

    private List<Shift> searchShiftByDay(Long restaurantId, LocalDate date) {
        ApplicationContext context = SpringContextProvider.getApplicationContext();
        ShiftRepository shiftRepository = context.getBean(ShiftRepository.class);
        return shiftRepository.findByDateAndRestaurantId(restaurantId, date);
    }

    private List<Shift> returnAllShifts(Long restaurantId) {
        ApplicationContext context = SpringContextProvider.getApplicationContext();
        ShiftRepository shiftRepository = context.getBean(ShiftRepository.class);
        return shiftRepository.findByRestaurantId(restaurantId);
    }

}
