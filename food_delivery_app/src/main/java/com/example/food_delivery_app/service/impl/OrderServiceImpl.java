package com.example.food_delivery_app.service.impl;

import com.example.food_delivery_app.exception.ResourceNotFoundException;
import com.example.food_delivery_app.model.Food;
import com.example.food_delivery_app.model.Order;
import com.example.food_delivery_app.model.OrderItem;
import com.example.food_delivery_app.model.Restaurant;
import com.example.food_delivery_app.model.User;
import com.example.food_delivery_app.repository.FoodRepository;
import com.example.food_delivery_app.repository.OrderRepository;
import com.example.food_delivery_app.repository.RestaurantRepository;
import com.example.food_delivery_app.request.CreateOrderRequest;
import com.example.food_delivery_app.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final RestaurantRepository restaurantRepository;
    private final FoodRepository foodRepository;

    public OrderServiceImpl(
            OrderRepository orderRepository,
            RestaurantRepository restaurantRepository,
            FoodRepository foodRepository) {
        this.orderRepository = orderRepository;
        this.restaurantRepository = restaurantRepository;
        this.foodRepository = foodRepository;
    }

    @Override
    public Order createOrder(CreateOrderRequest orderRequest, User user) {
        Restaurant restaurant = restaurantRepository.findById(orderRequest.getRestaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));

        Order order = new Order();
        order.setUser(user);
        order.setRestaurant(restaurant);
        order.setDeliveryAddress(orderRequest.getDeliveryAddress());
        order.setContactNumber(orderRequest.getContactNumber());
        order.setSpecialInstructions(orderRequest.getSpecialInstructions());
        order.setStatus(Order.OrderStatus.PENDING);

        List<OrderItem> orderItems = new ArrayList<>();
        double totalAmount = 0.0;

        for (CreateOrderRequest.OrderItemRequest itemRequest : orderRequest.getItems()) {
            Food food = foodRepository.findById(itemRequest.getFoodId())
                    .orElseThrow(() -> new ResourceNotFoundException("Food not found"));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setFood(food);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setPrice(food.getPrice() * itemRequest.getQuantity());
            orderItems.add(orderItem);

            totalAmount += orderItem.getPrice();
        }

        order.setItems(orderItems);
        order.setTotalAmount(totalAmount);

        return orderRepository.save(order);
    }

    @Override
    public Page<Order> getUserOrders(User user, Pageable pageable) {
        return orderRepository.findByUser(user, pageable);
    }

    @Override
    public Order getOrderById(Long orderId, User user) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Order not found");
        }

        return order;
    }

    @Override
    public Order updateOrderStatus(Long orderId, String status, User user) {
        Order order = getOrderById(orderId, user);

        try {
            Order.OrderStatus newStatus = Order.OrderStatus.valueOf(status.toUpperCase());
            order.setStatus(newStatus);
            return orderRepository.save(order);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid order status: " + status);
        }
    }
} 