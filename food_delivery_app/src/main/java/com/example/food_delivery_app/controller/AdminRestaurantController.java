package com.example.food_delivery_app.controller;

import com.example.food_delivery_app.dto.RestaurantResponseDto;
import com.example.food_delivery_app.model.Restaurant;
import com.example.food_delivery_app.model.USER_ROLE;
import com.example.food_delivery_app.model.User;
import com.example.food_delivery_app.request.CreateRestaurantRequest;
import com.example.food_delivery_app.response.MessageResponse;
import com.example.food_delivery_app.service.RestaurantService;
import com.example.food_delivery_app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/admin/restaurants")
public class AdminRestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<RestaurantResponseDto> createRestaurant(@RequestBody CreateRestaurantRequest req,
                                                                  @RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserByJwtToken(jwt);
        checkAdminAccess(user);
        Restaurant created = restaurantService.createRestaurant(req, user);
        RestaurantResponseDto dto = restaurantService.convertToDto(created);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Restaurant> updateRestaurant(@RequestBody CreateRestaurantRequest req,
                                                       @RequestHeader("Authorization")String jwt,
                                                       @PathVariable Long id) throws Exception {
        User user = userService.findUserByJwtToken(jwt);
        Restaurant restaurant = restaurantService.updateRestaurant(id, req);
        return new ResponseEntity<>(restaurant, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteRestaurant(@RequestHeader("Authorization") String jwt,
                                                            @PathVariable Long id) throws Exception {
        User user = userService.findUserByJwtToken(jwt);
        checkAdminAccess(user);
        restaurantService.deleteRestaurant(id);
        MessageResponse res = new MessageResponse();
        res.setMessage("Restaurant deleted");
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<RestaurantResponseDto> updateRestaurantStatus(@RequestHeader("Authorization") String jwt,
                                                                        @PathVariable Long id) throws Exception {
        User user = userService.findUserByJwtToken(jwt);
        checkAdminAccess(user);
        Restaurant updated = restaurantService.updateRestaurantStatus(id);
        RestaurantResponseDto dto = restaurantService.convertToDto(updated);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    private void checkAdminAccess(User user) throws Exception {
        if (user.getRole() != USER_ROLE.ROLE_ADMIN) {
            throw new Exception("Access denied: Admin role required.");
        }
    }
}
