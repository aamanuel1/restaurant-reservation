# restaurant-reservation
Restaurant Reservation App

The following is the agent-based backend for a restaurant reservation app that uses a combination of JADE and Spring Boot to integrate a customer portal and a staff portal with RESTful API endpoints with agents that access the database through Spring Boot repositories for each of the entities. Entities are similar to the designed system as provided in Assignment 2, and are included in the entity package, although there have been some changes during implementation. 

To use: Maven was used to manage the software project. The dependencies used are listed in the `pom.xml` file in the `com.project.restaurantbooking` folder. In order to use, maven must be installed in the environment and plugins installed on your IDE, or use IntelliJ IDE as this IDE has a built-in Maven module. The project can be opened from the `pom.xml` file in the root directory if using Intellij. Due to complications with the maven repository for JADE, the jar files are included in the lib folder and referred to in the pom. A jar file can be provided if needed. 

To run, select the `RestaurantbookingApplication` java file and run the application. The application will start up, using the DatabaseLoader to populate a MySQL database. Jade components will run as a Spring Boot configuration as defined in `JadeConfiguration.java`.  

To setup the database with your username and password, navigate to the `application.properties` file. Include your desired username and password prior to running the application.  

As this is a backend system, calls to the api endpoints can be made with Postman or curl calls in the command line. 

Overall Architecture: The overall design of the system employs a typical Spring Boot style model-view-controller framework, the controller/service classes provide RESTful endpoints and setup logic for the JSONs in order to send to JADE agents. As per the JADE documentation, we make use of the JadeGateway API in order to send JAVA objects, either as a Jackson Data Transfer Object, or as a string formatted JSON to `TheGatewayAgent`, which forwards the message to the appropriate agent. The request is stored as a `CompletableFuture` in a hashmap or map structure that is completed in the Gateway Agent or direct from the service through a correlation ID once completed. The agent performs an action with the database through Spring Boot repositories through an `ApplicationContext` object in Spring Boot, as provided in the `SpringContextProvider.java` class, before sending a message direct from the agent back to the service, or by sending a reply message to the gateway agent, which completes a CompletableFuture, given the asynchronous nature of the agents.  

Package explanation: The following are an explanation of the packages in the project. 

- Agent: Includes the Jade agents. 

- Behaviours: Behaviour classes are provided here if not embedded and added as inner classes in the agent. 

- Controller: Spring Boot controllers and controller-service exposing the API endpoints are located here. 

- Entity: Entity classes for the database are provided here. 

- Enums: A cuisine type enum is provided here to support the Restaurant entity. 

- Messagetemplates: This package includes the Data Transfer Object definitions used in the formation of JSONs or to support asynchronous messaging.

- Repo: Includes the Spring Boot repositories that allow access to entity databases. 

- Service: Contains the Spring Boot services that apply logic prior to sending to agents. 
