package com.example.food_delivery_app.service;

import com.example.food_delivery_app.exception.CartItemNotFoundException;
import com.example.food_delivery_app.exception.CartNotFoundException;
import com.example.food_delivery_app.exception.FoodNotFoundException;
import com.example.food_delivery_app.model.Cart;
import com.example.food_delivery_app.model.CartItem;
import com.example.food_delivery_app.model.Food;
import com.example.food_delivery_app.model.User;
import com.example.food_delivery_app.repository.CartItemRepository;
import com.example.food_delivery_app.repository.CartRepository;
import com.example.food_delivery_app.repository.FoodRepository;
import com.example.food_delivery_app.request.CartItemRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Transactional
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private FoodRepository foodRepository;

    @Override
    public Cart getCart(User user) {
        return cartRepository.findByUser(user)
            .orElseThrow(() -> new CartNotFoundException("Cart not found for user: " + user.getId()));
    }

    private void updateCartTotalAmount(Cart cart) {
        double total = cart.getItems().stream()
            .mapToDouble(CartItem::getPrice)
            .sum();
        cart.setTotalAmount(total);
    }

    @Override
    public Cart addItemToCart(User user, CartItemRequest itemRequest) {
        Cart cart = cartRepository.findByUser(user)
            .orElseThrow(() -> new CartNotFoundException("Cart not found for user: " + user.getId()));

        Food food = foodRepository.findById(itemRequest.getFoodId())
                .orElseThrow(() -> new FoodNotFoundException("Food not found with ID: " + itemRequest.getFoodId()));

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getFood().getId().equals(food.getId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + itemRequest.getQuantity());
            item.setPrice(food.getPrice() * item.getQuantity());
            cartItemRepository.save(item);
        } else {
            CartItem newItem = new CartItem();
            newItem.setFood(food);
            newItem.setQuantity(itemRequest.getQuantity());
            newItem.setPrice(food.getPrice() * itemRequest.getQuantity());
            newItem.setCart(cart);
            cartItemRepository.save(newItem);
            cart.getItems().add(newItem);
        }

        updateCartTotalAmount(cart);
        return cartRepository.save(cart);
    }

    @Override
    public Cart updateCartItem(User user, Long itemId, CartItemRequest itemRequest) {
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for user: " + user.getId()));

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new CartItemNotFoundException("Cart item not found with ID: " + itemId));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("Item does not belong to user's cart");
        }

        item.setQuantity(itemRequest.getQuantity());
        item.setPrice(item.getFood().getPrice() * itemRequest.getQuantity());
        cartItemRepository.save(item);
        
        updateCartTotalAmount(cart);
        return cartRepository.save(cart);
    }

    @Override
    public Cart removeItemFromCart(User user, Long itemId) {
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for user: " + user.getId()));

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new CartItemNotFoundException("Cart item not found with ID: " + itemId));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("Item does not belong to user's cart");
        }

        cart.getItems().remove(item);
        cartItemRepository.delete(item);
        
        updateCartTotalAmount(cart);
        return cartRepository.save(cart);
    }

    @Override
    public void clearCart(User user) {
        Cart cart = cartRepository.findByUser(user)
            .orElseThrow(() -> new CartNotFoundException("Cart not found for user: " + user.getId()));
        
        cartItemRepository.deleteAll(cart.getItems());
        cart.getItems().clear();
        cart.setTotalAmount(0.0);
        cartRepository.save(cart);
    }
} 