package com.example.food_delivery_app.controller;

import com.example.food_delivery_app.model.*;
import com.example.food_delivery_app.repository.CategoryRepository;
import com.example.food_delivery_app.request.CreateFoodRequest;
import com.example.food_delivery_app.service.FoodService;
import com.example.food_delivery_app.service.MenuService;
import com.example.food_delivery_app.service.RestaurantService;
import com.example.food_delivery_app.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Optional;
import java.math.BigDecimal;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdminFoodController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminFoodControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private FoodService foodService;
    @MockitoBean
    private UserService userService;
    @MockitoBean
    private RestaurantService restaurantService;
    @MockitoBean
    private MenuService menuService;
    @MockitoBean
    private CategoryRepository categoryRepository;
    @MockitoBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;
    @MockitoBean
    private AuditorAware<Object> auditorAware;
    @Test
    void ItShouldCreateFood() throws Exception{

        CreateFoodRequest food = new CreateFoodRequest();
        food.setFoodName("Pizza");
        food.setFoodDescription("Peperoni");
        food.setFoodPrice(BigDecimal.valueOf(15));
        food.setCategoryId(1L);
        food.setMenuId(1L);
        food.setRestaurantId(1L);

        User mockUser = new User();
        Restaurant mockRestaurant = new Restaurant();
        Menu mockMenu = new Menu();
        Category mockCategory = new Category();
        Food mockFood = new Food();
        mockFood.setName("Pizza");

        given(userService.findUserByJwtToken("Bearer test")).willReturn(mockUser);
        given(restaurantService.findById(1L)).willReturn(mockRestaurant);
        given(menuService.getMenuById(1L)).willReturn(mockMenu);
        given(categoryRepository.findById(1L)).willReturn(Optional.of(mockCategory));
        given(foodService.createFood(food, mockCategory, mockMenu)).willReturn(mockFood);


        mockMvc.perform(post("/api/admin/food")
                .header("Authorization", "Bearer test")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(food)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Pizza"));
    }

    @Test
    void ItShouldDeleteFood() throws Exception {
        User mockUser = new User();
        Food deletedFood = new Food();
        deletedFood.setId(1L);
        deletedFood.setName("Pizza");

        given(userService.findUserByJwtToken("Bearer test")).willReturn(mockUser);
        given(foodService.deleteFood(1L)).willReturn(deletedFood);

        mockMvc.perform(delete("/api/admin/food/1")
                .header("Authorization", "Bearer test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Successfully deleted food"));
    }
    @Test
    void ShouldUpdateFoodAvailability() throws Exception{

        User mockUser = new User();
        Food updatedFood = new Food();
        updatedFood.setId(1L);
        updatedFood.setName("Pizza");
        updatedFood.setAvailable(true);

        given(userService.findUserByJwtToken("Bearer test")).willReturn(mockUser);
        given(foodService.updateAvailabilityStatus(1L)).willReturn(updatedFood);

        mockMvc.perform(put("/api/admin/food/1")
                .header("Authorization", "Bearer test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.available").value(true));
    }
}