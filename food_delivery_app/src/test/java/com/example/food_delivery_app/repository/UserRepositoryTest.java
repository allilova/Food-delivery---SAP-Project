package com.example.food_delivery_app.repository;
import com.example.food_delivery_app.model.User;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
@ExtendWith(MockitoExtension.class)
class UserRepositoryTest {

    @Mock
    private UserRepository userRepository;

    @Test
    void itShouldFindExistingUserByEmail() {
        // given
        User user = new User();
        user.setEmail("Sezer@example.com");

        when(userRepository.findByEmail("Sezer@example.com")).thenReturn(user);

        // when
        User foundUser = userRepository.findByEmail("Sezer@example.com");

        // then
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getEmail()).isEqualTo("Sezer@example.com");

        verify(userRepository).findByEmail("Sezer@example.com");
    }

    @Test
    void itShouldCheckIfUserExistsByEmail() {
        // given
        String email = "Sezer@example.com";

        when(userRepository.existsByEmail(email)).thenReturn(true);

        // when
        boolean exists = userRepository.existsByEmail(email);

        // then
        assertThat(exists).isTrue();

        verify(userRepository).existsByEmail(email);
    }
}
