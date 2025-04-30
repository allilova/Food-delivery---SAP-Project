package com.example.food_delivery_app.service;

import com.example.food_delivery_app.model.User;
import com.example.food_delivery_app.dto.request.UpdateProfileRequest;
import com.example.food_delivery_app.dto.request.UpdatePasswordRequest;

public interface UserService {

    public User findUserByJwtToken(String jwt) throws Exception;

    public User findByEmail(String jwt) throws Exception;

<<<<<<< HEAD
<<<<<<< HEAD
    public User updateUserProfile(User user, UpdateProfileRequest updateRequest) throws Exception;

    public void updatePassword(User user, UpdatePasswordRequest passwordRequest) throws Exception;

    public void deleteUser(User user) throws Exception;
=======
    public User registerUser(User user);
>>>>>>> 3b97e188d54bd0a20c3391ce1ad1a3d3dc0fb7ca
=======
    public User registerUser(User user);
>>>>>>> 1db6b8e08a5a54e3b88a36b81c018b3860e2aaf5
}
