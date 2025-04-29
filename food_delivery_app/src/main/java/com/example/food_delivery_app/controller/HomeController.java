package com.example.food_delivery_app.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/api/home")
    public ResponseEntity<Map<String, Object>> getHomeData() {
        Map<String, Object> response = new HashMap<>();
        response.put("title", "Bunny-Fast Bites, Anytime!");
        response.put("mainText", "Feeling hungry? We've got you covered!");
        response.put("description", "Explore the menu, place your order, and enjoy fresh, hot, and tasty food in no time—no hassle, just great flavors.");
        response.put("callToAction", "Order now and let us handle the rest!");
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}