package com.example.food_delivery_app.request;

import lombok.Data;

@Data
public class CreateAddressRequest {
    private String street;

    private String city;
}
