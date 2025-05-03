package com.example.food_delivery_app.controller;

import com.example.food_delivery_app.model.Cart;
import com.example.food_delivery_app.model.User;
import com.example.food_delivery_app.request.CartItemRequest;
import com.example.food_delivery_app.service.CartService;
import com.example.food_delivery_app.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.h2.command.dml.MergeUsing;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "app.jwt.secret=test-secret-key",
        "app.jwt.expiration=3600000"
})
@AutoConfigureMockMvc
class CartControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private CartService cartService;
    @MockitoBean
    private UserService userService;
    @Autowired
    private ObjectMapper objectMapper;

    private final String jwtToken = "mock-token";

    private User mockUser() {
        User user = new User();
        user.setEmail("7brirovki");
        return user;
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getCart_shouldReturnCart() throws Exception {
        User user = mockUser();
        Cart cart = new Cart();

        when(userService.findUserByJwtToken(jwtToken)).thenReturn(user);
        when(cartService.getCart(user)).thenReturn(cart);

        mockMvc.perform(get("/api/cart")
                .header("Authorization", jwtToken))
                .andExpect(status().isOk());

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void addItemToCart() throws Exception {
        User user = mockUser();
        Cart cart = new Cart();
        CartItemRequest request = new CartItemRequest();

        when(userService.findUserByJwtToken(jwtToken)).thenReturn(user);
        when(cartService.addItemToCart(any(), any())).thenReturn(cart);

        mockMvc.perform(post("/api/cart/items")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void updateCartItem() throws Exception {
        User user = mockUser();
        Cart cart = new Cart();
        CartItemRequest request = new CartItemRequest();

        when(userService.findUserByJwtToken(jwtToken)).thenReturn(user);
        when(cartService.updateCartItem(any(), any(), any())).thenReturn(cart);

        mockMvc.perform(put("/api/cart/items/1")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void removeItemFromCart() throws Exception {
        User user = mockUser();
        Cart cart = new Cart();

        when(userService.findUserByJwtToken(jwtToken)).thenReturn(user);
        when(cartService.removeItemFromCart(any(), any())).thenReturn(cart);

        mockMvc.perform(delete("/api/cart/items/1")
                .header("Authorization", jwtToken))
                .andExpect(status().isOk());


    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void clearCart()throws Exception {
        User user = mockUser();

        when(userService.findUserByJwtToken(jwtToken)).thenReturn(user);

        mockMvc.perform(delete("/api/cart")
                .header("Authorization", jwtToken))
                .andExpect(status().isNoContent());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void getCartError() throws Exception {
        when(userService.findUserByJwtToken(jwtToken)).thenThrow(new RuntimeException("User not found"));

        mockMvc.perform(get("/api/cart")
                .header("Authorization", jwtToken))
                .andExpect(status().isNotFound());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void addItemToCartFail() throws Exception {
        CartItemRequest request = new CartItemRequest();

        when(userService.findUserByJwtToken(jwtToken)).thenThrow(new RuntimeException("Invalid token"));

        mockMvc.perform(post("/api/cart/items")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void updateCartItemFail() throws Exception {
        CartItemRequest request = new CartItemRequest();

        when(userService.findUserByJwtToken(jwtToken)).thenThrow(new RuntimeException("Invalid token"));

        mockMvc.perform(put("/api/cart/items/1")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void removeItemFromCartFail() throws Exception {
        when(userService.findUserByJwtToken(jwtToken)).thenThrow(new RuntimeException("Invalid token"));

        mockMvc.perform(delete("/api/cart/items/1")
                .header("Authorization", jwtToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void clearCartFail() throws Exception {
        when(userService.findUserByJwtToken(jwtToken)).thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(delete("/api/cart")
                .header("Authorization", jwtToken))
                .andExpect(status().isInternalServerError());
    }
}