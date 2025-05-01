// src/app/shop-cart/shop-cart.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CartService } from '../services/cart.service';
import { AuthService } from '../services/auth.service';
import { CartItem } from '../types/cart';
import { LoadingSpinnerComponent } from '../components/loading-spinner.component';

@Component({
  selector: 'app-shop-cart',
  standalone: true,
  imports: [RouterLink, CommonModule, FormsModule, LoadingSpinnerComponent],
  templateUrl: './shop-cart.component.html',
  styleUrl: './shop-cart.component.css'
})
export class ShopCartComponent implements OnInit {
  cartItems: CartItem[] = [];
  subtotal = 0;
  serviceFee = 1.00;
  deliveryFee = 2.05;
  totalPrice = 0;
  loading = false;
  error = '';

  constructor(
    private cartService: CartService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Check if user is logged in, redirect if not
    if (!this.authService.isLoggedIn) {
      this.router.navigate(['/login'], { 
        queryParams: { returnUrl: '/shopCart' } 
      });
      return;
    }

    // Load cart data initially
    this.loading = true;
    
    // Subscribe to cart items observable
    this.cartService.cartItems.subscribe({
      next: (items) => {
        this.cartItems = items;
        this.calculateTotals();
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading cart:', err);
        this.error = 'Failed to load cart items.';
        this.loading = false;
      }
    });

    // Fetch cart data from backend
    this.cartService.loadCartFromBackend();
  }

  // Calculate subtotal and total
  calculateTotals(): void {
    this.subtotal = this.cartService.getCartTotal();
    this.totalPrice = this.subtotal + this.serviceFee + this.deliveryFee;
  }

  // Update item quantity
  updateQuantity(foodId: number, event: Event): void {
    const quantity = parseInt((event.target as HTMLInputElement).value, 10);
    if (quantity > 0) {
      this.cartService.updateQuantity(foodId, quantity);
      this.calculateTotals();
    }
  }

  // Remove item from cart
  removeItem(foodId: number): void {
    this.cartService.removeFromCart(foodId);
    this.calculateTotals();
  }

  // Clear cart
  clearCart(): void {
    if (confirm('Are you sure you want to clear your cart?')) {
      this.cartService.clearCart();
      this.calculateTotals();
    }
  }

  // Proceed to checkout/payment
  proceedToPayment(): void {
    // Navigate to payment page using router
    this.router.navigate(['/payment']);
  }
}