package com.example.food_delivery_app.controller;

import com.example.food_delivery_app.model.Menu;
import com.example.food_delivery_app.model.USER_ROLE;
import com.example.food_delivery_app.model.User;
import com.example.food_delivery_app.service.FoodService;
import com.example.food_delivery_app.service.MenuService;
import com.example.food_delivery_app.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {"app.jwt.secret=test-secret-key","app.jwt.expiration=3600000"})
@AutoConfigureMockMvc
class FoodControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private FoodService foodService;
    @MockitoBean
    private UserService userService;
    @MockitoBean
    private MenuService menuService;
    @Autowired
    private ObjectMapper objectMapper;

    private final String jwtToken = "mock-token";
    private User mockUser() {
        User user = new User();
        return user;
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void searchFoodSuccess() throws Exception {
        when(userService.findUserByJwtToken(jwtToken)).thenReturn(mockUser());
        when(foodService.searchFood("pizza")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/food/search")
                .param("foodName","pizza")
                .header("Authorization",jwtToken))
                .andExpect(status().isOk());

    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void GetMenuSuccess() throws Exception {
        Menu menu = new Menu();

        when(userService.findUserByJwtToken(jwtToken)).thenReturn(mockUser());
        when(menuService.getMenuById(anyLong())).thenReturn(menu);
        when(foodService.getMenuFood(eq(menu),any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/food/menu/1")
                .header("Authorization", jwtToken))
                .andExpect(status().isOk());

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getMenuFoodFails() throws Exception {
        when(userService.findUserByJwtToken(jwtToken)).thenThrow(new RuntimeException("Invalid token"));

        mockMvc.perform(get("/api/food/menu/1")
                .header("Authorization", jwtToken))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void SearchFoodFails() throws Exception {
        when(userService.findUserByJwtToken(jwtToken)).thenThrow(new RuntimeException("Invalid token"));

        mockMvc.perform(get("/api/food/menu")
                .param("foodName", "pasta")
                .header("Authorization", jwtToken))
                .andExpect(status().isInternalServerError());
    }
}