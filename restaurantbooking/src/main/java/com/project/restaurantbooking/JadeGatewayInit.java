//package com.project.restaurantbooking;
//
//import jade.core.Profile;
//import jade.core.ProfileImpl;
//import jade.wrapper.gateway.JadeGateway;
//import org.springframework.boot.CommandLineRunner;
//
//public class JadeGatewayInit implements CommandLineRunner {
//    @Override
//    public void run(String... args) throws Exception {
//        ProfileImpl jadeProfile = new ProfileImpl();
//        jadeProfile.setParameter(Profile.MAIN_HOST, "localhost");
//        jadeProfile.setParameter(Profile.MAIN_PORT, "1099");
//        JadeGateway.init("com.project.restaurantbooking.agent.RestaurantGatewayAgent", jadeProfile.getProperties());
//    }
//
//}
