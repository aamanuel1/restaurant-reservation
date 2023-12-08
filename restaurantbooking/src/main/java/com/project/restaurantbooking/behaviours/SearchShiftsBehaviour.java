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

import java.util.List;
import java.util.Optional;

public class SearchShiftsBehaviour extends CyclicBehaviour {

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

            if (task.equals("search-shift")) {
                Long restaurantId = json.getJSONObject("data").getLong("shiftId");
                Optional<Shift> returnedShift = this.searchShiftById(restaurantId);
                String shiftInfoJson = null;
                if(!returnedShift.isEmpty()){
                    shiftInfoJson = jsonMapper.writeValueAsString(returnedShift.get());
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
            else{
                myAgent.putBack(msg);
                block();
                return;
            }
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

    private Optional<Shift> searchShiftById(Long shiftId) {
        ApplicationContext context = SpringContextProvider.getApplicationContext();
        ShiftRepository shiftRepository = context.getBean(ShiftRepository.class);
        return shiftRepository.findByShiftId(shiftId);
    }
}
