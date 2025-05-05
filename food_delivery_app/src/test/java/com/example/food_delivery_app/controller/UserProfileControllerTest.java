package com.example.food_delivery_app.controller;

import com.example.food_delivery_app.model.Address;
import com.example.food_delivery_app.model.USER_ROLE;
import com.example.food_delivery_app.model.User;
import com.example.food_delivery_app.request.CreateAddressRequest;
import com.example.food_delivery_app.request.CreateRestaurantRequest;
import com.example.food_delivery_app.service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import javax.crypto.SecretKey;
import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "app.jwt.secret=12345678901234567890123456789012",
        "app.jwt.expiration=3600000"
})
@AutoConfigureMockMvc
class UserProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    private User mockUser;
    private String jwtToken;

    @BeforeEach
    void setup() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("test@example.com");
        mockUser.setName("Test User");
        mockUser.setPhoneNumber("1234567890");
        mockUser.setRole(USER_ROLE.ROLE_CUSTOMER);

        Address address = new Address();
        address.setStreet("Main St");
        address.setCity("TestCity");
        mockUser.setAddress(address);

        jwtToken = generateTestJwt();
    }

    private String generateTestJwt() {
        String secret = "12345678901234567890123456789012";
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());

        return Jwts.builder()
                .setSubject("test@example.com")
                .claim("email", "test@example.com")
                .claim("authorities", "[ROLE_CUSTOMER]")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    @Test
    void getUserProfile() throws Exception {
        when(userService.findUserByJwtToken(any())).thenReturn(mockUser);

        mockMvc.perform(get("/api/user/profile").header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.name").value("Test User"));
    }

    @Test
    void updateUserProfile() throws Exception {
        CreateAddressRequest.UpdateProfileRequest updateRequest = new CreateAddressRequest.UpdateProfileRequest();
        updateRequest.setFirstName("Updated Name");
        updateRequest.setPhoneNumber("9876543210");

        when(userService.findUserByJwtToken(any())).thenReturn(mockUser);
        when(userService.updateUserProfile(any(), any())).thenReturn(mockUser);

        String requestJson = "{\"name\":\"Updated Name\",\"phoneNumber\":\"9876543210\"}";


        mockMvc.perform(put("/api/user/profile")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void updatePassword() throws Exception {
        CreateRestaurantRequest.UpdatePasswordRequest passwordRequest = new CreateRestaurantRequest.UpdatePasswordRequest();
        passwordRequest.setCurrentPassword("oldpass");
        passwordRequest.setNewPassword("newpass");

        when(userService.findUserByJwtToken(any())).thenReturn(mockUser);
        doNothing().when(userService).updatePassword(any(), any());

        String requestJson = "{\"name\":\"Updated Name\",\"phoneNumber\":\"9876543210\"}";


        mockMvc.perform(put("/api/user/profile/password")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk());
    }

    @Test
    void deleteUserProfile() throws Exception {
        when(userService.findUserByJwtToken(any())).thenReturn(mockUser);
        doNothing().when(userService).deleteUser(any());

        mockMvc.perform(delete("/api/user/profile").header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNoContent());
    }
}
