package com.example.food_delivery_app.service;

import com.example.food_delivery_app.exception.PaymentNotFoundException;
import com.example.food_delivery_app.model.*;
import com.example.food_delivery_app.repository.OrderRepository;
import com.example.food_delivery_app.repository.PaymentRepository;
import com.example.food_delivery_app.request.PaymentRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public Payment processPayment(PaymentRequest paymentRequest, User user) {
        Order order = orderRepository.findById(paymentRequest.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentType(PaymentType.valueOf(paymentRequest.getPaymentMethod()));
        payment.setAmount(paymentRequest.getAmount());
        payment.setUser(user);
        payment.setPaymentDate(new Date());
        payment.setPaymentStatus(PaymentStatus.PAID);
        
        return paymentRepository.save(payment);
    }

    @Override
    public Payment getPaymentById(Long paymentId, User user) {
        return paymentRepository.findByIdAndUser(paymentId, user)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with ID: " + paymentId));
    }

    @Override
    public Payment getPaymentByOrderId(Long orderId, User user) {
        return paymentRepository.findByOrder_IdAndUser(orderId, user)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found for order ID: " + orderId));
    }

    @Override
    public Payment refundPayment(Long paymentId, User user) {
        Payment payment = getPaymentById(paymentId, user);
        payment.setPaymentStatus(PaymentStatus.FAILED);
        return paymentRepository.save(payment);
    }
} 