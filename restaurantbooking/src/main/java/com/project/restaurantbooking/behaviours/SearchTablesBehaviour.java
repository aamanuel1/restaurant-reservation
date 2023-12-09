package com.project.restaurantbooking.behaviours;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate5.jakarta.Hibernate5JakartaModule;
import com.project.restaurantbooking.SpringContextProvider;
import com.project.restaurantbooking.entity.Restaurant;
import com.project.restaurantbooking.entity.RestaurantTable;
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

public class SearchTablesBehaviour extends CyclicBehaviour {

    private ObjectMapper jsonMapper = new ObjectMapper();
    public SearchTablesBehaviour(Agent agent) {
        super(agent);
        Hibernate5JakartaModule module = new Hibernate5JakartaModule();
        module.disable(Hibernate5JakartaModule.Feature.FORCE_LAZY_LOADING);
        module.enable(Hibernate5JakartaModule.Feature.WRITE_MISSING_ENTITIES_AS_NULL);
        jsonMapper.registerModule(module);
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

            String searchTableResultsToGateway = null;

            if (task.equals("search-tables")) {
                Long restaurantId = json.getJSONObject("data").getLong("restaurantId");
                List<RestaurantTable> restaurantTableList = this.searchTablesByRestaurant(restaurantId);
                String restaurantTablesJson = null;
                if(!restaurantTableList.isEmpty()){
                    restaurantTablesJson = jsonMapper.writeValueAsString(restaurantTableList);
                    System.out.println(restaurantTablesJson);
                }

                searchTableResultsToGateway = String.format("""
                    {
                        "correlationId": "%s",
                        "task": "%s",
                        "tables": %s,
                    }                       
                    """, correlationId, task, restaurantTablesJson);
            }
            else{
                myAgent.putBack(msg);
                block();
                return;
            }
            ACLMessage searchTablesReply = msg.createReply();
            System.out.println(searchTableResultsToGateway);
            searchTablesReply.setPerformative(ACLMessage.INFORM);
            searchTablesReply.setContent(searchTableResultsToGateway);
            searchTablesReply.setConversationId(correlationId);
            searchTablesReply.setProtocol("search-table-response");
            myAgent.send(searchTablesReply);
        }
        else{
            block();
        }
    }

    private List<RestaurantTable> searchTablesByRestaurant(Long restaurantId) {
        ApplicationContext context = SpringContextProvider.getApplicationContext();
        TableRepository tableRepository = context.getBean(TableRepository.class);
        return tableRepository.findByRestaurantId(restaurantId);
    }
}
