package com.example.food_delivery_app.service;

import com.example.food_delivery_app.config.JwtProvider;
import com.example.food_delivery_app.model.USER_ROLE;
import com.example.food_delivery_app.model.User;
import com.example.food_delivery_app.repository.UserRepository;
import com.example.food_delivery_app.dto.request.UpdateProfileRequest;
import com.example.food_delivery_app.dto.request.UpdatePasswordRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImp implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("A user with this email already exists.");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(USER_ROLE.ROLE_CUSTOMER);

        return userRepository.save(user);
    }

    @Override
    public User findUserByJwtToken(String jwt) throws Exception {
        // Extract email from token
        String email = jwtProvider.getEmailFromToken(jwt);
        
        // Find and return the user
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new Exception("User not found with email: " + email);
        }
        
        return user;
    }

    @Override
    public User findByEmail(String email) throws Exception {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new Exception("User not found with email: " + email);
        }

        return user;
    }

    @Override
    public User updateUserProfile(User user, UpdateProfileRequest updateRequest) throws Exception {
        user.setName(updateRequest.getFirstName() + " " + updateRequest.getLastName());
        user.setEmail(updateRequest.getEmail());
        user.setPhoneNumber(updateRequest.getPhoneNumber());
        return userRepository.save(user);
    }

    @Override
    public void updatePassword(User user, UpdatePasswordRequest passwordRequest) throws Exception {
        if (!passwordEncoder.matches(passwordRequest.getCurrentPassword(), user.getPassword())) {
            throw new Exception("Current password is incorrect");
        }

        if (!passwordRequest.getNewPassword().equals(passwordRequest.getConfirmPassword())) {
            throw new Exception("New password and confirm password do not match");
        }

        user.setPassword(passwordEncoder.encode(passwordRequest.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public void deleteUser(User user) throws Exception {
        userRepository.delete(user);
    }
}