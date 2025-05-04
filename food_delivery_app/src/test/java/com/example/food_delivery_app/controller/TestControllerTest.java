package com.example.food_delivery_app.controller;

import com.example.food_delivery_app.model.Restaurant;
import com.example.food_delivery_app.model.USER_ROLE;
import com.example.food_delivery_app.model.User;
import com.example.food_delivery_app.repository.RestaurantRepository;
import com.example.food_delivery_app.service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "app.jwt.secret=12345678901234567890123456789012",
        "app.jwt.expiration=3600000"
})
@AutoConfigureMockMvc
@Import(TestControllerTest.TestSecurityConfig.class)
class TestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private RestaurantRepository restaurantRepository;

    private User mockUser;
    private Restaurant mockRestaurant;

    @BeforeEach
    void setup() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("test@example.com");
        mockUser.setName("Test User");
        mockUser.setRole(USER_ROLE.ROLE_RESTAURANT);

        mockRestaurant = new Restaurant();
        mockRestaurant.setId(1L);
        mockRestaurant.setRestaurantName("Testaurant");
        mockRestaurant.setRestaurant(mockUser);
        mockRestaurant.setType("General");
        mockRestaurant.setOpen(true);
    }

    private String generateTestJwt() {
        String secret = "12345678901234567890123456789012";
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());

        return Jwts.builder()
                .setSubject("test@example.com")
                .claim("email", "test@example.com")
                .claim("authorities", "[ROLE_RESTAURANT]")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    @Test
    void testApiIsWorking() throws Exception {
        mockMvc.perform(get("/api/test"))
                .andExpect(status().isOk())
                .andExpect(content().string("API is Working"));
    }

    @Test
    void debugAuth_withValidAuthHeader() throws Exception {
        when(userService.findUserByJwtToken(any())).thenReturn(mockUser);
        when(restaurantRepository.findByRestaurant(any())).thenReturn(Optional.of(mockRestaurant));

        String jwtToken = generateTestJwt();

        mockMvc.perform(get("/api/auth/debug").header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.auth.isAuthenticated").value(true))
                .andExpect(jsonPath("$.token.user.email").value("test@example.com"));
    }

    @Test
    void debugAuth_withoutHeader() throws Exception {
        mockMvc.perform(get("/api/auth/debug"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenPresent").value(false));
    }

    @Test
    void debugAuthToken() throws Exception {
        when(userService.findUserByJwtToken(any())).thenReturn(mockUser);
        when(restaurantRepository.findByRestaurant(any())).thenReturn(Optional.of(mockRestaurant));

        String jwtToken = generateTestJwt();

        mockMvc.perform(get("/api/auth/debug-token").header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.hasRestaurant").value(true));
    }

    @Test
    void getUserRole() throws Exception {
        when(userService.findUserByJwtToken(any())).thenReturn(mockUser);
        String jwtToken = generateTestJwt();

        mockMvc.perform(get("/api/user/role").header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.isRestaurant").value(true));
    }

    @TestConfiguration
    static class TestSecurityConfig {
        @Bean
        public Filter testAuthFilter() {
            return new OncePerRequestFilter() {
                @Override
                protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                        throws ServletException, IOException {

                    List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_RESTAURANT");

                    User mockUser = new User();
                    mockUser.setId(1L);
                    mockUser.setEmail("test@example.com");
                    mockUser.setName("Test User");
                    mockUser.setRole(USER_ROLE.ROLE_RESTAURANT);

                    Authentication auth = new UsernamePasswordAuthenticationToken(mockUser, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    filterChain.doFilter(request, response);
                }
            };
        }
    }
}
