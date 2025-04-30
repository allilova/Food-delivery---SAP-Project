package com.example.food_delivery_app.controller;

import com.example.food_delivery_app.model.Review;
import com.example.food_delivery_app.model.User;
import com.example.food_delivery_app.request.CreateReviewRequest;
import com.example.food_delivery_app.service.ReviewService;
import com.example.food_delivery_app.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:4000"})
public class ReviewController {
    private static final Logger logger = LoggerFactory.getLogger(ReviewController.class);
    private final ReviewService reviewService;
    private final UserService userService;

    public ReviewController(ReviewService reviewService, UserService userService) {
        this.reviewService = reviewService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<Review> createReview(
            @Valid @RequestBody CreateReviewRequest reviewRequest,
            @RequestHeader("Authorization") String jwtToken) {
        try {
            User user = userService.findUserByJwtToken(jwtToken);
            Review review = reviewService.createReview(reviewRequest, user);
            logger.info("Review created successfully by user: {}", user.getEmail());
            return new ResponseEntity<>(review, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Failed to create review", e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<Page<Review>> getRestaurantReviews(
            @PathVariable Long restaurantId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Review> reviews = reviewService.getRestaurantReviews(restaurantId, pageable);
            logger.info("Retrieved reviews for restaurant: {}", restaurantId);
            return new ResponseEntity<>(reviews, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Failed to retrieve restaurant reviews", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/food/{foodId}")
    public ResponseEntity<Page<Review>> getFoodReviews(
            @PathVariable Long foodId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Review> reviews = reviewService.getFoodReviews(foodId, pageable);
            logger.info("Retrieved reviews for food: {}", foodId);
            return new ResponseEntity<>(reviews, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Failed to retrieve food reviews", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<Review> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody CreateReviewRequest reviewRequest,
            @RequestHeader("Authorization") String jwtToken) {
        try {
            User user = userService.findUserByJwtToken(jwtToken);
            Review review = reviewService.updateReview(reviewId, reviewRequest, user);
            logger.info("Review updated successfully: {}", reviewId);
            return new ResponseEntity<>(review, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Failed to update review: {}", reviewId, e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long reviewId,
            @RequestHeader("Authorization") String jwtToken) {
        try {
            User user = userService.findUserByJwtToken(jwtToken);
            reviewService.deleteReview(reviewId, user);
            logger.info("Review deleted successfully: {}", reviewId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            logger.error("Failed to delete review: {}", reviewId, e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
} 