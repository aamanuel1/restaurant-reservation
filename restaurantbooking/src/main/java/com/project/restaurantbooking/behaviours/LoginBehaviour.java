package com.project.restaurantbooking.behaviours;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.restaurantbooking.agent.StaffAgent;
import com.project.restaurantbooking.entity.Staff;
import com.project.restaurantbooking.messagetemplates.LoginRequest;
import com.project.restaurantbooking.messagetemplates.LoginResponse;
import com.project.restaurantbooking.repo.StaffRepository;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.Optional;

public class LoginBehaviour extends CyclicBehaviour {

    private ObjectMapper jsonMapper = new ObjectMapper();
    private final StaffRepository staffRepository;
    LoginRequest loginRequest;

    public LoginBehaviour(Agent agent, StaffRepository staffRepository){
        super(agent);
        this.staffRepository = staffRepository;
    }

    @Override
    public void action() {
        ACLMessage loginMsg = myAgent.receive();
        if(loginMsg != null){
            try{
                loginRequest = jsonMapper.readValue(loginMsg.getContent(), LoginRequest.class);
            } catch(Exception e){
                e.printStackTrace();
            }
            Optional<Staff> loggedInStaff = checkStaffCredentials(loginRequest.getUsername(), loginRequest.getPassword());
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setOperation("login-response");
            if(loggedInStaff.isPresent()){
                //Send a response json back.0
                loginResponse.setSuccessfulLogin(true);
                loginResponse.setStaff(loggedInStaff.get());
            }
            else{
                loginResponse.setSuccessfulLogin(false);
                loginResponse.setStaff(loggedInStaff.get());
            }

            String loginResponseJSON = "";

            try{
                loginResponseJSON = jsonMapper.writeValueAsString(loginResponse);
            } catch(Exception e){
                e.printStackTrace();
            }

            ACLMessage loginResponseMsg = loginMsg.createReply();
            loginResponseMsg.setPerformative(ACLMessage.INFORM);
            loginResponseMsg.setContent(loginResponseJSON);
            myAgent.send(loginResponseMsg);
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
