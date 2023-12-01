//package com.project.restaurantbooking.service;
//
//import com.project.restaurantbooking.agent.TheGatewayAgent;
//import jade.core.Profile;
//import jade.core.ProfileImpl;
//import jade.core.Runtime;
//import jade.wrapper.AgentController;
//import jade.wrapper.ContainerController;
//import jade.wrapper.StaleProxyException;
//import jade.wrapper.gateway.JadeGateway;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.ApplicationContext;
//import org.springframework.stereotype.Service;
//
//import javax.annotation.PostConstruct;
//
//@Service
//public class JadeService {
//
////    private final ApplicationContext applicationContext;
//
//    @Value("${jade.show-gui}")
//    private boolean showGui;
//
//    @Value("${jade.container-name}")
//    private String containerName;
//
//    private ContainerController jadeContainer;
//
////    public JadeService(ApplicationContext applicationContext) {
////
////        System.out.println("\nJadeService C'tor - AppContext: "+ applicationContext+"\n");
////        this.applicationContext = applicationContext;
////    }
//
//    @PostConstruct
//    public void initJade() {
//        Runtime jadeRuntime = Runtime.instance();
//        Profile jadeProfile = new ProfileImpl();
//        jadeProfile.setParameter(Profile.CONTAINER_NAME, containerName);
//        jadeProfile.setParameter(Profile.GUI, Boolean.toString(showGui));
//        jadeProfile.setParameter(Profile.MAIN_HOST, "localhost");
//        jadeProfile.setParameter(Profile.MAIN_PORT, "1099");
//
//        jadeContainer = jadeRuntime.createMainContainer(jadeProfile);
//
//        try {
//            AgentController staffAgent = jadeContainer.createNewAgent(
//                    "staffAgent",
//                    "com.project.restaurantbooking.agent.StaffAgent",
//                    null);
//            staffAgent.start();
//            AgentController restaurantAgent = jadeContainer.createNewAgent(
//                    "restaurantAgent",
//                    "com.project.restaurantbooking.agent.RestaurantAgent",
//                    null);
//            restaurantAgent.start();
//
////            System.out.println("\nJadeService initJade - AppContext: "+ applicationContext+"\n");
////            MyGatewayAgent.setApplicationContext(applicationContext);
//            JadeGateway.init("com.project.restaurantbooking.agent.TheGatewayAgent", jadeProfile.getBootProperties());
//
//        } catch (StaleProxyException e) {
//            e.printStackTrace();
//        }
//    }
//
//    // Add additional methods as needed, e.g., to stop agents or retrieve the container controller
//}
//
