package com.example.food_delivery_app.service;

import com.example.food_delivery_app.exception.ReviewNotFoundException;
import com.example.food_delivery_app.model.Review;
import com.example.food_delivery_app.model.User;
import com.example.food_delivery_app.repository.ReviewRepository;
import com.example.food_delivery_app.request.CreateReviewRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Override
    public Review createReview(CreateReviewRequest reviewRequest, User user) {
        Review review = new Review();
        review.setRestaurantId(reviewRequest.getRestaurantId());
        review.setFoodId(reviewRequest.getFoodId());
        review.setRating(reviewRequest.getRating());
        review.setComment(reviewRequest.getComment());
        review.setUser(user);
        review.setCreatedAt(LocalDateTime.now());
        
        return reviewRepository.save(review);
    }

    @Override
    public Page<Review> getRestaurantReviews(Long restaurantId, Pageable pageable) {
        return reviewRepository.findByRestaurantId(restaurantId, pageable);
    }

    @Override
    public Page<Review> getFoodReviews(Long foodId, Pageable pageable) {
        return reviewRepository.findByFoodId(foodId, pageable);
    }

    @Override
    public Review updateReview(Long reviewId, CreateReviewRequest reviewRequest, User user) {
        Review review = reviewRepository.findByIdAndUser(reviewId, user)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found with ID: " + reviewId));
        
        review.setRating(reviewRequest.getRating());
        review.setComment(reviewRequest.getComment());
        review.setUpdatedAt(LocalDateTime.now());
        
        return reviewRepository.save(review);
    }

    @Override
    public void deleteReview(Long reviewId, User user) {
        Review review = reviewRepository.findByIdAndUser(reviewId, user)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found with ID: " + reviewId));
        
        reviewRepository.delete(review);
    }
} 