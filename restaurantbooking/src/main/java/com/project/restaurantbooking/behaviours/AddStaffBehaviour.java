package com.project.restaurantbooking.behaviours;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.restaurantbooking.SpringContextProvider;
import com.project.restaurantbooking.entity.Restaurant;
import com.project.restaurantbooking.entity.Staff;
import com.project.restaurantbooking.messagetemplates.AddStaffRequest;
import com.project.restaurantbooking.messagetemplates.AddStaffResponse;
import com.project.restaurantbooking.repo.RestaurantRepository;
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
        //Establish Spring Application context. Response channel to send back to spring boot service.
        ApplicationContext context = SpringContextProvider.getApplicationContext();
        staffAgentResponseChannel = context.getBean("StaffAgentReplyChannel", DirectChannel.class);
        ACLMessage addStaffMsg = myAgent.receive(messageTemplate);
        if(addStaffMsg != null){
            //Set up the request object, then turn the json into a request message object.
            AddStaffRequest addStaffRequest = null;

            try{
                addStaffRequest = jsonMapper.readValue(addStaffMsg.getContent(), AddStaffRequest.class);
            } catch(Exception e){
                e.printStackTrace();
                block();
            }
            //Match the operation stored in the protocol portion of the message, if it doesn't match put it back.
            if(!addStaffMsg.getProtocol().equals("add-staff")){
                myAgent.putBack(addStaffMsg);
            }

            //Check if staff is admin.
            boolean isStaffAuthorized = this.isStaffAuthorized(addStaffRequest.getUsername());
            boolean isAddSuccessful = false;
            //If staff is authorized, then add the staff member.
            if(isStaffAuthorized){
                try{
                    isAddSuccessful = this.addStaff(addStaffRequest.getAddStaff(), addStaffRequest.getRestaurantId());
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
            Message<String> addStaffSpringMessage = MessageBuilder.withPayload(addStaffResponseJSON)
                    .setHeader("requestId", addStaffMsg.getConversationId())
                    .build();
            staffAgentResponseChannel.send(addStaffSpringMessage);
        }
        block();
    }

    public boolean addStaff(Staff newStaff, Long restaurantId){
        //Get the staff repository as a dependency. Then search for the user to avoid copies.
        ApplicationContext context = SpringContextProvider.getApplicationContext();
        StaffRepository staffRepository = context.getBean(StaffRepository.class);
        RestaurantRepository restaurantRepository = context.getBean(RestaurantRepository.class);
        Optional<Staff> staffToAdd = searchStaffByUsername(newStaff.getUsername());
        //Get the restaurant from the restaurant database.
        Optional<Restaurant> newStaffRestaurant = restaurantRepository.findById(restaurantId);
        if(newStaffRestaurant.isEmpty()){
            //We can't find the restaurant so we can't add the staff member.
            return false;
        }

        if(staffToAdd.isPresent()){
            //if the username is not unique then stop.
            return false;
        }
        else if(staffToAdd.isEmpty()){
            //set the restaurant.
            newStaff.setRestaurant(newStaffRestaurant.get());
            //If the staff username isn't in the database, then save the newstaff.
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
