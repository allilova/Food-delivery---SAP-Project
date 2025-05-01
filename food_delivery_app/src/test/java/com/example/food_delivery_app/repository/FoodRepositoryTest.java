/* package com.example.food_delivery_app.repository;

import com.example.food_delivery_app.model.Category;
import com.example.food_delivery_app.model.Food;
import com.example.food_delivery_app.model.Menu;
import com.example.food_delivery_app.model.Restaurant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;
import java.util.ArrayList;
import org.springframework.test.context.TestPropertySource;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:mysql://localhost:3306/food_delivery_test?createDatabaseIfNotExist=true",
    "spring.datasource.username=root",
    "spring.datasource.password=root",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class FoodRepositoryTest {

    @Autowired
    private FoodRepository foodRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void itShouldFindFoodByMenu() {
        Restaurant restaurant = new Restaurant();
        restaurant.setRestaurantName("Test Restaurant");
        entityManager.persist(restaurant);

        Menu menu = new Menu();
        menu.setRestaurant(restaurant);
        menu.setFoods(new ArrayList<>());
        menu.setCategories(new ArrayList<>());
        entityManager.persist(menu);

        Category category = new Category();
        category.setCategoryName("Test Category");
        category.setMenu(menu);
        entityManager.persist(category);

        Food food1 = new Food();
        food1.setName("Food 1");
        food1.setDescription("Description 1");
        food1.setPrice(10.0);
        food1.setRestaurant(menu.getRestaurant());
        food1.setIsAvailable(true);
        food1.setImageUrl("http://example.com/food1.jpg");
        food1.setCategory(category);
        entityManager.persist(food1);

        Food food2 = new Food();
        food2.setName("Food 2");
        food2.setDescription("Description 2");
        food2.setPrice(15.0);
        food2.setRestaurant(menu.getRestaurant());
        food2.setIsAvailable(true);
        food2.setImageUrl("http://example.com/food2.jpg");
        food2.setCategory(category);
        entityManager.persist(food2);

        menu.getFoods().add(food1);
        menu.getFoods().add(food2);
        menu.getCategories().add(category);
        entityManager.persist(menu);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Food> result = foodRepository.findByMenu(menu, pageable);

        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    void ItShouldFindByCategory() {
        Category category = new Category();
        category.setCategoryName("Test Category");
        entityManager.persist(category);

        Restaurant restaurant = new Restaurant();
        restaurant.setRestaurantName("Test Restaurant");
        entityManager.persist(restaurant);

        Food food = new Food();
        food.setName("Test Food");
        food.setDescription("Test Description");
        food.setPrice(10.0);
        food.setCategory(category);
        food.setRestaurant(restaurant);
        food.setIsAvailable(true);
        food.setImageUrl("http://example.com/test-food.jpg");
        entityManager.persist(food);

        List<Food> result = foodRepository.findByCategory(category);

        assertThat(result).hasSize(1);
    }

    @Test
    void ItShouldSearchFoodByKeyword() {
        Category category = new Category();
        category.setCategoryName("Italian");
        entityManager.persist(category);

        Restaurant restaurant = new Restaurant();
        restaurant.setRestaurantName("Test Restaurant");
        entityManager.persist(restaurant);

        Food food = new Food();
        food.setName("Spaghetti Boloneze");
        food.setDescription("Classic Italian pasta");
        food.setPrice(12.0);
        food.setCategory(category);
        food.setRestaurant(restaurant);
        food.setIsAvailable(true);
        food.setImageUrl("http://example.com/spaghetti.jpg");
        entityManager.persist(food);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Food> result = foodRepository.searchFood("Spaghetti", pageable);

        assertThat(result.getContent()).isNotEmpty()
            .anyMatch(f -> f.getName().contains("Spaghetti"));
    }
} */