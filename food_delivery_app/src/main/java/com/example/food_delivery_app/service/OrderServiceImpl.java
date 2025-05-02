package com.example.food_delivery_app.service;

import com.example.food_delivery_app.model.*;
import com.example.food_delivery_app.repository.OrderRepository;
import com.example.food_delivery_app.repository.RestaurantRepository;
import com.example.food_delivery_app.request.CreateOrderRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private FoodService foodService;

    // CUSTOMER ORDER METHODS

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
                        
                        // Set the restaurant from the food item
                        if (order.getRestaurant() == null && food.getMenu() != null && food.getMenu().getRestaurant() != null) {
                            order.setRestaurant(food.getMenu().getRestaurant());
                        }
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
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
    }

    @Override
    public Order updateOrderStatus(Long orderId, String status, User user) {
        Order order = getOrderById(orderId, user);
        try {
            OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
            order.setOrderStatus(orderStatus);
            order.setUpdatedAt(new Date());
            return orderRepository.save(order);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid order status: " + status);
        }
    }

    // RESTAURANT ORDER METHODS

    @Override
    public Page<Order> getRestaurantOrders(User restaurantOwner, Pageable pageable) {
        org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(OrderServiceImpl.class);
        
        try {
            if (restaurantOwner == null) {
                logger.error("Restaurant owner is null in getRestaurantOrders");
                return Page.empty(pageable);
            }
            
            logger.info("Getting all restaurant orders for user: {}", restaurantOwner.getEmail());
            
            Restaurant restaurant;
            try {
                restaurant = getUserRestaurant(restaurantOwner);
                if (restaurant == null) {
                    logger.error("Restaurant not found and could not be created for user {}", restaurantOwner.getEmail());
                    return Page.empty(pageable);
                }
                logger.info("Found restaurant: {} (ID: {})", restaurant.getRestaurantName(), restaurant.getId());
            } catch (Exception e) {
                logger.error("Error getting restaurant for user {}: {}", restaurantOwner.getEmail(), e.getMessage());
                e.printStackTrace();
                return Page.empty(pageable);
            }
            
            try {
                Page<Order> orders = orderRepository.findByRestaurant(restaurant, pageable);
                logger.info("Found {} orders for restaurant {}", orders.getTotalElements(), restaurant.getId());
                return orders;
            } catch (Exception e) {
                logger.error("Error retrieving orders for restaurant {}: {}", 
                    restaurant.getId(), e.getMessage(), e);
                e.printStackTrace();
                return Page.empty(pageable);
            }
        } catch (Exception e) {
            logger.error("Unexpected error in getRestaurantOrders: {}", e.getMessage(), e);
            e.printStackTrace();
            return Page.empty(pageable);
        }
    }

    @Override
    public Page<Order> getRestaurantOrdersByStatus(User restaurantOwner, OrderStatus status, Pageable pageable) {
        org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(OrderServiceImpl.class);
        
        try {
            if (restaurantOwner == null) {
                logger.error("Restaurant owner is null in getRestaurantOrdersByStatus");
                return Page.empty(pageable);
            }
            
            logger.info("Getting restaurant orders with status: {} for user: {}", status, restaurantOwner.getEmail());
            
            Restaurant restaurant;
            try {
                restaurant = getUserRestaurant(restaurantOwner);
                if (restaurant == null) {
                    logger.error("Restaurant not found and could not be created for user {}", restaurantOwner.getEmail());
                    return Page.empty(pageable);
                }
            } catch (Exception e) {
                logger.error("Error getting restaurant for user {}: {}", restaurantOwner.getEmail(), e.getMessage());
                e.printStackTrace();
                return Page.empty(pageable);
            }
            
            // Special handling for IN_PROGRESS status which is a composite status
            if (status == OrderStatus.IN_PROGRESS) {
                logger.info("IN_PROGRESS status detected - fetching all active orders");
                // For IN_PROGRESS, we need to fetch orders with multiple statuses
                List<Order> activeOrders = new ArrayList<>();
                
                // Get orders for each active status
                List<OrderStatus> activeStatuses = Arrays.asList(
                    OrderStatus.CONFIRMED, 
                    OrderStatus.PREPARING, 
                    OrderStatus.READY, 
                    OrderStatus.OUT_FOR_DELIVERY
                );
                
                // Log the types of statuses we're looking for
                logger.info("Searching for orders with statuses: {}", activeStatuses);
                
                try {
                    for (OrderStatus activeStatus : activeStatuses) {
                        try {
                            Page<Order> ordersWithStatus = orderRepository.findByRestaurantAndOrderStatus(
                                restaurant, activeStatus, PageRequest.of(0, 100));
                            activeOrders.addAll(ordersWithStatus.getContent());
                            logger.info("Found {} orders with status: {}", ordersWithStatus.getContent().size(), activeStatus);
                        } catch (Exception e) {
                            logger.error("Error fetching orders with status {}: {}", activeStatus, e.getMessage());
                            // Continue with next status even if this one fails
                        }
                    }
                    
                    // If no orders found at all, return empty page immediately
                    if (activeOrders.isEmpty()) {
                        logger.info("No active orders found for restaurant ID {}", restaurant.getId());
                        return Page.empty(pageable);
                    }
                    
                    // Create a new Page with the combined results
                    final int start = (int)pageable.getOffset();
                    final int end = Math.min((start + pageable.getPageSize()), activeOrders.size());
                    
                    // Sort by order date in descending order
                    activeOrders.sort((o1, o2) -> {
                        if (o1.getOrderDate() == null) return 1;
                        if (o2.getOrderDate() == null) return -1;
                        return o2.getOrderDate().compareTo(o1.getOrderDate());
                    });
                    
                    // Check boundaries to avoid exceptions
                    if (start >= 0 && start <= end && end <= activeOrders.size()) {
                        List<Order> pageContent = activeOrders.subList(start, end);
                        return new PageImpl<>(pageContent, pageable, activeOrders.size());
                    } else {
                        // If start is beyond the end, return empty page
                        logger.warn("Invalid page parameters: start={}, end={}, size={}", 
                            start, end, activeOrders.size());
                        return Page.empty(pageable);
                    }
                } catch (Exception e) {
                    logger.error("Error processing active orders: {}", e.getMessage(), e);
                    e.printStackTrace();
                    // Return empty page in case of error
                    return Page.empty(pageable);
                }
            } else {
                // Normal case - just fetch orders with the specific status
                try {
                    logger.info("Fetching orders with status {} for restaurant ID {}", status, restaurant.getId());
                    
                    // Safety check for the status
                    if (status == null) {
                        logger.error("Status is null in getRestaurantOrdersByStatus");
                        return Page.empty(pageable);
                    }
                    
                    try {
                        Page<Order> orders = orderRepository.findByRestaurantAndOrderStatus(restaurant, status, pageable);
                        logger.info("Found {} orders with status {} for restaurant ID {}", 
                            orders.getTotalElements(), status, restaurant.getId());
                        return orders;
                    } catch (Exception e) {
                        logger.error("Database error fetching orders: {}", e.getMessage(), e);
                        e.printStackTrace();
                        return Page.empty(pageable);
                    }
                } catch (Exception e) {
                    logger.error("Error fetching orders with status {} for restaurant ID {}: {}", 
                        status, restaurant.getId(), e.getMessage(), e);
                    e.printStackTrace();
                    // Return empty page instead of throwing error to avoid 500
                    return Page.empty(pageable);
                }
            }
        } catch (Exception e) {
            logger.error("Unexpected error in getRestaurantOrdersByStatus: {}", e.getMessage(), e);
            e.printStackTrace();
            return Page.empty(pageable);
        }
    }

    @Override
    public Order getRestaurantOrderById(Long orderId, User restaurantOwner) {
        org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(OrderServiceImpl.class);
        try {
            logger.info("Getting order ID {} for restaurant owner {}", orderId, restaurantOwner.getEmail());
            
            Restaurant restaurant = getUserRestaurant(restaurantOwner);
            logger.info("Found restaurant ID {} for order lookup", restaurant.getId());
            
            return orderRepository.findByIdAndRestaurant(orderId, restaurant)
                    .orElseThrow(() -> {
                        logger.warn("Order ID {} not found for restaurant ID {}", orderId, restaurant.getId());
                        return new ResponseStatusException(HttpStatus.NOT_FOUND, 
                            "Order " + orderId + " not found for restaurant " + restaurant.getRestaurantName());
                    });
        } catch (ResponseStatusException e) {
            // Just rethrow response status exceptions 
            throw e;
        } catch (Exception e) {
            logger.error("Error retrieving order ID {} for restaurant owner {}: {}", 
                orderId, restaurantOwner.getEmail(), e.getMessage(), e);
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "Error retrieving order: " + e.getMessage(), e);
        }
    }

    @Override
    public Order updateRestaurantOrderStatus(Long orderId, OrderStatus status, User restaurantOwner) {
        org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(OrderServiceImpl.class);
        try {
            logger.info("Updating order ID {} to status {} for restaurant owner {}", 
                orderId, status, restaurantOwner.getEmail());
            
            Order order = getRestaurantOrderById(orderId, restaurantOwner);
            OrderStatus previousStatus = order.getOrderStatus();
            
            // Validate status transition
            if (!isValidStatusTransition(previousStatus, status)) {
                logger.warn("Invalid status transition from {} to {} for order ID {}", 
                    previousStatus, status, orderId);
                // Allow it for now but log a warning
            }
            
            order.setOrderStatus(status);
            order.setUpdatedAt(new Date());
            
            Order savedOrder = orderRepository.save(order);
            logger.info("Successfully updated order ID {} status from {} to {}", 
                orderId, previousStatus, status);
            
            return savedOrder;
        } catch (ResponseStatusException e) {
            // Just rethrow response status exceptions
            throw e;
        } catch (Exception e) {
            logger.error("Error updating order ID {} status to {}: {}", 
                orderId, status, e.getMessage(), e);
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "Error updating order status: " + e.getMessage(), e);
        }
    }
    
    // Helper method to validate status transitions
    private boolean isValidStatusTransition(OrderStatus from, OrderStatus to) {
        // Define valid transitions
        switch (from) {
            case PENDING:
                return to == OrderStatus.CONFIRMED || to == OrderStatus.CANCELLED;
            case CONFIRMED:
                return to == OrderStatus.PREPARING || to == OrderStatus.CANCELLED;
            case PREPARING:
                return to == OrderStatus.READY || to == OrderStatus.CANCELLED;
            case READY:
                return to == OrderStatus.OUT_FOR_DELIVERY || to == OrderStatus.CANCELLED;
            case OUT_FOR_DELIVERY:
                return to == OrderStatus.DELIVERED || to == OrderStatus.CANCELLED;
            case DELIVERED:
                return false; // Terminal state
            case CANCELLED:
                return false; // Terminal state
            case IN_PROGRESS:
                // IN_PROGRESS is a virtual status for UI purposes, allow any state
                return true;
            default:
                return false;
        }
    }

    // HELPER METHODS

    @Override
    public Restaurant getUserRestaurant(User user) {
        // Add detailed logging
        org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(OrderServiceImpl.class);
        logger.info("Getting restaurant for user: {} (ID: {}, Role: {})", user.getName(), user.getId(), user.getRole());
        
        // Check if the user has restaurant role
        if (user.getRole() != USER_ROLE.ROLE_RESTAURANT && user.getRole() != USER_ROLE.ROLE_ADMIN) {
            logger.error("User {} does not have restaurant or admin role", user.getEmail());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not a restaurant owner or admin");
        }
        
        // Find restaurant owned by this user
        try {
            Optional<Restaurant> restaurantOpt = restaurantRepository.findByRestaurant(user);
            if (restaurantOpt.isPresent()) {
                Restaurant restaurant = restaurantOpt.get();
                logger.info("Found existing restaurant: {} (ID: {})", restaurant.getRestaurantName(), restaurant.getId());
                return restaurant;
            } else {
                logger.info("No restaurant found for user: {}", user.getEmail());
            }
        } catch (Exception e) {
            logger.error("Error finding restaurant for user {}: {}", user.getEmail(), e.getMessage());
            e.printStackTrace();
        }
        
        // Create a restaurant if it doesn't exist
        try {
            logger.info("Creating new restaurant for user: {}", user.getEmail());
            Restaurant newRestaurant = new Restaurant();
            newRestaurant.setRestaurant(user);
            newRestaurant.setRestaurantName("Auto-created Restaurant for " + user.getName());
            newRestaurant.setType("General");
            newRestaurant.setOpen(true);
            
            Restaurant savedRestaurant = restaurantRepository.save(newRestaurant);
            logger.info("Created new restaurant with ID: {}", savedRestaurant.getId());
            return savedRestaurant;
        } catch (Exception e) {
            logger.error("Error creating restaurant for user {}: {}", user.getEmail(), e.getMessage());
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "Failed to create restaurant for user: " + e.getMessage());
        }
    }

    private float calculateTotalPrice(List<OrderItem> items) {
        return (float) items.stream()
                .mapToDouble(OrderItem::getPrice)
                .sum();
    }
} 