package com.example.food_delivery_app.repository;

import com.example.food_delivery_app.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void itShouldFindExistingUserByEmail() {
        // Given
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setName("Test User");
        user.setPhoneNumber("1234567890");
        userRepository.save(user);

        // When
        User found = userRepository.findByEmail("test@example.com");

        // Then
        assertNotNull(found);
        assertEquals("test@example.com", found.getEmail());
    }

    @Test
    public void itShouldCheckIfUserExistsByEmail() {
        // Given
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setName("Test User");
        user.setPhoneNumber("1234567890");
        userRepository.save(user);

        // When & Then
        assertTrue(userRepository.existsByEmail("test@example.com"));
        assertFalse(userRepository.existsByEmail("nonexistent@example.com"));
    }
}
