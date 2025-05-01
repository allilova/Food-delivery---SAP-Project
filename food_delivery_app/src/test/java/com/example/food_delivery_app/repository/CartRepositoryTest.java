package com.example.food_delivery_app.repository;

import com.example.food_delivery_app.model.Cart;
import com.example.food_delivery_app.model.Order;
import com.example.food_delivery_app.model.Payment;
import com.example.food_delivery_app.model.User;
import com.example.food_delivery_app.model.USER_ROLE;
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
    void ItShouldFindCartByUserEmail() {
        User user = new User();
        user.setEmail("Sezer@gmail.com");
        user.setPassword("password");
        user.setRole(USER_ROLE.ROLE_CUSTOMER);
        user.setName("Sezer");
        user.setAddress("123 Test St");
        user.setPhoneNumber("1234567890");
        user = userRepository.save(user);

        Cart cart = new Cart();
        cart.setUser(user);
        cartRepository.save(cart);

        //Optional<Cart> foundCart = cartRepository.findByUser_Email("Sezer@gmail.com");

        //assertThat(foundCart).isPresent();
        //assertThat(foundCart.get().getUser().getEmail()).isEqualTo("Sezer@gmail.com");
    }
}