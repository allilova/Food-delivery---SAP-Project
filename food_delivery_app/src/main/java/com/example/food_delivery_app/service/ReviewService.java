package com.example.food_delivery_app.service;

import com.example.food_delivery_app.model.Review;
import com.example.food_delivery_app.model.User;
import com.example.food_delivery_app.request.CreateReviewRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewService {
    Review createReview(CreateReviewRequest reviewRequest, User user);
    Page<Review> getRestaurantReviews(Long restaurantId, Pageable pageable);
    Page<Review> getFoodReviews(Long foodId, Pageable pageable);
    Review updateReview(Long reviewId, CreateReviewRequest reviewRequest, User user);
    void deleteReview(Long reviewId, User user);
} 