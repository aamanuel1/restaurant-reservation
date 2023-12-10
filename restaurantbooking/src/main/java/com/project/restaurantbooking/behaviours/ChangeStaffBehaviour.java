package com.project.restaurantbooking.behaviours;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.restaurantbooking.ApplicationContextProvider;
import com.project.restaurantbooking.SpringContextProvider;
import com.project.restaurantbooking.entity.Staff;
import com.project.restaurantbooking.messagetemplates.ChangeStaffRequest;
import com.project.restaurantbooking.messagetemplates.ChangeStaffResponse;
import com.project.restaurantbooking.repo.StaffRepository;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.util.Optional;

public class ChangeStaffBehaviour extends CyclicBehaviour {

    private ObjectMapper jsonMapper = new ObjectMapper();

    private DirectChannel staffAgentResponseChannel;

    private MessageTemplate messageTemplate;

    public ChangeStaffBehaviour(Agent agent){
        super(agent);
        this.messageTemplate = MessageTemplate.MatchProtocol("change-staff");
    }
    @Override
    public void action() {
        ApplicationContext context = ApplicationContextProvider.getApplicationContext();
        staffAgentResponseChannel = context.getBean("StaffAgentReplyChannel", DirectChannel.class);
        ACLMessage changeStaffMsg = myAgent.receive(messageTemplate);
        if (changeStaffMsg != null){
            ChangeStaffRequest changeStaffRequest = null;

            try{
                changeStaffRequest = jsonMapper.readValue(changeStaffMsg.getContent(), ChangeStaffRequest.class);
            } catch (Exception e){
                e.printStackTrace();
                block();
            }

            boolean isStaffAuthorized = this.isStaffAuthorized(changeStaffRequest.getUsername());
            boolean isChangeSuccessful = false;
            if(isStaffAuthorized) {
                try {
                    isChangeSuccessful = this.changeStaff(changeStaffRequest);
                } catch (Exception e) {
                    e.printStackTrace();
                    isChangeSuccessful = false;
                }
            }
            ChangeStaffResponse changeStaffResponse = new ChangeStaffResponse();
            changeStaffResponse.setRequestId(changeStaffMsg.getConversationId());
            changeStaffResponse.setOperation("change-staff-response");
            changeStaffResponse.setChangeStaffSuccessful(isChangeSuccessful);
            if(isStaffAuthorized && isChangeSuccessful){
                changeStaffResponse.setChangeStaffResponseMessage("Change staff request successful");
            }
            else if (isStaffAuthorized && !isChangeSuccessful){
                changeStaffResponse.setChangeStaffResponseMessage("Change staff request unsuccessful");
            }
            else{
                changeStaffResponse.setChangeStaffResponseMessage("User not authorized.");
            }

            String changeStaffResponseJSON = "";
            try{
                changeStaffResponseJSON = jsonMapper.writeValueAsString(changeStaffResponse);
            }catch (JsonProcessingException e){
                e.printStackTrace();
            }
            Message<String> changeStaffSpringMessage = MessageBuilder.withPayload(changeStaffResponseJSON)
                    .setHeader("requestId", changeStaffMsg.getConversationId())
                    .build();
            staffAgentResponseChannel.send(changeStaffSpringMessage);
        }
        block();
    }

    public boolean changeStaff(ChangeStaffRequest changeStaffRequest) {
        ApplicationContext context = SpringContextProvider.getApplicationContext();
        StaffRepository staffRepository = context.getBean(StaffRepository.class);
        Staff staffAttributeChanges = changeStaffRequest.getChangeStaff();
        Optional<Staff> staffToChange = searchStaffByStaffId(changeStaffRequest.getChangeStaffId());

        if(staffToChange.isEmpty()){
            return false;
        }

        //Do a check of the staff changes to make sure they're not empty/null.
        //If they're null then that part of the RequestBody wasn't filled.
        if(staffAttributeChanges.getFirstName() != null){
            staffToChange.get().setFirstName(staffAttributeChanges.getFirstName());
        }
        if (staffAttributeChanges.getLastName() != null) {
            staffToChange.get().setLastName(staffAttributeChanges.getLastName());
        }
        if(staffAttributeChanges.getUsername() != null){
            staffToChange.get().setUsername(staffAttributeChanges.getUsername());
        }
        if(staffAttributeChanges.getIsAdmin() != staffToChange.get().getIsAdmin()){
            staffToChange.get().setIsAdmin(staffAttributeChanges.getIsAdmin());
        }
        if(staffAttributeChanges.getPassword() != null){
            staffToChange.get().setPassword(staffAttributeChanges.getPassword());
        }
        staffRepository.save(staffToChange.get());
        return true;

    }

    //Helper functions.
    public Optional<Staff> searchStaffByUsername(String username) {
        ApplicationContext context = SpringContextProvider.getApplicationContext();
        StaffRepository staffRepository = context.getBean(StaffRepository.class);
        return staffRepository.findByUsername(username);
    }

    public Optional<Staff> searchStaffByStaffId(Long staffId) {
        ApplicationContext context = SpringContextProvider.getApplicationContext();
        StaffRepository staffRepository = context.getBean(StaffRepository.class);
        return staffRepository.findById(staffId);
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
