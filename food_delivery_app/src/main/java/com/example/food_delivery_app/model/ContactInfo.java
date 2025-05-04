package com.example.food_delivery_app.model;


import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class ContactInfo {
    private String email;

    private String phone;

    private String socialMedia;

}
