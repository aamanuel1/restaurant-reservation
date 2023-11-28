package com.project.restaurantbooking.behaviours;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.restaurantbooking.SpringContextProvider;
import com.project.restaurantbooking.entity.Staff;
import com.project.restaurantbooking.messagetemplates.LoginRequest;
import com.project.restaurantbooking.messagetemplates.LoginResponse;
import com.project.restaurantbooking.repo.StaffRepository;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.util.Optional;

public class LoginBehaviour extends CyclicBehaviour {

    private ObjectMapper jsonMapper = new ObjectMapper();
    private final StaffRepository staffRepository;

    private DirectChannel staffAgentResponseChannel;

    public LoginBehaviour(Agent agent, StaffRepository staffRepository){
        super(agent);
        this.staffRepository = staffRepository;
    }

    @Override
    public void action() {
        ApplicationContext context = SpringContextProvider.getApplicationContext();
        staffAgentResponseChannel = context.getBean("StaffAgentReplyChannel", DirectChannel.class);
        ACLMessage loginMsg = myAgent.receive();
        if(loginMsg != null){
            LoginRequest loginRequest = null;

            //Have to have a way to check the operation type.
            try{
                loginRequest = jsonMapper.readValue(loginMsg.getContent(), LoginRequest.class);
            } catch(Exception e){
                //It's not a login request so block and return message.
                e.printStackTrace();
                block();
            }
            if(!loginRequest.getOperation().equals("login")){
                block();
            }

            Optional<Staff> loggedInStaff = checkStaffCredentials(loginRequest.getUsername(), loginRequest.getPassword());
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setRequestId(loginMsg.getConversationId());
            loginResponse.setOperation("login-response");
            if(loggedInStaff.isPresent()){
                //Send a response json back.0
                loginResponse.setSuccessfulLogin(true);
                loginResponse.setStaff(loggedInStaff.get());
            }
            else {
                loginResponse.setSuccessfulLogin(false);
                loginResponse.setStaff(null);
            }

            try {
                myAgent.putO2AObject(loginResponse, AgentController.ASYNC);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            String loginResponseJSON = "";

            try{
                loginResponseJSON = jsonMapper.writeValueAsString(loginResponse);
            } catch(Exception e){
                e.printStackTrace();
            }

//            ACLMessage loginResponseMsg = loginMsg.createReply();
//            loginResponseMsg.setPerformative(ACLMessage.INFORM);
//            loginResponseMsg.setContent(loginResponseJSON);
//            myAgent.send(loginResponseMsg);
            Message<String> loginSpringMessage = MessageBuilder.withPayload(loginResponseJSON)
                            .setHeader("requestId", loginMsg.getConversationId())
                            .build();
            staffAgentResponseChannel.send(loginSpringMessage);
        }
        block();
    }

    public Optional<Staff> checkStaffCredentials(String username, String password){
        Optional<Staff> staff = staffRepository.findByUsername(username);
        if(staff.isEmpty()){
            return Optional.empty();
        }
        if(!staff.get().getPassword().equals(password)){
            return Optional.empty();
        }
        return staff;
    }
}
