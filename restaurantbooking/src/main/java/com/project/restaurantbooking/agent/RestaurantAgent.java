package com.project.restaurantbooking.agent;

import com.project.restaurantbooking.SpringContextProvider;
import com.project.restaurantbooking.entity.*;
import com.project.restaurantbooking.repo.*;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import lombok.SneakyThrows;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class RestaurantAgent extends Agent {

    protected RestaurantRepository restaurantRepository;
    protected ReservationRepository reservationRepository;
    protected FoodRepository foodRepository;
    protected WaitTimeRepository waitTimeRepository;
    protected CustomerRepository customerRepository;
    protected ShiftRepository shiftRepository;


    @Override
    protected void setup() {
        System.out.println("\n=== RestaurantAgent "+getAID().getName()+" is ready. ===\n");
        ApplicationContext context = SpringContextProvider.getApplicationContext();
        restaurantRepository = context.getBean(RestaurantRepository.class);
        reservationRepository = context.getBean(ReservationRepository.class);
        foodRepository = context.getBean(FoodRepository.class);
        waitTimeRepository = context.getBean(WaitTimeRepository.class);
        customerRepository = context.getBean(CustomerRepository.class);
        shiftRepository = context.getBean(ShiftRepository.class);


        System.out.println("\n========= RestaurantAgent - setup - Context:"+ context+ "\n");
        // Register the restaurant-reservation service in the yellow pages
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("restaurant-reservation");
        sd.setName(getLocalName());
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        // Add behaviour to handle reservation requests
        addBehaviour(new ReservationRequestsServer());
    }


    private class ReservationRequestsServer extends CyclicBehaviour {
        private ReservationRequestsServer() {

        }
        @SneakyThrows
        @Override
        public void action() {
            System.out.println("\n=== RestaurantAgent -- Receiving msg. ===\n");
            // Message template to listen for reservation requests
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
            ACLMessage msg = myAgent.receive(mt);

            if (msg != null) {
                System.out.println("\n=== RestaurantAgent -- Msg Rcd. ===\n"+ msg);
                System.out.println("\nRestaurantAgent: Msg Sender"+ msg.getSender() +"\n");

                String content = msg.getContent();
                JSONObject json = new JSONObject(content);
                String correlationId = json.getString("correlationId");
                String task = json.getString("task");

                String responseToGateway = null;
                System.out.println("RestAgent: data "+ json.getJSONObject("data"));

                if (task.equals("inquire")) {
                    String foodName = json.getJSONObject("data").getString("foodName");
                    System.out.println("RestAgent: foodName: "+ json.getJSONObject("data"));
                    responseToGateway = inquireAboutFood(foodName, correlationId, task);
                    System.out.println("\nRestAgCycBehaviour: ResponseJSON "+ responseToGateway);

                } else if (task.equals("reserve")) {
                    String foodName = json.getJSONObject("data").getString("foodName");
                    String waitTime = json.getJSONObject("data").getString("waitTime");
                    String customerEmail = json.getJSONObject("data").getString("email");
                    responseToGateway = makeReservation(foodName, waitTime, correlationId, task, customerEmail);

                } else if (task.equals("getReservationDetails")) {
                    long reservationNumber = json.getJSONObject("data").getLong("reservationNumber");
                    responseToGateway = getReservationDetails(correlationId, task, reservationNumber);

                } else if (task.equals("cancelReservation")) {
                    long reservationNumber = json.getJSONObject("data").getLong("reservationNumber");
                    responseToGateway = cancelReservation(correlationId, task, reservationNumber);

                } else if (task.equals("getAllReservations")) {

                String customerEmail = json.getJSONObject("data").getString("email");
                    responseToGateway = fetchAllReservationForCustomer(responseToGateway, correlationId, task, customerEmail);
                }

                // sending reply
                ACLMessage reply = msg.createReply();
                reply.setPerformative(ACLMessage.INFORM);
                reply.setContent(responseToGateway);
                reply.setConversationId("reserve");
                reply.setProtocol("reserve");
                myAgent.send(reply);
                System.out.println("\nReply Sent\n");
            } else {
                System.out.println("\n=== RestaurantAgent -- No Msg. ===\n"+ msg);
                block();
            }
        }

        private String getReservationDetails(String correlationId, String task, long reservationNumber) {
            String responseToGateway;
            String message;
            Optional<Reservation> reservation = reservationRepository.findReservationByReservationNumber(reservationNumber);
            if (reservation.isEmpty()) {
                message = "This reservation does not exist";
                return responseToGateway = String.format("""
                    {
                        "correlationId": "%s",
                        "task": "%s",
                        "message": %s,
                    }
                    """, correlationId, task, JSONObject.quote(message));
            } else {
                Reservation reservation1 = reservation.get();
                JSONObject reservationJSON = new JSONObject(reservation1);
                message = "See your reservation details below";

                return responseToGateway = String.format("""
                    {
                        "correlationId": "%s",
                        "task": "%s",
                        "message": %s,
                        "reservation": %s
                    }
                    """, correlationId, task, JSONObject.quote(message), reservationJSON);
            }
        }

        private String cancelReservation(String correlationId, String task, long reservationNumber) {
            String message;
            Optional<Reservation> reservation = reservationRepository.findReservationByReservationNumber(reservationNumber);
            if (reservation.isEmpty()) {
                message = "This reservation does not exist";
                return String.format("""
                    {
                        "correlationId": "%s",
                        "task": "%s",
                        "message": %s,
                    }
                    """, correlationId, task, JSONObject.quote(message));
            } else {
                Reservation reservation1 = reservation.get();
                reservationRepository.deleteById(reservation1.getId());
                JSONObject reservationJSON = new JSONObject(reservation1);
                message = "Success! Your reservation has been cancelled successfully!";
                String status = "Success!";

                return String.format("""
                    {
                        "correlationId": "%s",
                        "task": "%s",
                        "message": %s,
                        "status": %s
                    }
                    """, correlationId, task, JSONObject.quote(message), JSONObject.quote(status));
            }
        }

        public String inquireAboutFood(String foodName, String correlationId, String task) {
            String message = null;
            Food food = foodRepository.findFoodByName(foodName);
            JSONObject foodJson = (food != null) ? new JSONObject(food) : new JSONObject(); // Use an empty JSON object instead of null

            List<Restaurant> restaurantList = null;
            if (food != null) {
                restaurantList = restaurantRepository.findRestaurantsByCuisine(food.getCuisine());
                if (!restaurantList.isEmpty()) {
                    List<Shift> shiftList = shiftRepository.findByRestaurantId(restaurantList.get(0).getRestaurantId());
                    if (shiftList.isEmpty()) {
                        message = "No restaurant has open tables for the time you request";
                        return String.format("""
                    {
                        "correlationId": "%s",
                        "task": "%s",
                        "food": %s,
                        "message": %s
                    }
                    """, correlationId, task, foodJson.toString(), JSONObject.quote(message));
                    }
                }

                message = foodName + " is available at the Restaurants listed below. Proceed to make your reservation.";
            } else {
                message = foodName + " not available. The food you are looking for is not served by any of our Restaurants";
            }
            JSONArray restaurantListJson = (restaurantList != null) ? new JSONArray(restaurantList) : new JSONArray(); // Use an empty JSON array instead of null
            return String.format("""
                    {
                        "correlationId": "%s",
                        "task": "%s",
                        "food": %s,
                        "message": %s,
                        "restaurants": %s
                    }
                    """, correlationId, task, foodJson.toString(), JSONObject.quote(message), restaurantListJson.toString());
        }

        public String makeReservation(String foodName, String waitTimeString, String correlationId, String task, String customerEmail) {
            Customer customer = getCustomerInfo(customerEmail);
            if (customer == null) {
                String message = "No customer with email: " + customerEmail + " in the database";
                return String.format("""
                        {
                            "correlationId": "%s",
                            "task": "%s",
                            "message": "%s",
                        }
                        """, correlationId, task, message);
            }
            Reservation reservation = null;
            long reservationNumber = 0L;
            JSONObject restaurantJSON = null;

            String message = null;
            List<WaitTime> waitTimes = waitTimeRepository.findWaitTimesByMinutesIsLessThan(Long.valueOf(waitTimeString));
            List<Restaurant> eligibleRestaurantList = new ArrayList<>();
            Restaurant restaurant = null;

            String responseToGateway;
            if (waitTimes.isEmpty()) {
                message = "Sorry! No restaurant currently has a wait time less than "+ waitTimeString;
                responseToGateway = String.format("""
                    {
                        "correlationId": "%s",
                        "task": "%s",
                        "message": "%s",
                    }
                    """, correlationId, task, message);
                return responseToGateway;

            } else {
                for (WaitTime wt : waitTimes) {
                    eligibleRestaurantList.add(wt.getRestaurant());
                }
            }

            Food food = foodRepository.findFoodByName(foodName);
            JSONObject foodJson = (food != null) ? new JSONObject(food) : null;
            List<Restaurant> restaurantList = null;

            if (food != null) {
                restaurantList = restaurantRepository.findRestaurantsByCuisine(food.getCuisine());
            }

            assert eligibleRestaurantList != null;
            assert restaurantList != null;
            if (!restaurantList.isEmpty()) {
                Set<Restaurant> restaurantSet = eligibleRestaurantList.stream()
                        .distinct()
                        .filter(restaurantList::contains)
                        .collect(Collectors.toSet());

                if (restaurantSet.isEmpty()) {
                    message = "The restaurants available don't serve " + foodName +". Adjust your wait time to make a reservation";
                } else {
                    restaurant = restaurantSet.iterator().next();
                    reservation = Reservation.builder()
                            .restaurant(restaurant)
                            .customer(customer)
                            .startTime(LocalDateTime.now())
                            .endTime(LocalDateTime.now())
                            .reservationNumber(Reservation.generateReservationNumber())
                            .build();

                    reservation = reservationRepository.save(reservation);
                    message = "Reservation confirmed";
                    reservationNumber = reservation.getReservationNumber();
                    restaurantJSON = new JSONObject(restaurant);
                }
            }

            responseToGateway = String.format("""
                    {
                        "correlationId": "%s",
                        "task": "%s",
                        "food": %s,
                        "message": "%s",
                        "reservationNumber": %d,
                        "restaurant": %s
                    }
                    """, correlationId, task, foodJson, message, reservationNumber, restaurantJSON);
            return responseToGateway;
        }

        public Customer getCustomerInfo(String email) {
            return customerRepository.findCustomerByEmail(email);
        }

        public String fetchAllReservationForCustomer(String responseToGateway, String correlationId, String task, String customerEmail) {
            String message = null;
            Customer customer = getCustomerInfo(customerEmail);
            if (customer == null) {
                message = "No customer with email: " + customerEmail + " in the database";
                return String.format("""
                        {
                            "correlationId": "%s",
                            "task": "%s",
                            "message": "%s",
                        }
                        """, correlationId, task, message);
            }

            List<Reservation> reservationList = reservationRepository.findAllByCustomer(customer);
            if (reservationList.isEmpty()) {
                message = "Customer with email: " + customerEmail + " has no reservations";
                return String.format("""
                        {
                            "correlationId": "%s",
                            "task": "%s",
                            "message": "%s",
                        }
                        """, correlationId, task, message);
            }

            JSONArray reservationsArray = new JSONArray(reservationList);
            message = "You have some reservations. See list below";
            responseToGateway = String.format("""
                    {
                        "correlationId": "%s",
                        "task": "%s",
                        "message": "%s",
                        "activeReservations": %s
                    }
                    """, correlationId, task, message, reservationsArray);

            return  responseToGateway;
        }
    }


    public List<Restaurant> restaurantListServingFood(Food food) {
        return restaurantRepository.findRestaurantsByCuisine(food.getCuisine());
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
