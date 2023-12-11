package com.project.restaurantbooking.behaviours;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate5.jakarta.Hibernate5JakartaModule;
import com.project.restaurantbooking.SpringContextProvider;
import com.project.restaurantbooking.entity.Staff;
import com.project.restaurantbooking.repo.StaffRepository;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import lombok.SneakyThrows;
import org.json.JSONObject;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.Message;

import java.util.List;
import java.util.Optional;

public class SearchStaffBehaviour extends CyclicBehaviour {

    private ObjectMapper jsonMapper = new ObjectMapper();

    public SearchStaffBehaviour(Agent agent){
        super(agent);
    }
    @SneakyThrows
    @Override
    public void action() {
        MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
        Hibernate5JakartaModule module = new Hibernate5JakartaModule();
        module.disable(Hibernate5JakartaModule.Feature.FORCE_LAZY_LOADING);
        module.enable(Hibernate5JakartaModule.Feature.WRITE_MISSING_ENTITIES_AS_NULL);
        jsonMapper.registerModule(module);
        ACLMessage msg = myAgent.receive(mt);

        if(msg != null){
            String content = msg.getContent();
            JSONObject json = new JSONObject(content);
            String correlationId = json.getString("correlationId");
            String task = json.getString("task");

            String searchStaffResultsToGateway = null;

            if(task.equals("search-staff")){
                String username = json.getJSONObject("data").getString("username");
                String searchUsername = json.getJSONObject("data").getString("searchUsername");
                Optional<Staff> foundStaff = searchStaffByUsername(searchUsername);
                String staffJson = null;
                if(foundStaff.isPresent()){
                    staffJson = jsonMapper.writeValueAsString(foundStaff.get());
                }

                searchStaffResultsToGateway = String.format("""
                    {
                        "correlationId": "%s",
                        "task": "%s",
                        "staff": %s,
                    }
                    """, correlationId, task, staffJson);
            }
            else if(task.equals("return-all-staff")){
                String username = json.getJSONObject("data").getString("username");
                Long restaurantId = json.getJSONObject("data").getLong("restaurantId");
                List<Staff> allStaff = this.findAllStaff();
                String staffListJson = null;
                if(!allStaff.isEmpty()){
                    staffListJson = jsonMapper.writeValueAsString(allStaff);
                }

                searchStaffResultsToGateway = String.format("""
                    {
                        "correlationId": "%s",
                        "task": "%s",
                        "staff": %s,
                    }
                    """, correlationId, task, staffListJson);
            }
            else{
                myAgent.putBack(msg);
                block();
                return;
            }

            ACLMessage searchStaffReply = msg.createReply();
            searchStaffReply.setPerformative(ACLMessage.INFORM);
            searchStaffReply.setContent(searchStaffResultsToGateway);
            searchStaffReply.setConversationId(correlationId);
            searchStaffReply.setProtocol("search-staff-response");
            myAgent.send(searchStaffReply);

        }
        else{
            block();
        }
    }

    public Optional<Staff> searchStaffByUsername(String username){
        ApplicationContext context = SpringContextProvider.getApplicationContext();
        StaffRepository staffRepository = context.getBean(StaffRepository.class);
        return staffRepository.findByUsername(username);
    }

    public List<Staff> findAllStaff(){
        ApplicationContext context = SpringContextProvider.getApplicationContext();
        StaffRepository staffRepository = context.getBean(StaffRepository.class);
        return staffRepository.findAll();
    }


    public boolean isStaffAuthorized(String username){
        ApplicationContext context = SpringContextProvider.getApplicationContext();
        StaffRepository staffRepository = context.getBean(StaffRepository.class);
        Optional<Staff> staff = staffRepository.findByUsername(username);
        if(staff.isEmpty()){
            return false;
        }
        if(!staff.get().getIsAdmin()){
            return false;
        }
        return true;
    }
}
