package com.project.restaurantbooking.behaviours;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate5.jakarta.Hibernate5JakartaModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.project.restaurantbooking.SpringContextProvider;
import com.project.restaurantbooking.entity.Shift;
import com.project.restaurantbooking.entity.Staff;
import com.project.restaurantbooking.repo.ShiftRepository;
import com.project.restaurantbooking.repo.StaffRepository;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import lombok.SneakyThrows;
import org.json.JSONObject;
import org.springframework.context.ApplicationContext;

import java.util.Optional;

public class SelectShiftsBehaviour extends CyclicBehaviour {

    //Note, for the json mapper, we need to include some modules to deal with a lazy loading
    //issue with some of the linked foreign keys in restaurant and table.
    private ObjectMapper jsonMapper = new ObjectMapper();
    public SelectShiftsBehaviour(Agent agent) {
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

            String selectShiftResultsToGateway = null;

            //match the task with the helper methods
            if (task.equals("select-shift")) {
                //get the information from the JSON. Perform database operations with help
                //of helper functions with access to database
                String username = json.getJSONObject("data").getString("username");
                Long shiftId = json.getJSONObject("data").getLong("shiftId");
                Optional<Shift> returnedShift = this.searchShiftById(shiftId);
                Optional<Staff> returnedStaff = this.searchUserByUsername(username);

                //Perform check on the database operation.
                Boolean isSelectShiftSuccessful = this.selectShift(returnedStaff, returnedShift);
                String message = null;

                if(isSelectShiftSuccessful){
                    message = "Shift selected.";
                }
                else{
                    message = "Shift select unsuccessful";
                }

                //format the response json.
                selectShiftResultsToGateway = String.format("""
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
            //Send the message back to the Jade Gateway Agent.
            ACLMessage selectShiftReply = msg.createReply();
            System.out.println(selectShiftResultsToGateway);
            selectShiftReply.setPerformative(ACLMessage.INFORM);
            selectShiftReply.setContent(selectShiftResultsToGateway);
            selectShiftReply.setConversationId(correlationId);
            selectShiftReply.setProtocol("select-shift-response");
            myAgent.send(selectShiftReply);
        }
        else{
            block();
        }
    }

    private Optional<Staff> searchUserByUsername(String username) {
        ApplicationContext context = SpringContextProvider.getApplicationContext();
        StaffRepository staffRepository = context.getBean(StaffRepository.class);
        return staffRepository.findByUsername(username);
    }

    //Helper methods, for database access.
    private Optional<Shift> searchShiftById(Long shiftId) {
        ApplicationContext context = SpringContextProvider.getApplicationContext();
        ShiftRepository shiftRepository = context.getBean(ShiftRepository.class);
        return shiftRepository.findByShiftId(shiftId);
    }

    private Boolean selectShift(Optional<Staff> staff, Optional<Shift> shift){
        ApplicationContext context = SpringContextProvider.getApplicationContext();
        ShiftRepository shiftRepository = context.getBean(ShiftRepository.class);
        if(staff.isEmpty() || shift.isEmpty()){
            return false;
        }
        shift.get().setStaff(staff.get());
        shiftRepository.save(shift.get());
        return true;
    }

}
