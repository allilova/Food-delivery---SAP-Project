package com.example.food_delivery_app.service.impl;

import com.example.food_delivery_app.exception.ResourceNotFoundException;
import com.example.food_delivery_app.model.Order;
import com.example.food_delivery_app.model.Payment;
import com.example.food_delivery_app.model.User;
import com.example.food_delivery_app.repository.OrderRepository;
import com.example.food_delivery_app.repository.PaymentRepository;
import com.example.food_delivery_app.request.PaymentRequest;
import com.example.food_delivery_app.service.PaymentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository, OrderRepository orderRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public Payment processPayment(PaymentRequest paymentRequest, User user) {
        Order order = orderRepository.findById(paymentRequest.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Order not found");
        }

        // TODO: Implement actual payment processing logic here
        // This is a placeholder for demonstration purposes
        Payment payment = new Payment();
        payment.setUser(user);
        payment.setOrder(order);
        payment.setAmount(order.getTotalAmount());
        payment.setStatus(Payment.PaymentStatus.COMPLETED);
        payment.setPaymentMethod(paymentRequest.getPaymentMethod());
        payment.setTransactionId(generateTransactionId());

        return paymentRepository.save(payment);
    }

    @Override
    public Payment getPaymentById(Long paymentId, User user) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        if (!payment.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Payment not found");
        }

        return payment;
    }

    @Override
    public Payment getPaymentByOrderId(Long orderId, User user) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Order not found");
        }

        return paymentRepository.findByOrder(order)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
    }

    @Override
    public Payment refundPayment(Long paymentId, User user) {
        Payment payment = getPaymentById(paymentId, user);

        if (payment.getStatus() != Payment.PaymentStatus.COMPLETED) {
            throw new IllegalStateException("Only completed payments can be refunded");
        }

        // TODO: Implement actual refund processing logic here
        // This is a placeholder for demonstration purposes
        payment.setStatus(Payment.PaymentStatus.REFUNDED);
        return paymentRepository.save(payment);
    }

    private String generateTransactionId() {
        // TODO: Implement proper transaction ID generation
        return "TXN" + System.currentTimeMillis();
    }
} 