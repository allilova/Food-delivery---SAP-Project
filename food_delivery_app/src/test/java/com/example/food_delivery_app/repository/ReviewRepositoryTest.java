package com.example.food_delivery_app.repository;

import com.example.food_delivery_app.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
@DataJpaTest
class ReviewRepositoryTest {
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private RestaurantRepository restaurantRepository;
    @Autowired
    private FoodRepository foodRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    void findReviewByRestaurantId() {
        Restaurant restaurant = new Restaurant();
        restaurantRepository.save(restaurant);

        Review review = new Review();
        review.setRestaurantId(restaurant.getId());
        reviewRepository.save(review);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Review> reviewPage = reviewRepository.findByRestaurantId(restaurant.getId(), pageable);

        assertThat(reviewPage).isNotEmpty();
        assertThat(reviewPage.getContent()).hasSize(1);
        assertThat(reviewPage.getContent().get(0).getRestaurantId()).isEqualTo(restaurant.getId());

    }

    @Test
    void findByFoodId() {
        Restaurant restaurant = new Restaurant();
        restaurantRepository.save(restaurant);

        Food food = new Food();
        food.setName("Food 1");
        food.setDescription("Description 1");
        food.setPrice(10.0);
        food.setIsAvailable(true);
        food.setImageUrl("http://example.com/food1.jpg");
        food.setRestaurant(restaurant);
        foodRepository.save(food);

        Review review = new Review();
        review.setFoodId(food.getId());
        reviewRepository.save(review);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Review> reviewPage = reviewRepository.findByFoodId(food.getId(), pageable);

        assertThat(reviewPage).isNotEmpty();
        assertThat(reviewPage.getContent()).hasSize(1);
        assertThat(reviewPage.getContent().get(0).getFoodId()).isEqualTo(food.getId());

    }

    @Test
    void findByIdAndUser() {

        User user = new User();
        user.setEmail("Sezer@gmail.com");
        user.setPassword("password");
        user.setName("Sezer");
        user.setPhoneNumber("1234567890");
        userRepository.save(user);

        Review review = new Review();
        review.setUser(user);
        reviewRepository.save(review);

        Optional<Review> result = reviewRepository.findByIdAndUser(review.getId(), user);

        assertThat(result).isPresent();
        assertThat(result.get().getUser()).isEqualTo(user);
        assertThat(result.get().getId()).isEqualTo(review.getId());
    }
}