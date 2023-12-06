package com.project.restaurantbooking.agent;

import com.project.restaurantbooking.ApplicationContextProvider;
import com.project.restaurantbooking.SpringContextProvider;
import com.project.restaurantbooking.behaviours.LeaveFeedbackBehaviour;
import com.project.restaurantbooking.entity.Restaurant;
import com.project.restaurantbooking.repo.CustomerRepository;
import com.project.restaurantbooking.repo.RestaurantRepository;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import org.springframework.context.ApplicationContext;

public class CustomerAgent extends Agent {
    CustomerRepository customerRepository;
    RestaurantRepository restaurantRepository;
    @Override
    protected void setup() {
        System.out.println("\n=== CustomerAgent "+getAID().getName()+" is ready. ===\n");

        ApplicationContext context = SpringContextProvider.getApplicationContext();
        customerRepository = context.getBean(CustomerRepository.class);
        restaurantRepository = context.getBean(RestaurantRepository.class);

        System.out.println("\n========= CustomerAgent - setup - Context:"+ context+ "\n");

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("customer");
        sd.setName(getLocalName());
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        // Add behaviour to handle reservation requests
        addBehaviour(new LeaveFeedbackBehaviour());

        // Add other behaviours as needed, for example, handling cancellations
        // addBehaviour(new CancellationServer());

    }
    @Override
    protected void takeDown() {
        // Deregister from the yellow pages
        try {
            DFService.deregister(this);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }
}
