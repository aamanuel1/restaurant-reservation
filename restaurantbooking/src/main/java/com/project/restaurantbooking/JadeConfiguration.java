package com.project.restaurantbooking;

import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JadeConfiguration {

    //Set parameter values, see application.properties for values.
    @Value("${jade.show-gui}")
    private boolean showGui;

    @Value("${jade.container-name}")
    private String containerName;

    @Value("${jade.agents")
    private String agents;

    //Create new bean for the jade container, and start the jade container with args.
    @Bean
    public ContainerController jadeContainer() {
        Runtime jadeRuntime = Runtime.instance();
        Profile jadeProfile = new ProfileImpl();
        jadeProfile.setParameter(Profile.CONTAINER_NAME, containerName);
        jadeProfile.setParameter(Profile.GUI, Boolean.toString(showGui));
//        jadeProfile.setParameter(Profile.AGENTS, agents);
        jadeProfile.setParameter(Profile.MAIN_HOST, "localhost");
        jadeProfile.setParameter(Profile.MAIN_PORT, "1099");

        //Create container based on the profile.
        ContainerController jadeContainer = jadeRuntime.createMainContainer(jadeProfile);

        staffAgentController(jadeContainer);

        return jadeContainer;
    }

    @Bean
    public AgentController staffAgentController(ContainerController container){
        try {
            AgentController staffAgent = container.createNewAgent("staffAgent",
                    "com.project.restaurantbooking.agent.StaffAgent",
                    null);
            staffAgent.start();
            return staffAgent;
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
        return null;
    }

}
