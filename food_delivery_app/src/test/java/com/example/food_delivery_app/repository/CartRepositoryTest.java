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
    void ItShouldFindCartByUserId() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setRole(USER_ROLE.ROLE_CUSTOMER);
        user.setName("Test User");
        user.setAddress("123 Test St");
        user.setPhoneNumber("1234567890");
        user = userRepository.save(user);

        Cart cart = new Cart();
        cart.setUser(user);
        cartRepository.save(cart);

<<<<<<< HEAD
<<<<<<< HEAD
        Optional<Cart> foundCart = cartRepository.findByUser_Id(user.getId());

        assertThat(foundCart).isPresent();
        assertThat(foundCart.get().getUser().getId()).isEqualTo(user.getId());
=======


       // Optional<Cart> foundCart = cartRepository.findByCustomer_UserID(user.getUserID());

      //  assertThat(foundCart).isPresent();
      //  assertThat(foundCart.get().getCustomer().getUserID()).isEqualTo(user.getUserID());
>>>>>>> 3b97e188d54bd0a20c3391ce1ad1a3d3dc0fb7ca
=======


       // Optional<Cart> foundCart = cartRepository.findByCustomer_UserID(user.getUserID());

      //  assertThat(foundCart).isPresent();
      //  assertThat(foundCart.get().getCustomer().getUserID()).isEqualTo(user.getUserID());
>>>>>>> 1db6b8e08a5a54e3b88a36b81c018b3860e2aaf5
    }

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

        Optional<Cart> foundCart = cartRepository.findByUser_Email("Sezer@gmail.com");

        assertThat(foundCart).isPresent();
        assertThat(foundCart.get().getUser().getEmail()).isEqualTo("Sezer@gmail.com");
    }
}