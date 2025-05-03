package com.example.food_delivery_app.repository;

import com.example.food_delivery_app.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
class PaymentRepositoryTest {
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderRepository orderRepository;

    @Test
    void findPaymentByIdAndUser() {
        User user = new User();
        user.setEmail("Sezer@gmail.com");
        user.setPassword("password");
        user.setName("Sezer");
        user.setPhoneNumber("1234567890");
        userRepository.save(user);

        Payment payment = new Payment();
        payment.setUser(user);
        paymentRepository.save(payment);

        Optional<Payment> foundPayment = paymentRepository.findByIdAndUser(payment.getId(), user);

        assertThat(foundPayment).isPresent();
        assertThat(foundPayment.get().getId()).isEqualTo(payment.getId());
        assertThat(foundPayment.get().getUser()).isEqualTo(user);
    }

    @Test
    void findPaymentByOrder_IdAndUser() {
        User user = new User();
        user.setEmail("Sezer@gmail.com");
        user.setPassword("password");
        user.setName("Sezer");
        user.setPhoneNumber("1234567890");
        userRepository.save(user);

        Order order = new Order();
        orderRepository.save(order);

        Payment payment = new Payment();
        payment.setUser(user);
        payment.setOrder(order);
        paymentRepository.save(payment);

        Optional<Payment> foundPayment = paymentRepository.findByOrder_IdAndUser(order.getId(), user);

        assertThat(foundPayment).isPresent();
        assertThat(foundPayment.get().getOrder().getId()).isEqualTo(order.getId());
        assertThat(foundPayment.get().getUser()).isEqualTo(user);
    }
}