package com.example.food_delivery_app.controller;

import com.example.food_delivery_app.model.Order;
import com.example.food_delivery_app.model.User;
import com.example.food_delivery_app.request.CreateOrderRequest;
import com.example.food_delivery_app.service.OrderService;
import com.example.food_delivery_app.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "app.jwt.secret=test-secret-key",
        "app.jwt.expiration=3600000"
})
@AutoConfigureMockMvc
class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private OrderService orderService;
    @MockitoBean
    private UserService userService;
    @Autowired
    private ObjectMapper objectMapper;

    private final String jwtToken = "mock-token";

    private User mockUser() {
        User user = new User();
        return user;
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createOrder() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest();
        Order order = new Order();

        when(userService.findUserByJwtToken("mock-token")).thenReturn(mockUser());
        when(orderService.createOrder(any(), any())).thenReturn(order);

        mockMvc.perform(post("/api/orders")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserOrders() throws Exception {
        User user = mockUser();
        Order order = new Order();

        when(userService.findUserByJwtToken("mock-token")).thenReturn(user);
        when(orderService.getUserOrders(eq(user), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(order)));

        mockMvc.perform(get("/api/orders/user").header("Authorization", jwtToken)).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getOrderById() throws Exception {
        User user = mockUser();
        Order order = new Order();

        when(userService.findUserByJwtToken("mock-token")).thenReturn(user);
        when(orderService.getOrderById(1L, user)).thenReturn(order);

        mockMvc.perform(get("/api/orders/1").header("Authorization", jwtToken)).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateOrderStatus() throws Exception {
        User user = mockUser();
        Order order = new Order();

        when(userService.findUserByJwtToken("mock-token")).thenReturn(user);
        when(orderService.updateOrderStatus(1L, "SHIPPED", user)).thenReturn(order);

        mockMvc.perform(put("/api/orders/1/status")
                .param("status", "SHIPPED")
                .header("Authorization", jwtToken))
                .andExpect(status().isOk());

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createOrderFails() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest();

        when(userService.findUserByJwtToken("mock-token")).thenThrow(new RuntimeException("Something went wrong"));

        mockMvc.perform(post("/api/orders")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getOrderByIdFails() throws Exception {
        User user = mockUser();

        when(userService.findUserByJwtToken("mock-token")).thenReturn(user);
        when(orderService.getOrderById(1L, user)).thenThrow(new RuntimeException("Order not found"));

        mockMvc.perform(get("/api/orders/1").header("Authorization", jwtToken)).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateOrderStatusFails() throws Exception {
        User user = mockUser();

        when(userService.findUserByJwtToken("mock-token")).thenReturn(user);
        when(orderService.updateOrderStatus(1L, "INVALID_STATUS", user)).thenThrow(new RuntimeException("Invalid status"));

        mockMvc.perform(put("/api/orders/1/status")
                .param("status", "INVALID_STATUS")
                .header("Authorization", jwtToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserOrdersFails() throws Exception {
        when(userService.findUserByJwtToken("mock-token")).thenThrow(new RuntimeException("User not found"));

        mockMvc.perform(get("/api/orders/user").header("Authorization", jwtToken)).andExpect(status().isUnauthorized());
    }
}