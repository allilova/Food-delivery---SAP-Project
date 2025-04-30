package com.example.food_delivery_app.controller;

import com.example.food_delivery_app.dto.FoodResponseDto;
import com.example.food_delivery_app.model.Food;
import com.example.food_delivery_app.model.Menu;
import com.example.food_delivery_app.model.User;
import com.example.food_delivery_app.service.FoodService;
import com.example.food_delivery_app.service.MenuService;
import com.example.food_delivery_app.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/food")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:4000"})
public class FoodController {
<<<<<<< HEAD
    private static final Logger logger = LoggerFactory.getLogger(FoodController.class);
    private final FoodService foodService;
    private final UserService userService;
    private final RestaurantService restaurantService;

    public FoodController(FoodService foodService, UserService userService, RestaurantService restaurantService) {
        this.foodService = foodService;
        this.userService = userService;
        this.restaurantService = restaurantService;
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Food>> searchFood(
            @RequestParam String foodName,
            @RequestHeader("Authorization") String jwtT,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            User user = userService.findUserByJwtToken(jwtT);
            Pageable pageable = PageRequest.of(page, size);
            Page<Food> foods = foodService.searchFood(foodName, pageable);
            logger.info("Food search successful for query: {}", foodName);
            return new ResponseEntity<>(foods, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Food search failed for query: {}", foodName, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/menu/{menuId}")
    public ResponseEntity<Page<Food>> getMenuFood(
            @PathVariable Long menuId,
            @RequestParam(required = false) String food_category,
            @RequestHeader("Authorization") String jwtT,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            User user = userService.findUserByJwtToken(jwtT);
            Menu menu = restaurantService.getMenuById(menuId)
                    .orElseThrow(() -> new IllegalArgumentException("Menu not found"));
            
            Pageable pageable = PageRequest.of(page, size);
            Page<Food> foods = foodService.getMenuFood(menu, food_category, pageable);
            logger.info("Menu food retrieval successful for menu: {}", menuId);
            return new ResponseEntity<>(foods, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid menu request: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Menu food retrieval failed for menu: {}", menuId, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
=======
    @Autowired
    private FoodService foodService;

    @Autowired
    private UserService userService;

    @Autowired
    private MenuService menuService;


    @GetMapping("/search")
    public ResponseEntity<List<FoodResponseDto>> searchFood(@RequestParam String foodName,
                                                            @RequestHeader("Authorization") String jwt) throws Exception {
        userService.findUserByJwtToken(jwt);
        List<Food> foods = foodService.searchFood(foodName);

        List<FoodResponseDto> response = foods.stream()
                .map(food -> foodService.convertToDto(food)) // ще трябва да направиш convertToDto публичен
                .toList();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/menu/{menuId}")
    public ResponseEntity<List<FoodResponseDto>> getMenuFood(@PathVariable Long menuId,
                                                             @RequestParam(required = false) String foodCategory,
                                                             @RequestHeader("Authorization") String jwt) throws Exception {
        userService.findUserByJwtToken(jwt);
        Menu menu = menuService.getMenuById(menuId);

        List<Food> foods = foodService.getMenuFood(menu, foodCategory);
        List<FoodResponseDto> response = foods.stream()
                .map(food -> foodService.convertToDto(food))
                .toList();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


>>>>>>> 3b97e188d54bd0a20c3391ce1ad1a3d3dc0fb7ca
}
