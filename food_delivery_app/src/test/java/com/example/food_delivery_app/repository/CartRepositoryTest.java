package com.example.food_delivery_app.repository;

import com.example.food_delivery_app.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.Optional;


@DataJpaTest

class CartRepositoryTest {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;


    @Test
    void ItShouldFindCartByUserId() {

        User user = new User();
        user.setEmail("Sezer@gmail.com");
        user.setPassword("password");
        user.setName("Sezer");
        user.setPhoneNumber("1234567890");
        user = userRepository.save(user);

        Cart cart = new Cart();
        cart.setUser(user);
        cartRepository.save(cart);

        Optional<Cart> foundCart = cartRepository.findByUserId(user.getId());

        assertThat(foundCart).isPresent();
        assertThat(foundCart.get().getUser().getId()).isEqualTo(user.getId());
    }

    @Test
    void ItShouldFindCartByUserEmail() {

        User user = new User();
        user.setEmail("Sezer@gmail.com");
        user.setPassword("password");
        user.setName("Sezer");
        user.setPhoneNumber("1234567890");
        user = userRepository.save(user);

        Cart cart = new Cart();
        cart.setUser(user);
        cartRepository.save(cart);

        Optional<Cart> foundCart = cartRepository.findByUserEmail("Sezer@gmail.com");

        assertThat(foundCart).isPresent();
        assertThat(foundCart.get().getUser().getEmail()).isEqualTo("Sezer@gmail.com");
    }

    @Test
    void ItShouldFindCartByUser() {

        User user = new User();
        user.setEmail("Sezer@gmail.com");
        user.setPassword("password");
        user.setName("Sezer");
        user.setPhoneNumber("1234567890");
        user = userRepository.save(user);

        Cart cart = new Cart();
        cart.setUser(user);
        cartRepository.save(cart);

        Optional<Cart> foundCart = cartRepository.findByUser(user);

        assertThat(foundCart).isPresent();
        assertThat(foundCart.get().getUser()).isEqualTo(user);

    }
}