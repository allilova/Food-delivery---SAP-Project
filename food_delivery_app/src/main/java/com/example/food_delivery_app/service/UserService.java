package com.example.food_delivery_app.service;

import com.example.food_delivery_app.model.User;

public interface UserService {

    public User findUserByJwtToken(String jwt) throws Exception;

    public User findByEmail(String jwt) throws Exception;

    public User registerUser(User user);
}
