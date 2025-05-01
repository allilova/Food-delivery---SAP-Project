package com.example.food_delivery_app.service;

import com.example.food_delivery_app.dto.RestaurantDto;
import com.example.food_delivery_app.dto.RestaurantResponseDto;
import com.example.food_delivery_app.model.Restaurant;
import com.example.food_delivery_app.model.User;
import com.example.food_delivery_app.request.CreateRestaurantRequest;

import java.util.List;

/**
 * Service interface for restaurant operations
 */
public interface RestaurantService {
    /**
     * Creates a new restaurant
     * @param req Restaurant creation request
     * @param user Owner of the restaurant
     * @return Created restaurant
     */
    Restaurant createRestaurant(CreateRestaurantRequest req, User user);

    /**
     * Updates an existing restaurant
     * @param restaurantId ID of the restaurant to update
     * @param updatedRestaurant Updated restaurant data
     * @return Updated restaurant
     * @throws Exception if restaurant not found
     */
    Restaurant updateRestaurant(Long restaurantId, CreateRestaurantRequest updatedRestaurant) throws Exception;

    /**
     * Deletes a restaurant
     * @param restaurantId ID of the restaurant to delete
     * @throws Exception if restaurant not found
     */
    void deleteRestaurant(Long restaurantId) throws Exception;

    /**
     * Retrieves all restaurants
     * @return List of all restaurants
     */
    List<Restaurant> getAllRestaurants();

    /**
     * Retrieves all restaurants as DTOs
     * @return List of restaurant DTOs
     */
    List<RestaurantResponseDto> getAllRestaurantsDto();

    /**
     * Searches for restaurants by keyword
     * @param keyword Search term
     * @return List of matching restaurants
     */
    List<Restaurant> searchRestaurants(String keyword);

    /**
     * Finds a restaurant by ID
     * @param id Restaurant ID
     * @return Restaurant
     * @throws Exception if restaurant not found
     */
    Restaurant findById(Long id) throws Exception;

    /**
     * Toggles a restaurant in a user's favorites list
     * @param restaurantId Restaurant ID
     * @param user User
     * @return True if added to favorites, false if removed
     * @throws Exception if restaurant not found
     */
    boolean toggleFavorite(Long restaurantId, User user) throws Exception;

    /**
     * Updates a restaurant's open/closed status
     * @param restaurantId Restaurant ID
     * @return Updated restaurant
     * @throws Exception if restaurant not found
     */
    Restaurant updateRestaurantStatus(Long restaurantId) throws Exception;

    /**
     * Converts a Restaurant entity to a DTO
     * @param restaurant Restaurant entity
     * @return Restaurant DTO
     */
    RestaurantResponseDto convertToDto(Restaurant restaurant);

    /**
     * Searches for restaurants and returns them as DTOs
     * @param keyword Search term
     * @return List of matching restaurant DTOs
     */
    List<RestaurantResponseDto> searchRestaurantsDto(String keyword);
    
    /**
     * Gets a user's favorite restaurants as DTOs
     * @param user User whose favorites to retrieve
     * @return List of favorite restaurant DTOs
     */
    List<RestaurantResponseDto> getUserFavorites(User user);
}