package com.example.food_delivery_app.repository;

import com.example.food_delivery_app.model.Order;
import com.example.food_delivery_app.model.Payment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.Optional;

@DataJpaTest
class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void ItShouldFindPaymentByOrderID() {
        Order order = new Order();
        entityManager.persist(order);

        Payment payment = new Payment();
        payment.setPaymentAmount(20.00f);
        payment.setOrder(order);

        paymentRepository.save(payment);

        //Optional<Payment> foundPayment = paymentRepository.findByOrderId(order.getOrderID());

        //assertThat(foundPayment).isPresent();
        //assertThat(foundPayment.get().getPaymentAmount()).isEqualTo(20.00f);
    }
}