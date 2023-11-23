package com.project.restaurantbooking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class RestaurantbookingApplication {

	public static void main(String[] args) {

//		SpringApplication.run(RestaurantbookingApplication.class, args);
		SpringApplicationBuilder builder= new SpringApplicationBuilder(RestaurantbookingApplication.class);
		builder.headless(false);
		ConfigurableApplicationContext context = builder.run(args);
	}

}
