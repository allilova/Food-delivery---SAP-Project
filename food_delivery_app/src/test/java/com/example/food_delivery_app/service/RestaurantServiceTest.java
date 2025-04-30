package com.example.food_delivery_app.service;

import com.example.food_delivery_app.dto.RestaurantResponseDto;
import com.example.food_delivery_app.model.Food;
import com.example.food_delivery_app.model.Menu;
import com.example.food_delivery_app.model.Restaurant;
import com.example.food_delivery_app.repository.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private RestaurantService restaurantService;

    private Restaurant restaurant;

    @BeforeEach
    void setup() {
        restaurant = new Restaurant();
        restaurant.setRestaurantID(1L);
        restaurant.setRestaurantName("Neapol");

        Food food = new Food();
        food.setFoodName("Pizza");

        Menu menu = new Menu();
        menu.setFoods(List.of(food));
        restaurant.setMenu(menu);
    }

    @Test
    void TestGetAllRestaurantsReturnsDtoList() {
        when(restaurantRepository.findAll()).thenReturn(List.of(restaurant));

        List<RestaurantResponseDto> result = restaurantService.getAllRestaurants();

        assertEquals(1, result.size());
        assertEquals("Neapol", result.get(0).getName());
        assertEquals("Pizza", result.get(0).getMenu().get(0));
    }

    @Test
    void getRestaurantByIdFound() {
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));

        Optional<RestaurantResponseDto> result = restaurantService.getRestaurantById(1L);

        assertTrue(result.isPresent());
        assertEquals("Neapol", result.get().getName());

    }

    @Test
    void getRestaurantByIdNotFound() {
        when(restaurantRepository.findById(2L)).thenReturn(Optional.empty());

        Optional<RestaurantResponseDto> result = restaurantService.getRestaurantById(2L);

        assertFalse(result.isPresent());

    }

    @Test
    void searchRestaurants() {
        when(restaurantRepository.findBySearchQuery("neapol")).thenReturn(List.of(restaurant));

        List<RestaurantResponseDto> result = restaurantService.searchRestaurants("neapol");

        assertEquals(1, result.size());
        assertEquals("Neapol", result.get(0).getName());
    }
}