package com.example.food_delivery_app.repository;

import com.example.food_delivery_app.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void itShouldFindExistingUserByEmail() {
        // given
        User user = new User();
        user.setEmail("Sezer@example.com");
        user.setPassword("password");
        userRepository.save(user);

        // when
        User foundUser = userRepository.findByEmail("Sezer@example.com");

        // then
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getEmail()).isEqualTo("Sezer@example.com");
    }

    @Test
    void itShouldCheckIfUserExistsByEmail() {
        // given
        User user = new User();
        user.setEmail("Sezer@example.com");
        user.setPassword("password");
        userRepository.save(user);

        // when
        boolean exists = userRepository.existsByEmail("Sezer@example.com");

        // then
        assertThat(exists).isTrue();
    }
}
