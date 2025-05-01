package com.example.food_delivery_app.service;

import com.example.food_delivery_app.config.JwtProvider;
import com.example.food_delivery_app.model.USER_ROLE;
import com.example.food_delivery_app.model.User;
import com.example.food_delivery_app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserServiceImp implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImp.class);
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User registerUser(User user) throws Exception {
        logger.debug("Registering new user: {}", user.getEmail());
        
        // Check for existing email
        if (userRepository.existsByEmail(user.getEmail())) {
            logger.warn("Registration failed: Email already exists - {}", user.getEmail());
            throw new Exception("A user with this email already exists.");
        }

        // Validate required fields
        if (user.getPhoneNumber() == null || user.getPhoneNumber().isEmpty()) {
            logger.warn("Registration failed: Phone number is required");
            throw new Exception("Phone number is required");
        }

        if (user.getAddress() == null || user.getAddress().isEmpty()) {
            logger.warn("Registration failed: Address is required");
            throw new Exception("Address is required");
        }

        // Encode password and set default role
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(USER_ROLE.ROLE_CUSTOMER);

        // Save and return user
        User savedUser = userRepository.save(user);
        logger.info("User registered successfully: {}", savedUser.getEmail());
        return savedUser;
    }

    @Override
    public User findUserByJwtToken(String jwt) throws Exception {
        // Extract email from token
        String email = jwtProvider.getEmailFromToken(jwt);
        
        // Find and return the user
        User user = userRepository.findByEmail(email);
        if (user == null) {
            logger.warn("User not found with email: {}", email);
            throw new Exception("User not found with email: " + email);
        }
        
        return user;
    }

    @Override
    public User findByEmail(String email) throws Exception {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            logger.warn("User not found with email: {}", email);
            throw new Exception("User not found with email: " + email);
        }

        return user;
    }

    @Override
    public void deleteUser(User user) {
        // Check if the user exists in the database
        if (user == null || !userRepository.existsById(user.getId())) {
            logger.warn("Delete user failed: User not found");
            throw new RuntimeException("User not found");
        }
        
        // Delete the user
        userRepository.delete(user);
        logger.info("User deleted successfully: {}", user.getEmail());
    }
    
    // Add the missing saveUser method
    @Override
    public User saveUser(User user) {
        logger.debug("Saving user: {}", user.getEmail());
        return userRepository.save(user);
    }
}