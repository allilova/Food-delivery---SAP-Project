package com.example.food_delivery_app.service;

import com.example.food_delivery_app.model.User;

public interface UserService {

    public User findUserByJwtToken(String jwt) throws Exception;

    public User findByEmail(String email) throws Exception;
    
    public void deleteUser(User user);

    public User registerUser(User user) throws Exception;
    
    // The missing method that was causing the error
    public User saveUser(User user);
}