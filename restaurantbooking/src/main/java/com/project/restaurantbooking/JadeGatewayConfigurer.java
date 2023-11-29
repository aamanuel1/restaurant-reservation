//package com.project.restaurantbooking;
//
//import com.project.restaurantbooking.MyGatewayAgent;
//import jade.wrapper.gateway.JadeGateway;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.context.annotation.Configuration;
//import javax.annotation.PostConstruct;
//
//@Configuration
//public class JadeGatewayConfigurer {
//
//    private static final Logger logger = LoggerFactory.getLogger(JadeGatewayConfigurer.class);
//
//    @PostConstruct
//    public void initJadeGateway() {
//        try {
//            JadeGateway.init(MyGatewayAgent.class.getName(), null);
//            logger.info("Jade Gateway initialized successfully");
//        } catch (Exception e) {
//            logger.error("Error initializing Jade Gateway: ", e);
//        }
//    }
//}