package com.example.food_delivery_app.service;

import com.example.food_delivery_app.exception.ResourceNotFoundException;
import com.example.food_delivery_app.model.Cart;
import com.example.food_delivery_app.model.CartItem;
import com.example.food_delivery_app.model.Food;
import com.example.food_delivery_app.model.User;
import com.example.food_delivery_app.repository.CartItemRepository;
import com.example.food_delivery_app.repository.CartRepository;
import com.example.food_delivery_app.repository.FoodRepository;
import com.example.food_delivery_app.request.CartItemRequest;
import com.example.food_delivery_app.service.impl.CartServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private FoodRepository foodRepository;

    @InjectMocks
    private CartServiceImpl cartService;

    private User user;
    private Food food;
    private Cart cart;
    private CartItemRequest cartItemRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");

        food = new Food();
        food.setId(1L);
        food.setName("Test Food");
        food.setPrice(10.0);

        cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
        cart.setTotalAmount(0.0);

        cartItemRequest = new CartItemRequest();
        cartItemRequest.setFoodId(1L);
        cartItemRequest.setQuantity(2);
    }

    @Test
    void getCart_WhenCartExists_ReturnsCart() {
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));

        Cart result = cartService.getCart(user);

        assertNotNull(result);
        assertEquals(cart.getId(), result.getId());
        verify(cartRepository).findByUser(user);
    }

    @Test
    void getCart_WhenCartDoesNotExist_CreatesNewCart() {
        when(cartRepository.findByUser(user)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        Cart result = cartService.getCart(user);

        assertNotNull(result);
        assertEquals(cart.getId(), result.getId());
        verify(cartRepository).findByUser(user);
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    void addItemToCart_WhenFoodExists_AddsItemToCart() {
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(foodRepository.findById(1L)).thenReturn(Optional.of(food));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        Cart result = cartService.addItemToCart(user, cartItemRequest);

        assertNotNull(result);
        assertEquals(cart.getId(), result.getId());
        verify(cartRepository).findByUser(user);
        verify(foodRepository).findById(1L);
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    void addItemToCart_WhenFoodDoesNotExist_ThrowsException() {
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(foodRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> 
            cartService.addItemToCart(user, cartItemRequest)
        );

        verify(cartRepository).findByUser(user);
        verify(foodRepository).findById(1L);
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    void updateCartItem_WhenItemExists_UpdatesItem() {
        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setCart(cart);
        cartItem.setFood(food);
        cartItem.setQuantity(1);
        cartItem.setPrice(10.0);
        cart.getItems().add(cartItem);

        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(cartItem));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        Cart result = cartService.updateCartItem(user, 1L, cartItemRequest);

        assertNotNull(result);
        assertEquals(cart.getId(), result.getId());
        verify(cartRepository).findByUser(user);
        verify(cartItemRepository).findById(1L);
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    void removeItemFromCart_WhenItemExists_RemovesItem() {
        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setCart(cart);
        cartItem.setFood(food);
        cartItem.setQuantity(1);
        cartItem.setPrice(10.0);
        cart.getItems().add(cartItem);

        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(cartItem));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        Cart result = cartService.removeItemFromCart(user, 1L);

        assertNotNull(result);
        assertEquals(cart.getId(), result.getId());
        verify(cartRepository).findByUser(user);
        verify(cartItemRepository).findById(1L);
        verify(cartItemRepository).delete(cartItem);
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    void clearCart_WhenCartExists_ClearsCart() {
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        cartService.clearCart(user);

        verify(cartRepository).findByUser(user);
        verify(cartRepository).save(any(Cart.class));
        assertTrue(cart.getItems().isEmpty());
        assertEquals(0.0, cart.getTotalAmount());
    }
} 