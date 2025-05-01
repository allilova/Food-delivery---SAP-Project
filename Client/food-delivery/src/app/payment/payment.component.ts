// src/app/payment/payment.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CartService } from '../services/cart.service';
import { OrderService } from '../services/order.service';
import { AuthService } from '../services/auth.service';
import { LoadingSpinnerComponent } from '../components/loading-spinner.component';

@Component({
  selector: 'app-payment',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, LoadingSpinnerComponent],
  templateUrl: './payment.component.html',
  styleUrl: './payment.component.css'
})
export class PaymentComponent implements OnInit {
  paymentForm!: FormGroup;
  paymentMethod: 'CARD' | 'CASH' = 'CARD';
  cartItems: any[] = [];
  subtotal = 0;
  serviceFee = 1.00;
  deliveryFee = 2.05;
  totalPrice = 0;
  loading = false;
  error = '';
  successMessage = '';

  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private cartService: CartService,
    private orderService: OrderService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    // Check if user is logged in, redirect if not
    if (!this.authService.isLoggedIn) {
      this.router.navigate(['/login'], { 
        queryParams: { returnUrl: '/payment' } 
      });
      return;
    }

    // Initialize form
    this.paymentForm = this.formBuilder.group({
      paymentMethod: ['CARD', Validators.required],
      deliveryAddress: ['', Validators.required],
      cardName: ['', Validators.required],
      cardNumber: ['', [Validators.required, Validators.pattern('^[0-9]{16}$')]],
      expiryDate: ['', [Validators.required, Validators.pattern('^(0[1-9]|1[0-2])\/[0-9]{2}$')]],
      cvv: ['', [Validators.required, Validators.pattern('^[0-9]{3,4}$')]]
    });

    // Load cart items
    this.cartService.cartItems.subscribe(items => {
      this.cartItems = items;
      this.calculateTotal();
    });

    // Watch for payment method changes
    this.paymentForm.get('paymentMethod')?.valueChanges.subscribe(value => {
      this.paymentMethod = value;
      
      // Conditionally require card fields
      const cardControls = ['cardName', 'cardNumber', 'expiryDate', 'cvv'];
      
      if (value === 'CARD') {
        cardControls.forEach(control => {
          this.paymentForm.get(control)?.setValidators([Validators.required]);
          this.paymentForm.get(control)?.updateValueAndValidity();
        });
      } else {
        cardControls.forEach(control => {
          this.paymentForm.get(control)?.clearValidators();
          this.paymentForm.get(control)?.updateValueAndValidity();
        });
      }
    });
  }

  // Calculate total amount from cart items
  calculateTotal(): void {
    this.subtotal = this.cartService.getCartTotal();
    // Add service fee and delivery fee
    this.totalPrice = this.subtotal + this.serviceFee + this.deliveryFee;
  }

  // Process payment and create order
  onSubmit(): void {
    if (this.paymentForm.invalid && this.paymentMethod === 'CARD') {
      return;
    }

    this.loading = true;
    
    // Prepare order data
    const orderData = {
      items: this.cartItems.map(item => ({
        foodId: item.food.id,
        quantity: item.quantity
      })),
      deliveryAddress: this.paymentForm.get('deliveryAddress')?.value,
      paymentMethod: this.paymentMethod
    };

    // Create order via API
    this.orderService.createOrder(orderData).subscribe({
      next: (response) => {
        this.loading = false;
        this.successMessage = 'Order placed successfully!';
        
        // Clear cart after successful order
        this.cartService.clearCart();
        
        // Redirect to confirmation page or order history after a delay
        setTimeout(() => {
          this.router.navigate(['/profile']);
        }, 2000);
      },
      error: (err) => {
        this.loading = false;
        this.error = err?.error?.message || 'Failed to process payment. Please try again.';
        console.error('Payment error:', err);
      }
    });
  }

  // Convenience getter for form fields
  get f() { return this.paymentForm.controls; }
}