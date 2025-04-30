package com.example.food_delivery_app.request;

import com.example.food_delivery_app.model.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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

    @Data
    public static class UpdatePasswordRequest {
        @NotBlank(message = "Current password is required")
        private String currentPassword;

        @NotBlank(message = "New password is required")
        @Size(min = 6, message = "Password must be at least 6 characters long")
        private String newPassword;

        @NotBlank(message = "Confirm password is required")
        private String confirmPassword;
    }
}
