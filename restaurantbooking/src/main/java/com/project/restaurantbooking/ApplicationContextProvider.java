//package com.project.restaurantbooking;
//
//import org.springframework.beans.BeansException;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.ApplicationContextAware;
//import org.springframework.stereotype.Component;
//
//@Component
//public class ApplicationContextProvider implements ApplicationContextAware {
//    private static ApplicationContext applicationContext;
//
//    @Override
//    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
//        System.out.println("\nApplicationContext being set in ApplicationContextProvider\n");
//        applicationContext = ctx;
//    }
//
//    public static ApplicationContext getApplicationContext() {
//        if (applicationContext == null) {
//            System.out.println("\nApplicationContext is null in ApplicationContextProvider\n");
//        }
//        return applicationContext;
//    }
//}
//
//
