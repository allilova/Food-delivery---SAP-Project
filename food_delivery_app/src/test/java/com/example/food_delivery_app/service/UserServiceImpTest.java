package com.example.food_delivery_app.service;

import com.example.food_delivery_app.config.JwtProvider;
import com.example.food_delivery_app.model.USER_ROLE;
import com.example.food_delivery_app.model.User;
import com.example.food_delivery_app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImpTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtProvider jwtProvider;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImp userServiceImp;

    private User user;

    @BeforeEach
    public void setup() {
        user = new User();
        user.setName("Sezer");
        user.setEmail("Sezer@gmail.com");
        user.setPassword("123456789");
    }


    @Test
    void TestRegisterUserSuccessful() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(passwordEncoder.encode("123456789")).thenReturn("encodedPassword");

        User savedUser = new User();
        savedUser.setEmail(user.getEmail());
        savedUser.setPassword("encodedPassword");
        savedUser.setRole(USER_ROLE.ROLE_CUSTOMER);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userServiceImp.registerUser(user);

        assertNotNull(result);
        assertEquals("encodedPassword", result.getPassword());
        assertEquals(USER_ROLE.ROLE_CUSTOMER, result.getRole());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void TestRegisterUserDuplicateEmailThrowsException() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userServiceImp.registerUser(user);
        });

        assertEquals("A user with this email already exists.", exception.getMessage());
    }
    @Test
    void findUserByJwtTokenSuccessful()  throws Exception {
        String token = "token";
        String email = "Sezer@gmail.com";

        User foundUser = new User();
        foundUser.setEmail(email);

        when(jwtProvider.getEmailFromToken(token)).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(foundUser);

        User result = userServiceImp.findUserByJwtToken(token);

        assertNotNull(result);
        assertEquals(email, result.getEmail());

    }

    @Test
    void FindUserByJwtTokenUserNotFound() {
        String token = "token";
        String email = "Sezer@gmail.com";

        when(jwtProvider.getEmailFromToken(token)).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(null);

        Exception exception = assertThrows(Exception.class, () -> {
            userServiceImp.findUserByJwtToken(token);
        });

        assertTrue(exception.getMessage().contains("User not found"));


    }

    @Test
    void findByUsernameSuccessful() throws Exception {
        String token = "token";
        String email = "Sezer@gmail.com";

        User user = new User();
        user.setEmail(email);

        when(jwtProvider.getEmailFromToken(token)).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(user);

        User result = userServiceImp.findByUsername(token);

        assertNotNull(result);
        assertEquals(email, result.getEmail());
    }

    @Test
    void testFindByUsername_userNotFound() {
        String token = "token";
        String email = "Sezer@gmail.com";

        when(jwtProvider.getEmailFromToken(token)).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(null);

        Exception exception = assertThrows(Exception.class, () -> {
            userServiceImp.findByUsername(token);
        });

        assertTrue(exception.getMessage().contains("User not found"));
    }


    @Test
    void TestFindByEmailSuccessful() throws Exception {
        String email = "Sezer@gmail.com";

        User user = new User();
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(user);

        User result = userServiceImp.findByEmail(email);

        assertNotNull(result);
        assertEquals(email, result.getEmail());
    }

    @Test
    void TestFindByEmailNotFound() {
        String email = "Sezer@gmail.com";

        when(userRepository.findByEmail(email)).thenReturn(null);

        Exception exception = assertThrows(Exception.class, () -> {
            userServiceImp.findByEmail(email);
        });

        assertTrue(exception.getMessage().contains("User not found"));
    }
}