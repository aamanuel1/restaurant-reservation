package com.project.restaurantbooking;

import com.project.restaurantbooking.entity.*;
import com.project.restaurantbooking.enums.Cuisine;
import com.project.restaurantbooking.repo.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataBaseLoader implements CommandLineRunner {
    private final CustomerRepository customerRepository;
    private final ReservationRepository reservationRepository;
    private final RestaurantRepository restaurantRepository;
    private final ShiftRepository shiftRepository;
    private final StaffRepository staffRepository;
    private final TableRepository tableRepository;
    private final FoodRepository foodRepository;
    private final FavouriteFoodRepository favouriteFoodRepository;

    public DataBaseLoader(CustomerRepository customerRepository, ReservationRepository reservationRepository,
                          RestaurantRepository restaurantRepository, ShiftRepository shiftRepository,
                          StaffRepository staffRepository, TableRepository tableRepository,
                          FoodRepository foodRepository, FavouriteFoodRepository favouriteFoodRepository) {
        this.customerRepository = customerRepository;
        this.reservationRepository = reservationRepository;
        this.restaurantRepository = restaurantRepository;
        this.shiftRepository = shiftRepository;
        this.staffRepository = staffRepository;
        this.tableRepository = tableRepository;
        this.foodRepository = foodRepository;
        this.favouriteFoodRepository = favouriteFoodRepository;
    }

    /**
     * @param args
     * @throws Exception
     */
    @Override
    public void run(String... args) throws Exception {
        // creating customer profiles
        Customer customer1 = customerRepository.save(new Customer(
                "Melon", "Tusk", 1112223333L,"mtusk123@email.com"));

        Customer customer2 = customerRepository.save(new Customer(
                "Gandalf", "TheGrey", 5559997777L,"gandalf@email.com"));

        Customer customer3 = customerRepository.save(new Customer(
                "Samwise", "Gamgee", 8886662121L,"sam@email.com"));

        // creating foods
        Food food1 = foodRepository.save(new Food("Sushie", Cuisine.JAPANESE));
        Food food2 = foodRepository.save(new Food("Tacos", Cuisine.MEXICAN));
        Food food3 = foodRepository.save(new Food("Pizza", Cuisine.ITALIAN));
        Food food4 = foodRepository.save(new Food("Croissant", Cuisine.FRENCH));
        Food food5 = foodRepository.save(new Food("Curry", Cuisine.INDIAN));
        Food food6 = foodRepository.save(new Food("Hamburger", Cuisine.AMERICAN));
        Food food7 = foodRepository.save(new Food("Paella", Cuisine.SPANISH));
        Food food8 = foodRepository.save(new Food("Kimchi", Cuisine.KOREAN));
        Food food9 = foodRepository.save(new Food("Poutine", Cuisine.CANADIAN));
        Food food10 = foodRepository.save(new Food("Borscht", Cuisine.RUSSIAN));
        Food food11 = foodRepository.save(new Food("Dim Sum", Cuisine.CHINESE));
        Food food12 = foodRepository.save(new Food("Tagine", Cuisine.MOROCCAN));
        Food food13 = foodRepository.save(new Food("Feijoada", Cuisine.BRAZILIAN));
        Food food14 = foodRepository.save(new Food("Pad Thai ", Cuisine.THAI));
        Food food15 = foodRepository.save(new Food("Baklava", Cuisine.TURKISH));

        // adding favourite foods to customers profiles
        FavouriteFoods favouriteFoods1 = favouriteFoodRepository.save(FavouriteFoods.builder()
                .customer(customer1)
                .food(food1)
                .build());
        FavouriteFoods favouriteFoods2 = favouriteFoodRepository.save(FavouriteFoods.builder()
                .customer(customer1)
                .food(food2)
                .build());
        FavouriteFoods favouriteFoods3 = favouriteFoodRepository.save(FavouriteFoods.builder()
                .customer(customer1)
                .food(food3)
                .build());
        FavouriteFoods favouriteFoods4 = favouriteFoodRepository.save(FavouriteFoods.builder()
                .customer(customer1)
                .food(food4)
                .build());

        FavouriteFoods favouriteFoods5 = favouriteFoodRepository.save(FavouriteFoods.builder()
                .customer(customer2)
                .food(food5)
                .build());
        FavouriteFoods favouriteFoods6 = favouriteFoodRepository.save(FavouriteFoods.builder()
                .customer(customer2)
                .food(food6)
                .build());
        FavouriteFoods favouriteFoods7 = favouriteFoodRepository.save(FavouriteFoods.builder()
                .customer(customer2)
                .food(food7)
                .build());
        FavouriteFoods favouriteFoods8 = favouriteFoodRepository.save(FavouriteFoods.builder()
                .customer(customer2)
                .food(food8)
                .build());

        // creating restaurants and tables
        Restaurant frenchRestaurant = new Restaurant(
                "Le Gourmet Parisien",
                "2468 Bistro Blvd, Calgary",
                "T1E 2LP",
                new HashSet<>(Set.of(Cuisine.FRENCH, Cuisine.MOROCCAN, Cuisine.ITALIAN, Cuisine.SPANISH))
        );
        frenchRestaurant = restaurantRepository.save(frenchRestaurant);

        for (int i=0; i < 10; i++) {
            tableRepository.save(
                    RestaurantTable.builder()
                            .available(true)
                            .tableOccupancyNum(10)
                            .restaurant(frenchRestaurant)
                            .build()
            );
        }

        Restaurant indianRestaurant = new Restaurant(
                "Russian Masala",
                "1357 Curry Ln, Mumbai-Moscow",
                "MB1357",
                new HashSet<>(Set.of(Cuisine.INDIAN, Cuisine.CHINESE, Cuisine.CANADIAN, Cuisine.RUSSIAN))
        );
        indianRestaurant = restaurantRepository.save(indianRestaurant);
        for (int i=0; i < 10; i++) {
            tableRepository.save(
                    RestaurantTable.builder()
                            .available(true)
                            .tableOccupancyNum(10)
                            .restaurant(indianRestaurant)
                            .build()
            );
        }

        // adding staff
        Staff staff1 = staffRepository.save(
                new Staff("Worker", "One", "worker1", true, "password"));

        Staff staff2 = staffRepository.save(
                new Staff("Hard", "worker", "hard1", false, "password"));

        Staff staff3 = staffRepository.save(
                new Staff("Second", "InCommand", "2ic", false, "password"));


    }

    private FavouriteFoods addFavouriteFood(Customer customer, Food food) {
        return favouriteFoodRepository.save(FavouriteFoods.builder()
                        .customer(customer)
                        .food(food)
                .build());
    }

//    private Restaurant addRestaurant()
}