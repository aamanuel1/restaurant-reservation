package com.project.restaurantbooking;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;

@Configuration
public class JadeSpringIntegrationConfig {

    @Bean
    @Qualifier("StaffAgentRequestChannel")
    public DirectChannel StaffAgentRequestChannel(){
        return new DirectChannel();
    }

    @Bean
    @Qualifier("StaffAgentResponseChannel")
    public DirectChannel StaffAgentReplyChannel(){
        return new DirectChannel();
    }
}
