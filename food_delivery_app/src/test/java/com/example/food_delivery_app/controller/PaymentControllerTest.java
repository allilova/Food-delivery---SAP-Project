package com.example.food_delivery_app.controller;

import com.example.food_delivery_app.model.Payment;
import com.example.food_delivery_app.model.User;
import com.example.food_delivery_app.request.PaymentRequest;
import com.example.food_delivery_app.service.PaymentService;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "app.jwt.secret=test-secret-key",
        "app.jwt.expiration=3600000"
})
@AutoConfigureMockMvc
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentService paymentService;

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
    void processPayment() throws Exception {
        PaymentRequest request = new PaymentRequest();
        Payment payment = new Payment();

        when(userService.findUserByJwtToken(any())).thenReturn(mockUser());
        when(paymentService.processPayment(any(), any())).thenReturn(payment);

        mockMvc.perform(post("/api/payments")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getPaymentById() throws Exception {
        Payment payment = new Payment();

        when(userService.findUserByJwtToken(any())).thenReturn(mockUser());
        when(paymentService.getPaymentById(1L, mockUser())).thenReturn(payment);

        mockMvc.perform(get("/api/payments/1").header("Authorization", jwtToken)).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getPaymentByOrderId() throws Exception {
        Payment payment = new Payment();

        when(userService.findUserByJwtToken(any())).thenReturn(mockUser());
        when(paymentService.getPaymentByOrderId(1L, mockUser())).thenReturn(payment);

        mockMvc.perform(get("/api/payments/order/1").header("Authorization", jwtToken)).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void refundPayment() throws Exception {
        Payment payment = new Payment();

        when(userService.findUserByJwtToken(any())).thenReturn(mockUser());
        when(paymentService.refundPayment(1L, mockUser())).thenReturn(payment);

        mockMvc.perform(post("/api/payments/refund/1").header("Authorization", jwtToken)).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void processPaymentFail() throws Exception {
        PaymentRequest request = new PaymentRequest();

        when(userService.findUserByJwtToken(any())).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(post("/api/payments")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getPaymentByIdFail() throws Exception {
        when(userService.findUserByJwtToken(any())).thenReturn(mockUser());
        when(paymentService.getPaymentById(1L, mockUser())).thenThrow(new RuntimeException("Not found"));

        mockMvc.perform(get("/api/payments/1").header("Authorization", jwtToken)).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getPaymentByOrderIdFail() throws Exception {
        when(userService.findUserByJwtToken(any())).thenReturn(mockUser());
        when(paymentService.getPaymentByOrderId(1L, mockUser())).thenThrow(new RuntimeException("Not found"));

        mockMvc.perform(get("/api/payments/order/1").header("Authorization", jwtToken)).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void refundPaymentFail() throws Exception {
        when(userService.findUserByJwtToken(any())).thenReturn(mockUser());
        when(paymentService.refundPayment(1L, mockUser())).thenThrow(new RuntimeException("Failed"));

        mockMvc.perform(post("/api/payments/refund/1").header("Authorization", jwtToken)).andExpect(status().isBadRequest());
    }
}