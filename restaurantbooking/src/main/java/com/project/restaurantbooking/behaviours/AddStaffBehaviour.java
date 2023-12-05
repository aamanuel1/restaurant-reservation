package com.project.restaurantbooking.behaviours;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.restaurantbooking.SpringContextProvider;
import com.project.restaurantbooking.entity.Staff;
import com.project.restaurantbooking.messagetemplates.AddStaffRequest;
import com.project.restaurantbooking.messagetemplates.AddStaffResponse;
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

public class AddStaffBehaviour extends CyclicBehaviour {

    private ObjectMapper jsonMapper = new ObjectMapper();

    //StaffRepository context established in StaffAgent

    private DirectChannel staffAgentResponseChannel;

    private MessageTemplate messageTemplate;
    public AddStaffBehaviour(Agent agent){
        super(agent);
        this.messageTemplate = MessageTemplate.MatchProtocol("add-staff");
    }

    @Override
    public void action() {
        //Establish Spring Application context.
        System.out.println("Getting to action of add staff");
        ApplicationContext context = SpringContextProvider.getApplicationContext();
        staffAgentResponseChannel = context.getBean("StaffAgentReplyChannel", DirectChannel.class);
        ACLMessage addStaffMsg = myAgent.receive(messageTemplate);
        if(addStaffMsg != null){
            AddStaffRequest addStaffRequest = null;

            try{
                addStaffRequest = jsonMapper.readValue(addStaffMsg.getContent(), AddStaffRequest.class);
            } catch(Exception e){
                e.printStackTrace();
                block();
            }

            boolean isStaffAuthorized = this.isStaffAuthorized(addStaffRequest.getUsername());
            boolean isAddSuccessful = false;
            if(isStaffAuthorized){
                try{
                    isAddSuccessful = this.addStaff(addStaffRequest.getAddStaff());
                }catch(Exception e){
                    e.printStackTrace();
                    isAddSuccessful = false;
                }

            }
            AddStaffResponse addStaffResponse = new AddStaffResponse();
            addStaffResponse.setRequestId(addStaffMsg.getConversationId());
            addStaffResponse.setOperation("add-staff-response");
            if(isStaffAuthorized && isAddSuccessful){
                addStaffResponse.setAddStaffSuccessful(true);
                addStaffResponse.setAddStaffResponseMessage("Add staff successful.");
            }
            else{
                addStaffResponse.setAddStaffSuccessful(false);
                addStaffResponse.setAddStaffResponseMessage("Add staff unsuccessful");
            }

            String addStaffResponseJSON = "";
            try{
                addStaffResponseJSON = jsonMapper.writeValueAsString(addStaffResponse);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            System.out.println("Message time.");
            Message<String> addStaffSpringMessage = MessageBuilder.withPayload(addStaffResponseJSON)
                    .setHeader("requestId", addStaffMsg.getConversationId())
                    .build();
            staffAgentResponseChannel.send(addStaffSpringMessage);
        }
        block();
    }

    public boolean addStaff(Staff newStaff){
        ApplicationContext context = SpringContextProvider.getApplicationContext();
        StaffRepository staffRepository = context.getBean(StaffRepository.class);
        Optional<Staff> staffToAdd = searchStaffByUsername(newStaff.getUsername());
        if(staffToAdd.isPresent()){
            return false;
        }
        else if(staffToAdd.isEmpty()){
            //Confirm this behaviour.
            staffRepository.save(newStaff);
            return true;
        }
        else{
            return false;
        }
    }

    public Optional<Staff> searchStaffByUsername(String username){
        ApplicationContext context = SpringContextProvider.getApplicationContext();
        StaffRepository staffRepository = context.getBean(StaffRepository.class);
        return staffRepository.findByUsername(username);
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
