package com.example.food_delivery_app.service;

import com.example.food_delivery_app.dto.RestaurantDto;
import com.example.food_delivery_app.dto.RestaurantResponseDto;
import com.example.food_delivery_app.model.Menu;
import com.example.food_delivery_app.model.Restaurant;
<<<<<<< HEAD
import com.example.food_delivery_app.repository.RestaurantRepository;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
=======
import com.example.food_delivery_app.model.User;
import com.example.food_delivery_app.request.CreateRestaurantRequest;
>>>>>>> 3b97e188d54bd0a20c3391ce1ad1a3d3dc0fb7ca

import java.util.List;

<<<<<<< HEAD
@Service
public class RestaurantService {
    private static final Logger logger = LoggerFactory.getLogger(RestaurantService.class);
    private final RestaurantRepository restaurantRepository;
    
    public RestaurantService(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }
    
    public List<RestaurantResponseDto> getAllRestaurants() {
        logger.info("Fetching all restaurants");
        List<Restaurant> restaurants = restaurantRepository.findAll();
        return restaurants.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public Optional<RestaurantResponseDto> getRestaurantById(Long id) {
        logger.info("Fetching restaurant by id: {}", id);
        return restaurantRepository.findById(id)
                .map(this::convertToDto);
    }
    
    public Optional<Menu> getMenuById(Long id) {
        logger.info("Fetching menu by id: {}", id);
        return restaurantRepository.findById(id)
                .map(Restaurant::getMenu);
    }
    
    public List<RestaurantResponseDto> searchRestaurants(String query) {
        logger.info("Searching restaurants with query: {}", query);
        List<Restaurant> restaurants = restaurantRepository.findBySearchQuery(query);
        return restaurants.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    private RestaurantResponseDto convertToDto(Restaurant restaurant) {
        RestaurantResponseDto dto = new RestaurantResponseDto();
        dto.setRestaurantId(restaurant.getRestaurantID().toString());
        dto.setName(restaurant.getRestaurantName());
        // Use the first image from the images list or a default
        dto.setImgUrl(restaurant.getImages() != null && !restaurant.getImages().isEmpty() 
                ? restaurant.getImages().get(0) 
                : "default-restaurant.jpg");
        dto.setAddress(restaurant.getRestaurantAddress() != null 
                ? restaurant.getRestaurantAddress().toString() 
                : "");
        // Since rating isn't directly in the model, we could calculate it or use a default
        dto.setRating(4.5); // Default or could be calculated from ratings
        dto.setFoodType(restaurant.getType());
        // Calculate or set a default delivery time
        dto.setTimeDelivery("30-45 min");
        
        // Extract menu items if available
        List<String> menuItems = new ArrayList<>();
        if (restaurant.getMenu() != null && restaurant.getMenu().getFoods() != null) {
            menuItems = restaurant.getMenu().getFoods().stream()
                    .map(food -> food.getName())
                    .collect(Collectors.toList());
        }
        dto.setMenu(menuItems);
        
        return dto;
    }
}
=======
public interface RestaurantService {
    public Restaurant createRestaurant(CreateRestaurantRequest req, User user);

    public Restaurant updateRestaurant(Long restaurantId,CreateRestaurantRequest updatesRestaurant)throws Exception;

    public void deleteRestaurant(Long restaurantId)throws Exception;

    public List<Restaurant> getAllRestaurants();

    public List<RestaurantResponseDto> getAllRestaurantsDto();

    public List<Restaurant> searchRestaurants(String keyword);

    public Restaurant findById (Long id)throws Exception;

    public RestaurantDto addToFavourites(Long restaurantId, User user)throws Exception;

    public Restaurant updateRestaurantStatus(Long restaurantId)throws Exception;

    public RestaurantResponseDto convertToDto(Restaurant restaurant);

    public List<RestaurantResponseDto> searchRestaurantsDto(String keyword);

}
>>>>>>> 3b97e188d54bd0a20c3391ce1ad1a3d3dc0fb7ca
