package com.example.food_delivery_app.service;

import com.example.food_delivery_app.model.Order;
import com.example.food_delivery_app.model.User;
import com.example.food_delivery_app.request.CreateOrderRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    Order createOrder(CreateOrderRequest orderRequest, User user);
    Page<Order> getUserOrders(User user, Pageable pageable);
    Order getOrderById(Long orderId, User user);
    Order updateOrderStatus(Long orderId, String status, User user);
} 