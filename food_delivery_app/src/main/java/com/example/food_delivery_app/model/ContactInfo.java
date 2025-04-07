package com.example.food_delivery_app.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContactInfo {
    private String email;

    private String phone;

    private String socialMedia;

}
