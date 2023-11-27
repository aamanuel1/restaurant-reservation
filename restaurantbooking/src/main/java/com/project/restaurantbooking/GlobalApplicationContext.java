package com.project.restaurantbooking;

import org.springframework.context.ApplicationContext;

public class GlobalApplicationContext {
    private static ApplicationContext context;

    public static void setApplicationContext(ApplicationContext applicationContext) {
        System.out.println("\nApplicationContext being set in GlobalApplicationContext\n");
        context = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        if (context == null) {
            System.out.println("\nApplicationContext is null in GlobalApplicationContext\n");
        }
        return context;
    }
}

