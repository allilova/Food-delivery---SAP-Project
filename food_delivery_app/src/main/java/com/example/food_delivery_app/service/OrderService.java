package com.example.food_delivery_app.service;

import com.example.food_delivery_app.model.Order;
import com.example.food_delivery_app.model.OrderStatus;
import com.example.food_delivery_app.model.Restaurant;
import com.example.food_delivery_app.model.User;
import com.example.food_delivery_app.request.CreateOrderRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service for managing orders in the food delivery application.
 * Handles both customer-related and restaurant-related order operations.
 */
public interface OrderService {
    /**
     * Customer order methods - used by customers to manage their orders
     */
    
    /**
     * Creates a new order for a customer
     * @param orderRequest The order details including items, delivery address, etc.
     * @param user The authenticated user (customer) placing the order
     * @return The created Order entity
     */
    Order createOrder(CreateOrderRequest orderRequest, User user);
    
    /**
     * Gets all orders for a specific customer with pagination
     * @param user The authenticated user (customer) whose orders to retrieve
     * @param pageable Pagination information
     * @return A page of orders
     */
    Page<Order> getUserOrders(User user, Pageable pageable);
    
    /**
     * Gets a specific order by ID for a customer
     * @param orderId The ID of the order to retrieve
     * @param user The authenticated user (customer) who owns the order
     * @return The Order entity if found
     */
    Order getOrderById(Long orderId, User user);
    
    /**
     * Updates the status of a customer's order
     * @param orderId The ID of the order to update
     * @param status The new status as a string
     * @param user The authenticated user (customer) who owns the order
     * @return The updated Order entity
     */
    Order updateOrderStatus(Long orderId, String status, User user);
    
    /**
     * Restaurant order methods - used by restaurant owners to manage their orders
     */
    
    /**
     * Gets all orders for a specific restaurant with pagination
     * @param restaurantOwner The authenticated user (restaurant owner)
     * @param pageable Pagination information
     * @return A page of orders for the restaurant
     */
    Page<Order> getRestaurantOrders(User restaurantOwner, Pageable pageable);
    
    /**
     * Gets all orders with a specific status for a restaurant with pagination
     * @param restaurantOwner The authenticated user (restaurant owner)
     * @param status The order status to filter by
     * @param pageable Pagination information
     * @return A page of filtered orders for the restaurant
     */
    Page<Order> getRestaurantOrdersByStatus(User restaurantOwner, OrderStatus status, Pageable pageable);
    
    /**
     * Gets a specific order by ID for a restaurant
     * @param orderId The ID of the order to retrieve
     * @param restaurantOwner The authenticated user (restaurant owner)
     * @return The Order entity if found and belongs to the restaurant
     */
    Order getRestaurantOrderById(Long orderId, User restaurantOwner);
    
    /**
     * Updates the status of an order for a restaurant
     * @param orderId The ID of the order to update
     * @param status The new order status
     * @param restaurantOwner The authenticated user (restaurant owner)
     * @return The updated Order entity
     */
    Order updateRestaurantOrderStatus(Long orderId, OrderStatus status, User restaurantOwner);
    
    /**
     * Helper methods
     */
    
    /**
     * Gets or creates a restaurant for a user with restaurant role
     * @param user The authenticated user (restaurant owner)
     * @return The Restaurant entity associated with the user
     */
    Restaurant getUserRestaurant(User user);
} 