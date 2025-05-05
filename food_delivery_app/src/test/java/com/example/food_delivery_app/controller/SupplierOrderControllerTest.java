package com.example.food_delivery_app.controller;

import com.example.food_delivery_app.model.*;
import com.example.food_delivery_app.repository.OrderRepository;
import com.example.food_delivery_app.repository.RestaurantRepository;
import com.example.food_delivery_app.repository.UserRepository;
import com.example.food_delivery_app.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "app.jwt.secret=test-secret-key",
        "app.jwt.expiration=3600000"
})
@AutoConfigureMockMvc
class SupplierOrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private RestaurantRepository restaurantRepository;

    @MockitoBean
    private OrderRepository orderRepository;

    @MockitoBean
    private UserRepository userRepository;

    private User mockUser;
    private Restaurant mockRestaurant;
    private final String jwtToken = "mock-token";

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

        SecurityContextHolder.getContext().setAuthentication(
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(mockUser, null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_RESTAURANT"))
                )
        );
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getDebugInfo() throws Exception {
        when(restaurantRepository.findByRestaurant(any())).thenReturn(Optional.of(mockRestaurant));

        mockMvc.perform(get("/api/supplier/debug-info")
                .header("Authorization", jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated").value(true));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void healthCheck() throws Exception {
        when(userRepository.count()).thenReturn(1L);
        when(restaurantRepository.count()).thenReturn(1L);
        when(orderRepository.count()).thenReturn(1L);

        mockMvc.perform(get("/api/supplier/health-check")).andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getFullDebugInfo() throws Exception {
        when(restaurantRepository.findByRestaurant(any())).thenReturn(Optional.of(mockRestaurant));

        mockMvc.perform(get("/api/supplier/debug-all")).andExpect(status().isOk())
                .andExpect(jsonPath("$.userInfo.email").value("test@example.com"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testOrdersByStatus() throws Exception {
        when(restaurantRepository.findByRestaurant(any())).thenReturn(Optional.of(mockRestaurant));
        when(orderRepository.findByRestaurantAndOrderStatus(any(), any(), any()))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        mockMvc.perform(get("/api/supplier/test-orders").param("status", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ordersFound").value(0));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getRestaurantOrders() throws Exception {
        when(restaurantRepository.findByRestaurant(any())).thenReturn(Optional.of(mockRestaurant));
        when(orderService.getRestaurantOrders(any(), any())).thenReturn(new PageImpl<>(Collections.emptyList()));

        mockMvc.perform(get("/api/supplier/orders")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getOrderById() throws Exception {
        Order order = new Order();
        order.setId(1L);
        when(restaurantRepository.findByRestaurant(any())).thenReturn(Optional.of(mockRestaurant));
        when(orderService.getRestaurantOrderById(eq(1L), any())).thenReturn(order);

        mockMvc.perform(get("/api/supplier/orders/1")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateOrderStatus() throws Exception {
        Order order = new Order();
        order.setId(1L);
        when(restaurantRepository.findByRestaurant(any())).thenReturn(Optional.of(mockRestaurant));
        when(orderService.getRestaurantOrderById(eq(1L), any())).thenReturn(order);
        when(orderService.updateRestaurantOrderStatus(eq(1L), eq(OrderStatus.PENDING), any())).thenReturn(order);

        mockMvc.perform(put("/api/supplier/orders/1/status").param("status", "PENDING")).andExpect(status().isOk());
    }
}
