package com.example.food_delivery_app.controller;

import com.example.food_delivery_app.dto.RestaurantResponseDto;
import com.example.food_delivery_app.model.Restaurant;
import com.example.food_delivery_app.model.USER_ROLE;
import com.example.food_delivery_app.model.User;
import com.example.food_delivery_app.request.CreateRestaurantRequest;
import com.example.food_delivery_app.service.RestaurantService;
import com.example.food_delivery_app.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "app.jwt.secret=test-secret-key",
        "app.jwt.expiration=3600000"
})
@AutoConfigureMockMvc
class AdminRestaurantControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private RestaurantService restaurantService;
    @MockitoBean
    private UserService userService;
    @Autowired
    private ObjectMapper objectMapper;

    private final String jwtToken = "mock-token";

    private User adminUSer() {
        User user = new User();
        user.setRole(USER_ROLE.ROLE_ADMIN);
        return  user;
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createRestaurant() throws Exception {
        CreateRestaurantRequest request = new CreateRestaurantRequest();
        Restaurant created = new Restaurant();
        RestaurantResponseDto dto = new RestaurantResponseDto();

        when(userService.findUserByJwtToken(jwtToken)).thenReturn(adminUSer());
        when(restaurantService.createRestaurant(any(),any())).thenReturn(created);
        when(restaurantService.convertToDto(created)).thenReturn(dto);

        mockMvc.perform(post("/api/admin/restaurants")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateRestaurant() throws Exception {
        CreateRestaurantRequest request = new CreateRestaurantRequest();
        Restaurant updated = new Restaurant();

        when(userService.findUserByJwtToken(jwtToken)).thenReturn(adminUSer());
        when(restaurantService.updateRestaurant(eq(1L), any())).thenReturn(updated);

        mockMvc.perform(put("/api/admin/restaurants/1")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteRestaurant() throws Exception{
        when(userService.findUserByJwtToken(jwtToken)).thenReturn(adminUSer());
        doNothing().when(restaurantService).deleteRestaurant(1L);

        mockMvc.perform(delete("/api/admin/restaurants/1")
                .header("Authorization", jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Restaurant deleted"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateRestaurantStatus() throws Exception {
        Restaurant updated = new Restaurant();
        RestaurantResponseDto dto = new RestaurantResponseDto();

        when(userService.findUserByJwtToken(jwtToken)).thenReturn(adminUSer());
        when(restaurantService.updateRestaurantStatus(1L)).thenReturn(updated);
        when(restaurantService.convertToDto(updated)).thenReturn(dto);

        mockMvc.perform(put("/api/admin/restaurants/1/status")
                .header("Authorization", jwtToken))
                .andExpect(status().isOk());

    }
}