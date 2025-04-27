package com.example.food_delivery_app.service;

import com.example.food_delivery_app.dto.RestaurantResponseDto;
import com.example.food_delivery_app.model.Restaurant;
import com.example.food_delivery_app.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RestaurantService {
    
    @Autowired
    private RestaurantRepository restaurantRepository;
    
    public List<RestaurantResponseDto> getAllRestaurants() {
        List<Restaurant> restaurants = restaurantRepository.findAll();
        return restaurants.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public Optional<RestaurantResponseDto> getRestaurantById(Long id) {
        return restaurantRepository.findById(id)
                .map(this::convertToDto);
    }
    
    public List<RestaurantResponseDto> searchRestaurants(String query) {
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
                    .map(food -> food.getFoodName())
                    .collect(Collectors.toList());
        }
        dto.setMenu(menuItems);
        
        return dto;
    }
}