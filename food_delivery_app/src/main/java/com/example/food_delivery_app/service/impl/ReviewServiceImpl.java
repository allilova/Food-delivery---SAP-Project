package com.example.food_delivery_app.service.impl;

import com.example.food_delivery_app.exception.ResourceNotFoundException;
import com.example.food_delivery_app.model.Food;
import com.example.food_delivery_app.model.Restaurant;
import com.example.food_delivery_app.model.Review;
import com.example.food_delivery_app.model.User;
import com.example.food_delivery_app.repository.FoodRepository;
import com.example.food_delivery_app.repository.RestaurantRepository;
import com.example.food_delivery_app.repository.ReviewRepository;
import com.example.food_delivery_app.request.CreateReviewRequest;
import com.example.food_delivery_app.service.ReviewService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final RestaurantRepository restaurantRepository;
    private final FoodRepository foodRepository;

    public ReviewServiceImpl(
            ReviewRepository reviewRepository,
            RestaurantRepository restaurantRepository,
            FoodRepository foodRepository) {
        this.reviewRepository = reviewRepository;
        this.restaurantRepository = restaurantRepository;
        this.foodRepository = foodRepository;
    }

    @Override
    public Review createReview(CreateReviewRequest reviewRequest, User user) {
        Review review = new Review();
        review.setUser(user);
        review.setRating(reviewRequest.getRating());
        review.setComment(reviewRequest.getComment());

        if (reviewRequest.getRestaurantId() != null) {
            Restaurant restaurant = restaurantRepository.findById(reviewRequest.getRestaurantId())
                    .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));
            review.setRestaurant(restaurant);
        }

        if (reviewRequest.getFoodId() != null) {
            Food food = foodRepository.findById(reviewRequest.getFoodId())
                    .orElseThrow(() -> new ResourceNotFoundException("Food not found"));
            review.setFood(food);
        }

        return reviewRepository.save(review);
    }

    @Override
    public Page<Review> getRestaurantReviews(Long restaurantId, Pageable pageable) {
        return reviewRepository.findByRestaurantRestaurantID(restaurantId, pageable);
    }

    @Override
    public Page<Review> getFoodReviews(Long foodId, Pageable pageable) {
        return reviewRepository.findByFoodId(foodId, pageable);
    }

    @Override
    public Review updateReview(Long reviewId, CreateReviewRequest reviewRequest, User user) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        if (!review.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Review not found");
        }

        review.setRating(reviewRequest.getRating());
        review.setComment(reviewRequest.getComment());

        return reviewRepository.save(review);
    }

    @Override
    public void deleteReview(Long reviewId, User user) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        if (!review.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Review not found");
        }

        reviewRepository.delete(review);
    }
} 