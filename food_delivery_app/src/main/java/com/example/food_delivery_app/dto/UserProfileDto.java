package com.example.food_delivery_app.dto;

import com.example.food_delivery_app.model.USER_ROLE;
import lombok.Data;
import java.util.List;
import java.util.ArrayList;

@Data
public class UserProfileDto {
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private String address;
    private USER_ROLE role;
    private List<RestaurantDto> favourites = new ArrayList<>();
}
