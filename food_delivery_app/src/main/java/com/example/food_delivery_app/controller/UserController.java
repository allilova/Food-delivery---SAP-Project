//package com.example.food_delivery_app.controller;
//
//import com.example.food_delivery_app.dto.UserProfileDto;
//import com.example.food_delivery_app.model.User;
//import com.example.food_delivery_app.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestHeader;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/api/users")
//public class UserController {
//    @Autowired
//    private UserService userService;
//
//    @GetMapping("/profile")
//    public ResponseEntity<UserProfileDto> findUserByJwtToken(@RequestHeader("Authorization")String jwt) throws Exception {
//        if (jwt.startsWith("Bearer ")) {
//            jwt = jwt.substring(7);
//        }
//
//        User user = userService.findUserByJwtToken(jwt);
//
//        UserProfileDto dto = new UserProfileDto();
//        dto.setId(user.getId());
//        dto.setName(user.getName());
//        dto.setEmail(user.getEmail());
//        dto.setPhoneNumber(user.getPhoneNumber());
//        dto.setAddress(user.getAddress().getStreet() + ", " + user.getAddress().getCity());
//        dto.setRole(user.getRole());
//
//        return new ResponseEntity<>(dto, HttpStatus.OK);
//    }
//}
package com.example.food_delivery_app.controller;

import com.example.food_delivery_app.dto.UserProfileDto;
import com.example.food_delivery_app.model.User;
import com.example.food_delivery_app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:4000"})
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(@RequestHeader("Authorization") String authHeader) {
        try {
            String jwt = extractJwt(authHeader);
            User user = userService.findUserByJwtToken(jwt);

            UserProfileDto dto = new UserProfileDto();
            dto.setId(user.getId());
            dto.setName(user.getName());
            dto.setEmail(user.getEmail());
            dto.setPhoneNumber(user.getPhoneNumber());
            dto.setRole(user.getRole());

            if (user.getAddress() != null) {
                dto.setAddress(user.getAddress().getStreet() + ", " + user.getAddress().getCity());
            } else {
                dto.setAddress("No address provided");
            }

            return ResponseEntity.ok(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing or invalid Authorization header");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }
    }

    private String extractJwt(String header) {
        if (header == null || !header.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid Authorization header");
        }
        return header.substring(7);
    }
}

