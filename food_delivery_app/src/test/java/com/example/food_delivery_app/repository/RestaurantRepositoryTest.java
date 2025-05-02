package com.example.food_delivery_app.repository;

import com.example.food_delivery_app.model.Restaurant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
public class RestaurantRepositoryTest {
    @Autowired
    private RestaurantRepository restaurantRepository;
    @Test
    void ItShouldFindRestaurantBySearchQuery() {
        Restaurant restaurant1 = new Restaurant();
        restaurant1.setRestaurantName("Not Happy");
        restaurant1.setType("Fast Food");

        Restaurant restaurant2 = new Restaurant();
        restaurant2.setRestaurantName("Neapol");
        restaurant2.setType("Italian");

        restaurantRepository.save(restaurant1);
        restaurantRepository.save(restaurant2);

        List<Restaurant> foundRestaurants = restaurantRepository.findBySearchQuery("happy");

        assertThat(foundRestaurants).isNotEmpty().anyMatch(r -> r.getRestaurantName().toLowerCase().contains("happy"));

    }
    @Test
    void itShouldFindById() {

        Restaurant restaurant = new Restaurant();
        restaurant.setRestaurantName("BFC");
        restaurant.setType("Fast Food");
        restaurantRepository.save(restaurant);

        Optional<Restaurant> foundRestaurant = restaurantRepository.findById(restaurant.getId());

        assertThat(foundRestaurant).isPresent();
        assertThat(foundRestaurant.get().getId()).isEqualTo(restaurant.getId());
    }

}