package com.example.food_delivery_app.service;

import com.example.food_delivery_app.model.User;
import com.example.food_delivery_app.request.CreateAddressRequest;
import com.example.food_delivery_app.request.CreateRestaurantRequest;
import jakarta.validation.Valid;

public interface UserService {

    public User findUserByJwtToken(String jwt) throws Exception;

    public User findByEmail(String jwt) throws Exception;

    public User registerUser(User user);

    public User updateUserProfile(User user, CreateAddressRequest.UpdateProfileRequest updateRequest);

    public void updatePassword(User user, CreateRestaurantRequest.UpdatePasswordRequest passwordRequest);

    public void deleteUser(User user);
}
