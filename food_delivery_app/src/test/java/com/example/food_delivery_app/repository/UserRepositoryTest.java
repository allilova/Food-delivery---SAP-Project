/*package com.example.food_delivery_app.repository;

import com.example.food_delivery_app.config.TestConfig;
import com.example.food_delivery_app.model.USER_ROLE;
import com.example.food_delivery_app.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(TestConfig.class)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.NONE)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:mysql://localhost:3306/food_delivery_test?createDatabaseIfNotExist=true",
    "spring.datasource.username=root",
    "spring.datasource.password=root",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void itShouldFindExistingUserByEmail() {
        // Given
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setRole(USER_ROLE.ROLE_CUSTOMER);
        user.setName("Test User");
        user.setAddress("123 Test St");
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
        user.setRole(USER_ROLE.ROLE_CUSTOMER);
        user.setName("Test User");
        user.setAddress("123 Test St");
        user.setPhoneNumber("1234567890");
        userRepository.save(user);

        // When & Then
        assertTrue(userRepository.existsByEmail("test@example.com"));
        assertFalse(userRepository.existsByEmail("nonexistent@example.com"));
    }
}
*/