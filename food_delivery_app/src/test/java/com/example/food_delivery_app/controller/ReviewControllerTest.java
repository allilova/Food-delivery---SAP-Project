package com.example.food_delivery_app.controller;

import com.example.food_delivery_app.model.Review;
import com.example.food_delivery_app.model.User;
import com.example.food_delivery_app.request.CreateReviewRequest;
import com.example.food_delivery_app.service.ReviewService;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "app.jwt.secret=test-secret-key",
        "app.jwt.expiration=3600000"
})
@AutoConfigureMockMvc
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ReviewService reviewService;

    @MockitoBean
    private UserService userService;

    private final String jwtToken = "mock-token";

    private User mockUser() {
        User user = new User();
        return user;
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createReview() throws Exception {
        CreateReviewRequest request = new CreateReviewRequest();
        Review review = new Review();

        when(userService.findUserByJwtToken("mock-token")).thenReturn(mockUser());
        when(reviewService.createReview(any(), any())).thenReturn(review);

        mockMvc.perform(post("/api/reviews")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getRestaurantReviews() throws Exception {
        Review review = new Review();
        when(reviewService.getRestaurantReviews(eq(1L), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(review)));

        mockMvc.perform(get("/api/reviews/restaurant/1")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getFoodReviews() throws Exception {
        Review review = new Review();
        when(reviewService.getFoodReviews(eq(1L), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(review)));

        mockMvc.perform(get("/api/reviews/food/1")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateReview() throws Exception {
        CreateReviewRequest request = new CreateReviewRequest();
        Review review = new Review();

        when(userService.findUserByJwtToken("mock-token")).thenReturn(mockUser());
        when(reviewService.updateReview(eq(1L), any(), any())).thenReturn(review);

        mockMvc.perform(put("/api/reviews/1")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteReview() throws Exception {
        when(userService.findUserByJwtToken("mock-token")).thenReturn(mockUser());

        mockMvc.perform(delete("/api/reviews/1").header("Authorization", jwtToken)).andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createReviewFail() throws Exception {
        CreateReviewRequest request = new CreateReviewRequest();
        when(userService.findUserByJwtToken("mock-token")).thenThrow(new RuntimeException("Invalid token"));

        mockMvc.perform(post("/api/reviews")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getRestaurantReviewsFail() throws Exception {
        when(reviewService.getRestaurantReviews(eq(1L), any(Pageable.class))).thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(get("/api/reviews/restaurant/1")).andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getFoodReviewsFail() throws Exception {
        when(reviewService.getFoodReviews(eq(1L), any(Pageable.class))).thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(get("/api/reviews/food/1")).andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateReviewFail() throws Exception {
        CreateReviewRequest request = new CreateReviewRequest();
        when(userService.findUserByJwtToken("mock-token")).thenReturn(mockUser());
        when(reviewService.updateReview(eq(1L), any(), any())).thenThrow(new RuntimeException("Update failed"));

        mockMvc.perform(put("/api/reviews/1")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteReviewFail() throws Exception {
        when(userService.findUserByJwtToken("mock-token")).thenReturn(mockUser());
        doThrow(new RuntimeException("Delete failed")).when(reviewService).deleteReview(eq(1L), any());

        mockMvc.perform(delete("/api/reviews/1").header("Authorization", jwtToken)).andExpect(status().isBadRequest());
    }
}