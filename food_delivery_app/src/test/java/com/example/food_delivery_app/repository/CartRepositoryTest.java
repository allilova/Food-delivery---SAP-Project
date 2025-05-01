package com.example.food_delivery_app.repository;

import com.example.food_delivery_app.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.Optional;


@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:mysql://localhost:3306/food_delivery_test?createDatabaseIfNotExist=true",
    "spring.datasource.username=root",
    "spring.datasource.password=root",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class CartRepositoryTest {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;


    @Test
    void ItShouldFindCartByUserId() {
        // Create address
        Address address = new Address();
        address.setStreet("123 Test St");
        address.setCity("Test City");

        // Create user
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setRole(USER_ROLE.ROLE_CUSTOMER);
        user.setName("Test User");
        user.setAddress(address);
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
        // Create address
        Address address = new Address();
        address.setStreet("123 Test St");
        address.setCity("Test City");

        // Create user
        User user = new User();
        user.setEmail("Sezer@gmail.com");
        user.setPassword("password");
        user.setRole(USER_ROLE.ROLE_CUSTOMER);
        user.setName("Sezer");
        user.setAddress(address);
        user.setPhoneNumber("1234567890");
        user = userRepository.save(user);

        Cart cart = new Cart();
        cart.setUser(user);
        cartRepository.save(cart);

        Optional<Cart> foundCart = cartRepository.findByUserEmail("Sezer@gmail.com");

        assertThat(foundCart).isPresent();
        assertThat(foundCart.get().getUser().getEmail()).isEqualTo("Sezer@gmail.com");
    }
}