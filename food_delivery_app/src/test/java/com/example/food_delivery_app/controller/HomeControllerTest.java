package com.example.food_delivery_app.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "app.jwt.secret=test-secret-key",
        "app.jwt.expiration=3600000"
})
@AutoConfigureMockMvc
class HomeControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void getHomeData() throws Exception {
        mockMvc.perform(get("/api/home"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Bunny-Fast Bites, Anytime!"))
                .andExpect(jsonPath("$.mainText").value("Feeling hungry? We've got you covered!"))
                .andExpect(jsonPath("$.description").value("Explore the menu, place your order, and enjoy fresh, hot, and tasty food in no time—no hassle, just great flavors."))
                .andExpect(jsonPath("$.callToAction").value("Order now and let us handle the rest!"));
    }
}