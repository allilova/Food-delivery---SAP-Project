package com.example.food_delivery_app.config;

import com.example.food_delivery_app.model.Address;
import com.example.food_delivery_app.model.ContactInfo;
import com.example.food_delivery_app.model.Restaurant;
import com.example.food_delivery_app.model.USER_ROLE;
import com.example.food_delivery_app.model.User;
import com.example.food_delivery_app.repository.AddressRepository;
import com.example.food_delivery_app.repository.RestaurantRepository;
import com.example.food_delivery_app.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;

/**
 * Initializes the database with sample data on application startup
 */
@Configuration
public class DatabaseInitializer {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AddressRepository addressRepository;
    
    @Autowired
    private RestaurantRepository restaurantRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * CommandLineRunner bean to initialize database on startup
     * Only runs when database is empty
     */
    @Bean
    public CommandLineRunner initDatabase() {
        return args -> {
            // Only initialize if no users exist
            if (userRepository.count() == 0) {
                logger.info("Initializing database with sample data");
                initializeUsers();
                initializeRestaurants();
            } else {
                logger.info("Database already contains data, skipping initialization");
            }
        };
    }

    /**
     * Initialize sample users
     */
    private void initializeUsers() {
        // Create admin user
        User adminUser = new User();
        adminUser.setName("Admin User");
        adminUser.setEmail("admin@example.com");
        adminUser.setPassword(passwordEncoder.encode("password"));
        adminUser.setPhoneNumber("1234567890");
        adminUser.setAddress("123 Admin St");
        adminUser.setRole(USER_ROLE.ROLE_ADMIN);
        userRepository.save(adminUser);
        
        // Create restaurant owner
        User restaurantUser = new User();
        restaurantUser.setName("Restaurant Owner");
        restaurantUser.setEmail("restaurant@example.com");
        restaurantUser.setPassword(passwordEncoder.encode("password"));
        restaurantUser.setPhoneNumber("9876543210");
        restaurantUser.setAddress("456 Restaurant Ave");
        restaurantUser.setRole(USER_ROLE.ROLE_RESTAURANT);
        userRepository.save(restaurantUser);
        
        // Create customer
        User customerUser = new User();
        customerUser.setName("Customer User");
        customerUser.setEmail("customer@example.com");
        customerUser.setPassword(passwordEncoder.encode("password"));
        customerUser.setPhoneNumber("5555555555");
        customerUser.setAddress("789 Customer Blvd");
        customerUser.setRole(USER_ROLE.ROLE_CUSTOMER);
        userRepository.save(customerUser);
        
        logger.info("Sample users created successfully");
    }
    
    /**
     * Initialize sample restaurants
     */
    private void initializeRestaurants() {
        // Find restaurant owner
        User restaurantOwner = userRepository.findByEmail("restaurant@example.com");
        if (restaurantOwner == null) {
            logger.warn("Restaurant owner not found, skipping restaurant initialization");
            return;
        }
        
        // Create sample restaurants
        createSampleRestaurant(
            restaurantOwner,
            "Gourmet Restaurant",
            "Italian",
            "123 Main St, Sofia",
            "restaurant@gourmet.com",
            "+359 2 123 4567",
            "9:00",
            "22:00",
            Arrays.asList("https://example.com/gourmet1.jpg", "https://example.com/gourmet2.jpg")
        );
        
        createSampleRestaurant(
            restaurantOwner,
            "Asian Fusion",
            "Asian",
            "456 Park Ave, Sofia",
            "restaurant@asianfusion.com",
            "+359 2 987 6543",
            "10:00",
            "23:00",
            Arrays.asList("https://example.com/asian1.jpg", "https://example.com/asian2.jpg")
        );
        
        createSampleRestaurant(
            restaurantOwner,
            "Burger Palace",
            "Fast Food",
            "789 Burger St, Sofia",
            "info@burgerpalace.com",
            "+359 2 555 5555",
            "8:00",
            "0:00",
            Arrays.asList("https://example.com/burger1.jpg", "https://example.com/burger2.jpg")
        );
        
        logger.info("Sample restaurants created successfully");
    }
    
    /**
     * Helper method to create a restaurant
     */
    private Restaurant createSampleRestaurant(
            User owner,
            String name,
            String type,
            String addressStr,
            String email,
            String phone,
            String openingHours,
            String closingHours,
            List<String> images) {
        
        // Create address
        Address address = new Address();
        address.setStreet(addressStr.split(",")[0].trim());
        address.setCity(addressStr.split(",")[1].trim());
        addressRepository.save(address);
        
        // Create contact info
        ContactInfo contactInfo = new ContactInfo();
        contactInfo.setEmail(email);
        contactInfo.setPhone(phone);
        
        // Create restaurant
        Restaurant restaurant = new Restaurant();
        restaurant.setRestaurantName(name);
        restaurant.setType(type);
        restaurant.setRestaurantAddress(address);
        restaurant.setContactInfo(contactInfo);
        restaurant.setOpeningHours(openingHours);
        restaurant.setClosingHours(closingHours);
        restaurant.setImages(images);
        restaurant.setOpen(true);
        restaurant.setRestaurant(owner);
        
        return restaurantRepository.save(restaurant);
    }
}