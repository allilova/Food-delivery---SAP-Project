package com.example.food_delivery_app.repository;

import com.example.food_delivery_app.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
@DataJpaTest
class OrderRepositoryTest {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RestaurantRepository restaurantRepository;
    @Test
    void findByCustomer() {

        User user = new User();
        user.setEmail("Sezer@gmail.com");
        user.setPassword("password");
        user.setName("Sezer");
        user.setPhoneNumber("1234567890");
        userRepository.save(user);

        Order order = new Order();
        order.setCustomer(user);
        orderRepository.save(order);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> ordersPage = orderRepository.findByCustomer(user, pageable);

        assertThat(ordersPage).isNotEmpty();
        assertThat(ordersPage.getContent()).hasSize(1);
        assertThat(ordersPage.getContent().get(0).getCustomer()).isEqualTo(user);
    }

    @Test
    void findOrderByIdAndCustomer() {

        User user = new User();
        user.setEmail("Sezer@gmail.com");
        user.setPassword("password");
        user.setName("Sezer");
        user.setPhoneNumber("1234567890");
        userRepository.save(user);

        Order order = new Order();
        order.setCustomer(user);
        orderRepository.save(order);

        Optional<Order> result = orderRepository.findByIdAndCustomer(order.getId(), user);

        assertThat(result).isPresent();
        assertThat(result.get().getCustomer()).isEqualTo(user);
        assertThat(result.get().getId()).isEqualTo(order.getId());
    }

    @Test
    void findOrderByRestaurant() {

        Restaurant restaurant = new Restaurant();
        restaurantRepository.save(restaurant);

        Order order = new Order();
        order.setRestaurant(restaurant);
        orderRepository.save(order);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> ordersPage = orderRepository.findByRestaurant(restaurant, pageable);

        assertThat(ordersPage).isNotEmpty();
        assertThat(ordersPage.getContent()).hasSize(1);
        assertThat(ordersPage.getContent().get(0).getRestaurant()).isEqualTo(restaurant);

    }

    @Test
    void findOrderByRestaurantAndOrderStatus() {
        Restaurant restaurant = new Restaurant();
        restaurantRepository.save(restaurant);

        Order order = new Order();
        order.setOrderStatus(OrderStatus.PREPARING);
        order.setRestaurant(restaurant);
        orderRepository.save(order);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> ordersPage = orderRepository.findByRestaurantAndOrderStatus(restaurant,OrderStatus.PREPARING, pageable);

        assertThat(ordersPage).isNotEmpty();
        assertThat(ordersPage.getContent()).hasSize(1);
        assertThat(ordersPage.getContent().get(0).getRestaurant()).isEqualTo(restaurant);
        assertThat(ordersPage.getContent().get(0).getOrderStatus()).isEqualTo(OrderStatus.PREPARING);

    }

    @Test
    void findOrderByIdAndRestaurant() {
        Restaurant restaurant = new Restaurant();
        restaurantRepository.save(restaurant);

        Order order = new Order();
        order.setRestaurant(restaurant);
        orderRepository.save(order);

        Optional<Order> result = orderRepository.findByIdAndRestaurant(order.getId(), restaurant);

        assertThat(result).isPresent();
        assertThat(result.get().getRestaurant()).isEqualTo(restaurant);
        assertThat(result.get().getId()).isEqualTo(order.getId());
    }

    @Test
    void findOrderByOrderStatus() {
        Order order = new Order();
        order.setOrderStatus(OrderStatus.PREPARING);
        orderRepository.save(order);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> ordersPage = orderRepository.findByOrderStatus(OrderStatus.PREPARING, pageable);

        assertThat(ordersPage).isNotEmpty();
        assertThat(ordersPage.getContent()).hasSize(1);
        assertThat(ordersPage.getContent().get(0).getOrderStatus()).isEqualTo(OrderStatus.PREPARING);
    }

    @Test
    void testFindOrderByOrderStatus() {
        Order order1 = new Order();
        order1.setOrderStatus(OrderStatus.PREPARING);
        orderRepository.save(order1);

        List<Order> preparingOrders = orderRepository.findByOrderStatus(OrderStatus.PREPARING);

        assertThat(preparingOrders).hasSize(1);
        assertThat(preparingOrders).allMatch(order -> order.getOrderStatus() == OrderStatus.PREPARING);
    }
}