package com.example.food_delivery_app.service;

import com.example.food_delivery_app.config.JwtProvider;
import com.example.food_delivery_app.model.Cart;
import com.example.food_delivery_app.model.USER_ROLE;
import com.example.food_delivery_app.model.User;
import com.example.food_delivery_app.repository.CartRepository;
import com.example.food_delivery_app.repository.UserRepository;
import com.example.food_delivery_app.request.LoginRequest;
import com.example.food_delivery_app.response.AuthResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AuthenticationService {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private CustomerUserDetailsService customerUserDetailsService;

    public AuthResponse register(User user) throws Exception {
        // Log the incoming user data for debugging
        logger.debug("Registering user: {}", user.getEmail());
        logger.debug("User data: name={}, phoneNumber={}, address={}, role={}", 
                     user.getName(), user.getPhoneNumber(), user.getAddress(), user.getRole());

        // Check if email already exists
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new Exception("A user with this email already exists.");
        }

        // Set default role if not provided
        if (user.getRole() == null) {
            user.setRole(USER_ROLE.ROLE_CUSTOMER);
        }

        // Simple password validation
        if (user.getPassword() == null || user.getPassword().length() < 8) {
            throw new Exception("Password must be at least 8 characters long");
        }

        // Check for required fields
        if (user.getPhoneNumber() == null || user.getPhoneNumber().isEmpty()) {
            throw new Exception("Phone number is required");
        }

        if (user.getAddress() == null || user.getAddress().isEmpty()) {
            throw new Exception("Address is required");
        }

        if (user.getName() == null || user.getName().isEmpty()) {
            throw new Exception("Name is required");
        }

        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Save user
        User savedUser = userRepository.save(user);
        logger.debug("User saved successfully: {}", savedUser.getId());

        // Create cart for customer users
        if (savedUser.getRole() == USER_ROLE.ROLE_CUSTOMER) {
            Cart cart = new Cart();
            cart.setUser(savedUser);
            cartRepository.save(cart);
            logger.debug("Cart created for user: {}", savedUser.getId());
        }

        // Create authentication object
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getEmail(), user.getPassword());
        
        // Generate JWT token
        String jwt = jwtProvider.generateToken(authentication);

        // Return auth response
        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(jwt);
        authResponse.setMessage("Registration successful");
        authResponse.setRole(savedUser.getRole());

        return authResponse;
    }

    public AuthResponse login(LoginRequest loginRequest) {
        String username = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        // Authenticate user
        Authentication authentication = authenticate(username, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate JWT token
        String jwt = jwtProvider.generateToken(authentication);

        // Get user details
        User user = userRepository.findByEmail(username);

        // Return auth response
        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(jwt);
        authResponse.setMessage("Login successful");
        authResponse.setRole(user.getRole());

        return authResponse;
    }

    private Authentication authenticate(String username, String password) {
        // Load user details
        UserDetails userDetails = customerUserDetailsService.loadUserByUsername(username);
        
        if (userDetails == null) {
            throw new BadCredentialsException("Invalid username");
        }
        
        // Check password
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }
        
        // Return authentication object
        return new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
    }
}