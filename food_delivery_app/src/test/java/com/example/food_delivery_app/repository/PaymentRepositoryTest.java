package com.example.food_delivery_app.repository;

import com.example.food_delivery_app.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:mysql://localhost:3306/food_delivery_test?createDatabaseIfNotExist=true",
    "spring.datasource.username=root",
    "spring.datasource.password=root",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void ItShouldFindPaymentByOrderId() {
        // Create and persist a User
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setRole(USER_ROLE.ROLE_CUSTOMER);
        user.setPhoneNumber("1234567890");
        user.setAddress("123 Test St");
        entityManager.persist(user);

        // Create and persist a Restaurant
        Restaurant restaurant = new Restaurant();
        restaurant.setRestaurantName("Test Restaurant");
        entityManager.persist(restaurant);

        // Create and persist an Order
        Order order = new Order();
        order.setUser(user);
        order.setRestaurant(restaurant);
        order.setTotalAmount(50.00);
        order.setStatus(Order.OrderStatus.PENDING);
        order.setDeliveryAddress("123 Test St");
        order.setContactNumber("1234567890");
        order.setItemsQuantity(1);
        order.setTotalPrice(50.00f);
        entityManager.persist(order);

        // Create and persist a Payment
        Payment payment = new Payment();
        payment.setUser(user);
        payment.setOrder(order);
        payment.setAmount(50.00);
        payment.setStatus(Payment.PaymentStatus.COMPLETED);
        payment.setPaymentMethod("CREDIT_CARD");
        payment.setTransactionId("TEST_TRANS_123");
        entityManager.persist(payment);

<<<<<<< HEAD
        // Test the repository method
        Optional<Payment> foundPayment = paymentRepository.findByOrder_Id(order.getId());

        // Assertions
        assertThat(foundPayment).isPresent();
        assertThat(foundPayment.get().getAmount()).isEqualTo(50.00);
=======
        paymentRepository.save(payment);

        //Optional<Payment> foundPayment = paymentRepository.findByOrderId(order.getOrderID());

        //assertThat(foundPayment).isPresent();
        //assertThat(foundPayment.get().getPaymentAmount()).isEqualTo(20.00f);
<<<<<<< HEAD
>>>>>>> 3b97e188d54bd0a20c3391ce1ad1a3d3dc0fb7ca
=======
>>>>>>> 1db6b8e08a5a54e3b88a36b81c018b3860e2aaf5
    }
}