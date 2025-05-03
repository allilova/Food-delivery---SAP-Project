package com.example.food_delivery_app.repository;

import com.example.food_delivery_app.model.Menu;
import com.example.food_delivery_app.model.Restaurant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.Optional;
@DataJpaTest
class MenuRepositoryTest {
    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;
    @Test
    void ItShouldfindMenuByRestaurantID() {

        Restaurant restaurant = new Restaurant();
        restaurant = restaurantRepository.save(restaurant);

        Menu menu = new Menu();
        menu.setRestaurant(restaurant);
        menuRepository.save(menu);

        Optional<Menu> foundMenu = menuRepository.findByRestaurantId(restaurant.getId());

        assertThat(foundMenu).isPresent();
        assertThat(foundMenu.get().getRestaurant().getId()).isEqualTo(restaurant.getId());

    }
}