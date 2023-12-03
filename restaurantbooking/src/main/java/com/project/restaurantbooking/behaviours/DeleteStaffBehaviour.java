package com.project.restaurantbooking.behaviours;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.restaurantbooking.ApplicationContextProvider;
import com.project.restaurantbooking.SpringContextProvider;
import com.project.restaurantbooking.entity.Staff;
import com.project.restaurantbooking.messagetemplates.DeleteStaffRequest;
import com.project.restaurantbooking.messagetemplates.DeleteStaffResponse;
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

public class DeleteStaffBehaviour extends CyclicBehaviour {

    private ObjectMapper jsonMapper = new ObjectMapper();

    private DirectChannel staffAgentResponseChannel;

    private MessageTemplate messageTemplate;

    public DeleteStaffBehaviour(Agent agent) {
        super(agent);
        this.messageTemplate = MessageTemplate.MatchProtocol("delete-staff");
    }

    @Override
    public void action() {
        ApplicationContext context = ApplicationContextProvider.getApplicationContext();
        staffAgentResponseChannel = context.getBean("StaffAgentReplyChannel", DirectChannel.class);
        ACLMessage deleteStaffMsg = myAgent.receive(messageTemplate);
        if (deleteStaffMsg != null) {
            DeleteStaffRequest deleteStaffRequest = null;

            try {
                deleteStaffRequest = jsonMapper.readValue(deleteStaffMsg.getContent(), DeleteStaffRequest.class);
            } catch (Exception e) {
                e.printStackTrace();
                block();
            }

            boolean isStaffAuthorized = this.isStaffAuthorized(deleteStaffRequest.getUsername());
            boolean isDeleteSuccessful = false;
            if (isStaffAuthorized) {
                isDeleteSuccessful = this.deleteStaff(deleteStaffRequest);
            }
            DeleteStaffResponse deleteStaffResponse = new DeleteStaffResponse();
            deleteStaffResponse.setRequestId(deleteStaffMsg.getConversationId());
            deleteStaffResponse.setOperation("delete-staff-response");
            deleteStaffResponse.setDeleteStaffSuccessful(isDeleteSuccessful);
            if (isStaffAuthorized && isDeleteSuccessful) {
                deleteStaffResponse.setDeleteStaffResponseMessage("Delete staff successful");
            } else if (isStaffAuthorized && !isDeleteSuccessful) {
                deleteStaffResponse.setDeleteStaffResponseMessage("Delete staff unsuccessful");
            } else {
                deleteStaffResponse.setDeleteStaffResponseMessage("User not authorized.");
            }

            String deleteStaffResponseJSON = "";
            try {
                deleteStaffResponseJSON = jsonMapper.writeValueAsString(deleteStaffResponse);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            Message<String> deleteStaffSpringMessage = MessageBuilder.withPayload(deleteStaffResponseJSON)
                    .setHeader("requestId", deleteStaffMsg.getConversationId())
                    .build();
            staffAgentResponseChannel.send(deleteStaffSpringMessage);
        }
        block();
    }

    public boolean deleteStaff(DeleteStaffRequest deleteStaffRequest) {
        ApplicationContext context = SpringContextProvider.getApplicationContext();
        StaffRepository staffRepository = context.getBean(StaffRepository.class);
        Optional<Staff> staffToDelete = null;
        if (deleteStaffRequest.getDeleteByStaffId() == -1) {
            staffToDelete = searchStaffByUsername(deleteStaffRequest.getDeleteByStaffUsername());
        } else if (deleteStaffRequest.getDeleteByStaffUsername().equals("")) {
            staffToDelete = serachStaffByStaffId(deleteStaffRequest.getDeleteByStaffId());
        }

        if(staffToDelete.isPresent()){
            staffRepository.delete(staffToDelete.get());
            return true;
        }
        else{
            return false;
        }
    }

    public Optional<Staff> searchStaffByUsername(String username) {
        ApplicationContext context = SpringContextProvider.getApplicationContext();
        StaffRepository staffRepository = context.getBean(StaffRepository.class);
        return staffRepository.findByUsername(username);
    }

    public Optional<Staff> serachStaffByStaffId(Long staffId) {
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

