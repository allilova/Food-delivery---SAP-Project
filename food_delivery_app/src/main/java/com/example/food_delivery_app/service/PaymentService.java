package com.example.food_delivery_app.service;

import com.example.food_delivery_app.model.Payment;
import com.example.food_delivery_app.model.User;
import com.example.food_delivery_app.request.PaymentRequest;

public interface PaymentService {
    Payment processPayment(PaymentRequest paymentRequest, User user);
    Payment getPaymentById(Long paymentId, User user);
    Payment getPaymentByOrderId(Long orderId, User user);
    Payment refundPayment(Long paymentId, User user);
} 