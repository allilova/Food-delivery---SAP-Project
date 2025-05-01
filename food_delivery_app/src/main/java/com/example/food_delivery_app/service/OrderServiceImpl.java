package com.example.food_delivery_app.service;

import com.example.food_delivery_app.model.*;
import com.example.food_delivery_app.repository.OrderRepository;
import com.example.food_delivery_app.request.CreateOrderRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private FoodService foodService;

    @Override
    public Order createOrder(CreateOrderRequest orderRequest, User user) {
        Order order = new Order();
        order.setCustomer(user);
        order.setOrderDate(new Date());
        order.setOrderStatus(OrderStatus.PENDING);
        
        Address address = new Address();
        address.setStreet(orderRequest.getDeliveryAddress());
        order.setDeliveryAddress(address);

        List<OrderItem> orderItems = orderRequest.getItems().stream()
                .map(itemRequest -> {
                    OrderItem orderItem = new OrderItem();
                    try {
                        Food food = foodService.findById(itemRequest.getFoodId());
                        orderItem.setFood(food);
                        orderItem.setQuantity(itemRequest.getQuantity());
                        orderItem.setPrice(food.getPrice() * itemRequest.getQuantity());
                        orderItem.setOrder(order);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to process order item: " + e.getMessage());
                    }
                    return orderItem;
                })
                .collect(Collectors.toList());

        order.setItems(orderItems);
        order.setTotalPrice(calculateTotalPrice(orderItems));
        order.setItemsQuantity(orderItems.size());

        return orderRepository.save(order);
    }

    @Override
    public Page<Order> getUserOrders(User user, Pageable pageable) {
        return orderRepository.findByCustomer(user, pageable);
    }

    @Override
    public Order getOrderById(Long orderId, User user) {
        return orderRepository.findByIdAndCustomer(orderId, user)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    @Override
    public Order updateOrderStatus(Long orderId, String status, User user) {
        Order order = getOrderById(orderId, user);
        order.setOrderStatus(OrderStatus.valueOf(status.toUpperCase()));
        return orderRepository.save(order);
    }

    private float calculateTotalPrice(List<OrderItem> items) {
        return (float) items.stream()
                .mapToDouble(OrderItem::getPrice)
                .sum();
    }
} 