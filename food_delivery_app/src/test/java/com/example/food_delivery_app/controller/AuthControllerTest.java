package com.example.food_delivery_app.controller;

import com.example.food_delivery_app.request.LoginRequest;
import com.example.food_delivery_app.request.RegisterRequest;
import com.example.food_delivery_app.response.AuthResponse;
import com.example.food_delivery_app.service.AuthenticationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(properties = {
        "app.jwt.secret=test-secret-key",
        "app.jwt.expiration=3600000"
})
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private AuthenticationService authenticationService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    void registerUserSuccessful() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("NeshtoEmail");
        request.setPassword("NEZNAM");
        request.setName("ZABRAVIH");

        AuthResponse response = new AuthResponse();
        response.setMessage("Registration successful");

        when(authenticationService.register(any(RegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Registration successful"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void loginUserSuccessful() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("ZABRAWIH");
        request.setPassword("MNOOGOQKOOO");

        AuthResponse response = new AuthResponse();
        response.setMessage("bravo lud vlezna");

        when(authenticationService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("bravo lud vlezna"));
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void LoginUserUnsuccessful() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("SPIONIRO");
        request.setPassword("GOLUBIRO");

        when(authenticationService.login(any(LoginRequest.class)))
                .thenThrow(new IllegalArgumentException("Invalid credentials"));

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid credentials"));
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void RegisterUserUnsuccessful() throws Exception {
        RegisterRequest request = new RegisterRequest();

        when(authenticationService.register(any(RegisterRequest.class))).thenThrow(new IllegalArgumentException("Invalid registration data"));


        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid registration data: Invalid registration data"));

    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void registerUserFaild() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("fail@example.com");

        when(authenticationService.register(any(RegisterRequest.class))).thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Registration failed. Please try again later."));

    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void loginUserFaild() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("fail@example.com");
        request.setPassword("secret");

        when(authenticationService.login(any(LoginRequest.class))).thenThrow(new RuntimeException("Unexpected failure"));

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Login failed. Please try again later."));
    }


}