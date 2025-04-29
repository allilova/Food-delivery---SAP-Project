package com.example.food_delivery_app.repository;

import com.example.food_delivery_app.model.Category;
import com.example.food_delivery_app.model.Food;
import com.example.food_delivery_app.model.Menu;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class FoodRepositoryTest {

    @Autowired
    private FoodRepository foodRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void itShouldFindFoodByMenu() {
        Menu menu = new Menu();
        entityManager.persist(menu);

        Food food1 = new Food();
        food1.setMenu(menu);
        entityManager.persist(food1);

        Food food2 = new Food();
        food2.setMenu(menu);
        entityManager.persist(food2);

        List<Food> result = foodRepository.findByMenu(menu);

        assertThat(result).hasSize(2);
    }


    @Test
    void ItShouldFindByCategory() {
        Category category= new Category();
        entityManager.persist(category);

        Food food = new Food();
        food.setCategory(category);
        entityManager.persist(food);

        List<Food> result = foodRepository.findByCategory(category);

        assertThat(result).hasSize(1);
    }

    @Test
    void ItShouldSearchFoodByKeyword() {
        Category category= new Category();
        category.setCategoryName("Italian");
        entityManager.persist(category);

        Food food = new Food();
        food.setFoodName("Spaghetti Boloneze");
        food.setCategory(category);
        entityManager.persist(food);

        List<Food> result = foodRepository.searchFood("Spaghetti");

        assertThat(result).isNotEmpty().anyMatch(f -> f.getFoodName().contains("Spaghetti"));
    }
}