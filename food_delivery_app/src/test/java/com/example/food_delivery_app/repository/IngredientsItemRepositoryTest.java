package com.example.food_delivery_app.repository;

import com.example.food_delivery_app.model.IngredientsItem;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.Optional;

@DataJpaTest
class IngredientsItemRepositoryTest {
    @Autowired
    private IngredientsItemRepository ingredientsItemRepository;
    @Test
    void findByIngredientName() {

        IngredientsItem ingredientsItem = new IngredientsItem();
        ingredientsItem.setIngredientName("Tomato");
        ingredientsItemRepository.save(ingredientsItem);

        Optional<IngredientsItem> foundName = ingredientsItemRepository.findByIngredientName("Tomato");

        assertThat(foundName).isPresent();
        assertThat(foundName.get().getIngredientName()).isEqualTo(ingredientsItem.getIngredientName());
    }
}