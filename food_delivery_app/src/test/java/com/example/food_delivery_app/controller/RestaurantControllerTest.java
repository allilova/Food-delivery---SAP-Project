package com.example.food_delivery_app.controller;

import com.example.food_delivery_app.dto.RestaurantDto;
import com.example.food_delivery_app.dto.RestaurantResponseDto;
import com.example.food_delivery_app.model.Restaurant;
import com.example.food_delivery_app.model.User;
import com.example.food_delivery_app.service.RestaurantService;
import com.example.food_delivery_app.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "app.jwt.secret=test-secret-key",
        "app.jwt.expiration=3600000"
})
@AutoConfigureMockMvc
class RestaurantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RestaurantService restaurantService;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private final String jwtToken = "mock-token";

    private User mockUser() {
        User user = new User();
        user.setEmail("user@example.com");
        return user;
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllRestaurants() throws Exception {
        when(restaurantService.getAllRestaurantsDto()).thenReturn(List.of(new RestaurantResponseDto()));

        mockMvc.perform(get("/api/restaurants")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void findRestaurantById() throws Exception {
        Restaurant restaurant = new Restaurant();
        RestaurantResponseDto dto = new RestaurantResponseDto();

        when(restaurantService.findById(1L)).thenReturn(restaurant);
        when(restaurantService.convertToDto(restaurant)).thenReturn(dto);

        mockMvc.perform(get("/api/restaurants/1")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void searchRestaurants() throws Exception {
        when(restaurantService.searchRestaurantsDto("pizza")).thenReturn(List.of(new RestaurantResponseDto()));

        mockMvc.perform(get("/api/restaurants/search").param("keyword", "pizza")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void addToFavourites() throws Exception {
        User user = mockUser();
        RestaurantDto dto = new RestaurantDto();

        when(userService.findUserByJwtToken("mock-token")).thenReturn(user);
        when(restaurantService.addToFavourites(1L, user)).thenReturn(dto);

        mockMvc.perform(put("/api/restaurants/1/add-favourites").header("Authorization", jwtToken))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void findRestaurantByIdFail() throws Exception {
        when(restaurantService.findById(1L)).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(get("/api/restaurants/1")).andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void searchRestaurantsFail() throws Exception {
        when(restaurantService.searchRestaurantsDto("anything")).thenThrow(new RuntimeException("Search error"));

        mockMvc.perform(get("/api/restaurants/search").param("keyword", "anything"))
                .andExpect(status().isInternalServerError());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void InternalServerError() throws Exception {
        when(restaurantService.getAllRestaurantsDto()).thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/api/restaurants"))
                .andExpect(status().isInternalServerError());
    }

}