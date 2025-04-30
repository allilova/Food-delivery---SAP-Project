package com.example.food_delivery_app.repository;

import com.example.food_delivery_app.model.Cart;
import com.example.food_delivery_app.model.Order;
import com.example.food_delivery_app.model.Payment;
import com.example.food_delivery_app.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.Optional;


@DataJpaTest
class CartRepositoryTest {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;


    @Test
    void ItShouldFindCustomerBy_UserID() {
        User user = new User();
        user = userRepository.save(user);

        Cart cart = new Cart();
        cart.setCustomer(user);
        cartRepository.save(cart);



       // Optional<Cart> foundCart = cartRepository.findByCustomer_UserID(user.getUserID());

      //  assertThat(foundCart).isPresent();
      //  assertThat(foundCart.get().getCustomer().getUserID()).isEqualTo(user.getUserID());
    }

    @Test
    void ItShouldFindCartByCustomer_Email() {
        User user = new User();
        user.setEmail("Sezer@gmail.com");
        user = userRepository.save(user);

        Cart cart = new Cart();
        cart.setCustomer(user);
        cartRepository.save(cart);

        Optional<Cart> foundCart = cartRepository.findByCustomer_Email("Sezer@gmail.com");

        assertThat(foundCart).isPresent();
        assertThat(foundCart.get().getCustomer().getEmail()).isEqualTo("Sezer@gmail.com");

    }
}