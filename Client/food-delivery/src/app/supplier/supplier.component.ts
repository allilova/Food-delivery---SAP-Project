// src/app/supplier/supplier.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { OrderService } from '../services/order.service';
import { AuthService } from '../services/auth.service';
import { Order, OrderStatus } from '../types/order';
import { LoadingSpinnerComponent } from '../components/loading-spinner.component';

@Component({
  selector: 'app-supplier',
  standalone: true,
  imports: [RouterLink, CommonModule, LoadingSpinnerComponent],
  templateUrl: './supplier.component.html',
  styleUrl: './supplier.component.css'
})
export class SupplierComponent implements OnInit {
  newOrders: Order[] = [];
  activeOrders: Order[] = [];
  completedOrders: Order[] = [];
  loading = {
    newOrders: false,
    activeOrders: false,
    completedOrders: false
  };
  error = {
    newOrders: '',
    activeOrders: '',
    completedOrders: ''
  };
  OrderStatus = OrderStatus; // Make enum available in template

  constructor(
    private orderService: OrderService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    // Check if user is logged in and is a restaurant owner
    if (!this.authService.isLoggedIn || this.authService.userRole !== 'ROLE_RESTAURANT') {
      // Redirect or show message
      return;
    }

    this.loadNewOrders();
    this.loadActiveOrders();
    this.loadCompletedOrders();
  }

  loadNewOrders(): void {
    this.loading.newOrders = true;
    this.orderService.getOrdersByStatus(OrderStatus.PENDING).subscribe({
      next: (orders) => {
        this.newOrders = orders;
        this.loading.newOrders = false;
      },
      error: (err) => {
        console.error('Error loading new orders:', err);
        this.error.newOrders = 'Failed to load new orders.';
        this.loading.newOrders = false;
      }
    });
  }

  loadActiveOrders(): void {
    this.loading.activeOrders = true;
    this.orderService.getOrdersByStatus(OrderStatus.IN_PROGRESS).subscribe({
      next: (orders) => {
        this.activeOrders = orders;
        this.loading.activeOrders = false;
      },
      error: (err) => {
        console.error('Error loading active orders:', err);
        this.error.activeOrders = 'Failed to load active orders.';
        this.loading.activeOrders = false;
      }
    });
  }

  loadCompletedOrders(): void {
    this.loading.completedOrders = true;
    this.orderService.getOrdersByStatus(OrderStatus.DELIVERED).subscribe({
      next: (orders) => {
        this.completedOrders = orders;
        this.loading.completedOrders = false;
      },
      error: (err) => {
        console.error('Error loading completed orders:', err);
        this.error.completedOrders = 'Failed to load completed orders.';
        this.loading.completedOrders = false;
      }
    });
  }

  // Accept order (change status from PENDING to IN_PROGRESS)
  acceptOrder(orderId: string): void {
    this.orderService.updateOrderStatus(orderId, OrderStatus.IN_PROGRESS).subscribe({
      next: () => {
        // Move order from new to active
        const order = this.newOrders.find(o => o.id.toString() === orderId);
        if (order) {
          order.orderStatus = OrderStatus.IN_PROGRESS;
          this.newOrders = this.newOrders.filter(o => o.id.toString() !== orderId);
          this.activeOrders.push(order);
        }
      },
      error: (err) => {
        console.error('Error accepting order:', err);
        // Show error message
      }
    });
  }

  // Mark order as ready for delivery
  markOrderAsReady(orderId: string): void {
    // In a real app, you might have a specific status for this
    // For now, we'll keep it in the ACTIVE orders but update UI
    
    // Update UI to show that the order is ready for pickup
    const orderIndex = this.activeOrders.findIndex(o => o.id.toString() === orderId);
    if (orderIndex !== -1) {
      // Update some property to indicate it's ready
      // This is just for UI purposes in this example
      this.activeOrders[orderIndex] = {
        ...this.activeOrders[orderIndex],
        // Add a custom property or update existing one
      };
    }
  }

  // Update order status
  updateOrderStatus(orderId: string, status: OrderStatus): void {
    this.orderService.updateOrderStatus(orderId, status).subscribe({
      next: () => {
        // Update lists based on new status
        if (status === OrderStatus.DELIVERED) {
          const order = this.activeOrders.find(o => o.id.toString() === orderId);
          if (order) {
            order.orderStatus = OrderStatus.DELIVERED;
            this.activeOrders = this.activeOrders.filter(o => o.id.toString() !== orderId);
            this.completedOrders.push(order);
          }
        }
      },
      error: (err) => {
        console.error('Error updating order status:', err);
        // Show error message
      }
    });
  }
}