// src/app/shop-cart/shop-cart.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CartService } from '../services/cart.service';
import { AuthService } from '../services/auth.service';
import { CartItem } from '../types/cart';
import { LoadingSpinnerComponent } from '../components/loading-spinner.component';
import { DialogService } from '../services/dialog.service';
import { NotificationService } from '../services/notification.service';

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
    private router: Router,
    private dialogService: DialogService,
    private notificationService: NotificationService
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
        console.log('Cart items updated:', items);
        this.cartItems = items;
        this.calculateTotals();
        this.loading = false;
        
        // Check if cart is empty after loading
        if (this.cartItems.length === 0) {
          console.log('Cart is empty, checking localStorage directly');
          this.loadFromLocalStorage();
        }
      },
      error: (err) => {
        console.error('Error loading cart:', err);
        this.error = 'Failed to load cart items.';
        this.loading = false;
        
        // Try loading from localStorage as fallback
        this.loadFromLocalStorage();
      }
    });
  }
  
  // Fallback method to load cart directly from localStorage if service fails
  private loadFromLocalStorage(): void {
    try {
      const savedCart = localStorage.getItem('cart');
      if (savedCart) {
        const parsedCart = JSON.parse(savedCart);
        if (Array.isArray(parsedCart) && parsedCart.length > 0) {
          console.log('Found cart in localStorage:', parsedCart);
          this.cartItems = parsedCart;
          this.calculateTotals();
          this.error = ''; // Clear any previous errors
        } else {
          console.log('Cart in localStorage is empty or invalid');
        }
      } else {
        console.log('No cart found in localStorage');
      }
    } catch (e) {
      console.error('Error parsing cart from localStorage:', e);
      this.error = 'Failed to retrieve cart data. Please try again.';
    }
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
  removeItem(foodId: number, itemName: string = 'this item'): void {
    const dialogRef = this.dialogService.confirm({
      title: 'Remove Item',
      message: `Are you sure you want to remove ${itemName} from your cart?`,
      confirmText: 'Remove',
      cancelText: 'Keep',
      type: 'warning'
    });
    
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.cartService.removeFromCart(foodId);
        this.calculateTotals();
        this.notificationService.info(`${itemName} has been removed from your cart.`);
      }
    });
  }

  // Clear cart
  clearCart(): void {
    const dialogRef = this.dialogService.confirm({
      title: 'Clear Cart',
      message: 'Are you sure you want to remove all items from your cart?',
      confirmText: 'Clear Cart',
      cancelText: 'Cancel',
      type: 'danger'
    });
    
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.cartService.clearCart();
        this.calculateTotals();
        this.notificationService.success('Your cart has been cleared.');
      }
    });
  }

  // Proceed to checkout/payment
  proceedToPayment(): void {
    // Navigate to payment page using router
    this.router.navigate(['/payment']);
  }
}