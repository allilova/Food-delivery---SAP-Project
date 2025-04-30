package com.example.food_delivery_app.request;

import com.example.food_delivery_app.model.*;
import lombok.Data;

import java.util.List;

@Data
public class CreateRestaurantRequest {

    private String restaurantName;
    private Address restaurantAddress;
    private List<String> images;
    private String type;
    private String openingHours;
    private String closingHours;
    private ContactInfo contactInfo;
}
