package com.example.food_delivery_app.controller;

import com.example.food_delivery_app.model.Address;
import com.example.food_delivery_app.model.USER_ROLE;
import com.example.food_delivery_app.model.User;
import com.example.food_delivery_app.service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import javax.crypto.SecretKey;
import java.util.Date;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "app.jwt.secret=12345678901234567890123456789012",
        "app.jwt.expiration=3600000"
})
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    private User mockUser;

    @BeforeEach
    void setup() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("test@example.com");
        mockUser.setName("Test User");
        mockUser.setPhoneNumber("1234567890");
        mockUser.setRole(USER_ROLE.ROLE_CUSTOMER);

        Address address = new Address();
        address.setStreet("123 Main St");
        address.setCity("Testville");
        mockUser.setAddress(address);
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
    void getUserProfile_withValidToken() throws Exception {
        when(userService.findUserByJwtToken(any())).thenReturn(mockUser);
        String jwtToken = generateTestJwt();

        mockMvc.perform(get("/api/users/profile").header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.address").value("123 Main St, Testville"));
    }

    @Test
    void getUserProfile_withMissingHeader() throws Exception {
        mockMvc.perform(get("/api/users/profile")).andExpect(status().isForbidden());
    }

    @Test
    void getUserProfile_withInvalidToken() throws Exception {
        mockMvc.perform(get("/api/users/profile").header("Authorization", "Bearer invalidtoken"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error", containsString("Invalid JWT token")));
    }
}
