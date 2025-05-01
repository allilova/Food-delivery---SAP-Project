package com.example.food_delivery_app.service;

import com.example.food_delivery_app.config.JwtProvider;
import com.example.food_delivery_app.model.Cart;
import com.example.food_delivery_app.model.USER_ROLE;
import com.example.food_delivery_app.model.User;
import com.example.food_delivery_app.repository.CartRepository;
import com.example.food_delivery_app.repository.UserRepository;
import com.example.food_delivery_app.request.LoginRequest;
import com.example.food_delivery_app.request.RegisterRequest;
import com.example.food_delivery_app.response.AuthResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

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

    public AuthResponse register(RegisterRequest request) throws Exception {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new Exception("A user with this email already exists.");
        }
    
        User user = new User();
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        user.setPhoneNumber(request.getPhone());
    
        // default: ROLE_CUSTOMER if not provided
        USER_ROLE role;
        try {
            role = USER_ROLE.valueOf(request.getRole() != null ? request.getRole() : "ROLE_CUSTOMER");
        } catch (IllegalArgumentException e) {
            throw new Exception("Invalid role: " + request.getRole());
        }
    
        user.setRole(role);
    
        if (request.getPassword() == null || request.getPassword().length() < 8) {
            throw new Exception("Password must be at least 8 characters long");
        }
    
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        User savedUser = userRepository.save(user);
    
        if (role == USER_ROLE.ROLE_CUSTOMER) {
            Cart cart = new Cart();
            cart.setUser(savedUser);
            cartRepository.save(cart);
        }
    
        UserDetails userDetails = customerUserDetailsService.loadUserByUsername(user.getEmail());
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        String jwt = jwtProvider.generateToken(authentication);
    
        AuthResponse response = new AuthResponse();
        response.setJwt(jwt);
        response.setMessage("Registration successful");
        response.setRole(savedUser.getRole());
        return response;
    }
    

    // public AuthResponse register(User user) throws Exception {
    //     // Check if email already exists
    //     if (userRepository.existsByEmail(user.getEmail())) {
    //         throw new Exception("A user with this email already exists.");
    //     }

    //     // Set default role if not provided
    //     if (user.getRole() == null) {
    //         user.setRole(USER_ROLE.ROLE_CUSTOMER);
    //     }

    //     // Simple password validation - removing complex regex for now
    //     // This will allow the registration to work while still enforcing basic security
    //     if (user.getPassword() == null || user.getPassword().length() < 8) {
    //         throw new Exception("Password must be at least 8 characters long");
    //     }

    //     // Encode password
    //     user.setPassword(passwordEncoder.encode(user.getPassword()));

    //     // Save user
    //     User savedUser = userRepository.save(user);

    //     // Create cart for customer users
    //     if (savedUser.getRole() == USER_ROLE.ROLE_CUSTOMER) {
    //         Cart cart = new Cart();
    //         cart.setUser(savedUser);
    //         cartRepository.save(cart);
    //     }

    //     // Create authentication object
    //     // Create authentication object properly using UserDetailsService
    //     UserDetails userDetails = customerUserDetailsService.loadUserByUsername(user.getEmail());
    //     Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());


    //     // Generate JWT token
    //     String jwt = jwtProvider.generateToken(authentication);

    //     // Return auth response
    //     AuthResponse authResponse = new AuthResponse();
    //     authResponse.setJwt(jwt);
    //     authResponse.setMessage("Registration successful");
    //     authResponse.setRole(savedUser.getRole());

    //     return authResponse;
    // }

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