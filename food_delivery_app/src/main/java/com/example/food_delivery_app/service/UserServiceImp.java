package com.example.food_delivery_app.service;

import com.example.food_delivery_app.config.JwtProvider;
import com.example.food_delivery_app.model.USER_ROLE;
import com.example.food_delivery_app.model.User;
import com.example.food_delivery_app.repository.UserRepository;
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
        return null;
    }

    @Override
    public User findByUsername(String jwt) throws Exception {
        String email = jwtProvider.getEmailFromToken(jwt);
        User user = userRepository.findByEmail(email);
        return user;
    }

    @Override
    public User findByEmail(String email) throws Exception{
        User user = userRepository.findByEmail(email);

        if(user == null) {
            throw new Exception("User not found");
        }

        return user;
    }

}

