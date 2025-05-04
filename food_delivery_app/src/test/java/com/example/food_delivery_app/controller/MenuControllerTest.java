package com.example.food_delivery_app.controller;

import com.example.food_delivery_app.model.Menu;
import com.example.food_delivery_app.service.MenuService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "app.jwt.secret=test-secret-key",
        "app.jwt.expiration=3600000"
})
@AutoConfigureMockMvc
class MenuControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private MenuService menuService;
    @Autowired
    private ObjectMapper objectMapper;
    @Test
    @WithMockUser(roles = "ADMIN")
    void getMenuByRestaurant() throws Exception {
        Menu menu = new Menu();
        when(menuService.getMenuByRestaurant(1L)).thenReturn(menu);

        mockMvc.perform(get("/api/menus/restaurant/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createMenu() throws Exception {
        Menu menu = new Menu();
        Menu savedmenu = new Menu();

        when(menuService.createMenu(anyLong(), any(Menu.class))).thenReturn(savedmenu);

        mockMvc.perform(post("/api/menus/restaurant/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(menu)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateMenu() throws Exception {
        Menu menu = new Menu();
        Menu updatedmenu = new Menu();

        when(menuService.updateMenu(anyLong(), any(Menu.class))).thenReturn(updatedmenu);

        mockMvc.perform(put("/api/menus/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(menu)))
                .andExpect(status().isOk());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteMenu() throws Exception {
        doNothing().when(menuService).deleteMenu(1L);

        mockMvc.perform(delete("/api/menus/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Menu deleted successfully."));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void addFoodToMenu() throws Exception {
        Menu updatedmenu = new Menu();
        when(menuService.addFoodToMenu(1L, 2L)).thenReturn(updatedmenu);

        mockMvc.perform(post("/api/menus/1/foods/2"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void removeFoodFromMenu() throws Exception {
        Menu updatedmenu = new Menu();
        when(menuService.removeFoodFromMenu(1L, 2L)).thenReturn(updatedmenu);

        mockMvc.perform(delete("/api/menus/1/foods/2"))
                .andExpect(status().isOk());
    }
}