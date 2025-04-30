package com.example.food_delivery_app.service;

import com.example.food_delivery_app.dto.RestaurantDto;
import com.example.food_delivery_app.dto.RestaurantResponseDto;
import com.example.food_delivery_app.model.Restaurant;
import com.example.food_delivery_app.model.User;
import com.example.food_delivery_app.request.CreateRestaurantRequest;

import java.util.List;

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
