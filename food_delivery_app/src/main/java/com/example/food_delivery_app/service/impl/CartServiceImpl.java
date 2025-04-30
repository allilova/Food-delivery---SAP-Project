package com.example.food_delivery_app.service.impl;

import com.example.food_delivery_app.exception.ResourceNotFoundException;
import com.example.food_delivery_app.model.Cart;
import com.example.food_delivery_app.model.CartItem;
import com.example.food_delivery_app.model.Food;
import com.example.food_delivery_app.model.User;
import com.example.food_delivery_app.repository.CartItemRepository;
import com.example.food_delivery_app.repository.CartRepository;
import com.example.food_delivery_app.repository.FoodRepository;
import com.example.food_delivery_app.request.CartItemRequest;
import com.example.food_delivery_app.service.CartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final FoodRepository foodRepository;

    public CartServiceImpl(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            FoodRepository foodRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.foodRepository = foodRepository;
    }

    @Override
    public Cart getCart(User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUser(user);
                    return cartRepository.save(cart);
                });
    }

    @Override
    public Cart addItemToCart(User user, CartItemRequest itemRequest) {
        Cart cart = getCart(user);
        Food food = foodRepository.findById(itemRequest.getFoodId())
                .orElseThrow(() -> new ResourceNotFoundException("Food not found"));

        CartItem existingItem = cart.getItems().stream()
                .filter(item -> item.getFood().getId().equals(food.getId()))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + itemRequest.getQuantity());
            existingItem.setPrice(food.getPrice() * existingItem.getQuantity());
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setFood(food);
            newItem.setQuantity(itemRequest.getQuantity());
            newItem.setPrice(food.getPrice() * itemRequest.getQuantity());
            cart.getItems().add(newItem);
        }

        updateCartTotal(cart);
        return cartRepository.save(cart);
    }

    @Override
    public Cart updateCartItem(User user, Long itemId, CartItemRequest itemRequest) {
        Cart cart = getCart(user);
        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new ResourceNotFoundException("Cart item not found in user's cart");
        }

        cartItem.setQuantity(itemRequest.getQuantity());
        cartItem.setPrice(cartItem.getFood().getPrice() * itemRequest.getQuantity());
        updateCartTotal(cart);
        return cartRepository.save(cart);
    }

    @Override
    public Cart removeItemFromCart(User user, Long itemId) {
        Cart cart = getCart(user);
        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new ResourceNotFoundException("Cart item not found in user's cart");
        }

        cart.getItems().remove(cartItem);
        cartItemRepository.delete(cartItem);
        updateCartTotal(cart);
        return cartRepository.save(cart);
    }

    @Override
    public void clearCart(User user) {
        Cart cart = getCart(user);
        cart.getItems().clear();
        cart.setTotalAmount(0.0);
        cartRepository.save(cart);
    }

    private void updateCartTotal(Cart cart) {
        double total = cart.getItems().stream()
                .mapToDouble(CartItem::getPrice)
                .sum();
        cart.setTotalAmount(total);
    }
} 