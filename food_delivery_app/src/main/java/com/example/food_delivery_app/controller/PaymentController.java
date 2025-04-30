package com.example.food_delivery_app.controller;

import com.example.food_delivery_app.model.Payment;
import com.example.food_delivery_app.model.User;
import com.example.food_delivery_app.request.PaymentRequest;
import com.example.food_delivery_app.service.PaymentService;
import com.example.food_delivery_app.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:4000"})
public class PaymentController {
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);
    private final PaymentService paymentService;
    private final UserService userService;

    public PaymentController(PaymentService paymentService, UserService userService) {
        this.paymentService = paymentService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<Payment> processPayment(
            @Valid @RequestBody PaymentRequest paymentRequest,
            @RequestHeader("Authorization") String jwtToken) {
        try {
            User user = userService.findUserByJwtToken(jwtToken);
            Payment payment = paymentService.processPayment(paymentRequest, user);
            logger.info("Payment processed successfully for user: {}", user.getEmail());
            return new ResponseEntity<>(payment, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Failed to process payment", e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<Payment> getPaymentById(
            @PathVariable Long paymentId,
            @RequestHeader("Authorization") String jwtToken) {
        try {
            User user = userService.findUserByJwtToken(jwtToken);
            Payment payment = paymentService.getPaymentById(paymentId, user);
            logger.info("Retrieved payment: {}", paymentId);
            return new ResponseEntity<>(payment, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Failed to retrieve payment: {}", paymentId, e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<Payment> getPaymentByOrderId(
            @PathVariable Long orderId,
            @RequestHeader("Authorization") String jwtToken) {
        try {
            User user = userService.findUserByJwtToken(jwtToken);
            Payment payment = paymentService.getPaymentByOrderId(orderId, user);
            logger.info("Retrieved payment for order: {}", orderId);
            return new ResponseEntity<>(payment, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Failed to retrieve payment for order: {}", orderId, e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/refund/{paymentId}")
    public ResponseEntity<Payment> refundPayment(
            @PathVariable Long paymentId,
            @RequestHeader("Authorization") String jwtToken) {
        try {
            User user = userService.findUserByJwtToken(jwtToken);
            Payment payment = paymentService.refundPayment(paymentId, user);
            logger.info("Payment refunded successfully: {}", paymentId);
            return new ResponseEntity<>(payment, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Failed to refund payment: {}", paymentId, e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
} 