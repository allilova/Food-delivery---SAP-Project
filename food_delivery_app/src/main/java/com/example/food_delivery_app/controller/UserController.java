package com.example.food_delivery_app.controller;

import com.example.food_delivery_app.dto.UserProfileDto;
import com.example.food_delivery_app.model.User;
import com.example.food_delivery_app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserProfileDto> findUserByJwtToken(@RequestHeader("Authorization")String jwt) throws Exception {
        if (jwt.startsWith("Bearer ")) {
            jwt = jwt.substring(7);
        }

        User user = userService.findUserByJwtToken(jwt);

        UserProfileDto dto = new UserProfileDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhone_number());
        dto.setAddress(user.getAddress());
        dto.setRole(user.getRole());

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }
}
