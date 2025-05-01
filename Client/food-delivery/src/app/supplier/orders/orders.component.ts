// src/app/supplier/orders/orders.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { OrderService } from '../../services/order.service';
import { AuthService } from '../../services/auth.service';
import { Order, OrderStatus } from '../../types/order';
import { LoadingSpinnerComponent } from '../../components/loading-spinner.component';

@Component({
  selector: 'app-orders',
  standalone: true,
  imports: [CommonModule, RouterLink, LoadingSpinnerComponent],
  templateUrl: './orders.component.html',
  styleUrl: './orders.component.css'
})
export class OrdersComponent implements OnInit {
  order: Order | null = null;
  orderId: string = '';
  loading = false;
  error = '';
  successMessage = '';
  OrderStatus = OrderStatus; // Make enum available in template

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private orderService: OrderService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    // Check if user is logged in and is a restaurant owner
    if (!this.authService.isLoggedIn || this.authService.userRole !== 'ROLE_RESTAURANT') {
      this.router.navigate(['/login']);
      return;
    }

    // Get order ID from query parameter or route
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) {
        this.orderId = id;
        this.loadOrderDetails(id);
      } else {
        this.error = 'No order ID provided.';
      }
    });
  }

  loadOrderDetails(orderId: string): void {
    this.loading = true;
    this.orderService.getOrderById(orderId).subscribe({
      next: (order) => {
        this.order = order;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading order details:', err);
        this.error = 'Failed to load order details.';
        this.loading = false;
      }
    });
  }

  // Accept order (change status from PENDING to IN_PROGRESS)
  acceptOrder(): void {
    if (!this.order) return;
    
    this.loading = true;
    this.orderService.updateOrderStatus(this.order.id.toString(), OrderStatus.IN_PROGRESS).subscribe({
      next: (updatedOrder) => {
        this.order = updatedOrder;
        this.successMessage = 'Order accepted successfully.';
        this.loading = false;
      },
      error: (err) => {
        console.error('Error accepting order:', err);
        this.error = 'Failed to accept order.';
        this.loading = false;
      }
    });
  }

  // Mark order as delivered
  markAsDelivered(): void {
    if (!this.order) return;
    
    this.loading = true;
    this.orderService.updateOrderStatus(this.order.id.toString(), OrderStatus.DELIVERED).subscribe({
      next: (updatedOrder) => {
        this.order = updatedOrder;
        this.successMessage = 'Order marked as delivered.';
        this.loading = false;
      },
      error: (err) => {
        console.error('Error updating order status:', err);
        this.error = 'Failed to update order status.';
        this.loading = false;
      }
    });
  }

  // Cancel order
  cancelOrder(): void {
    if (!this.order) return;
    
    if (confirm('Are you sure you want to cancel this order?')) {
      this.loading = true;
      this.orderService.updateOrderStatus(this.order.id.toString(), OrderStatus.CANCELLED).subscribe({
        next: (updatedOrder) => {
          this.order = updatedOrder;
          this.successMessage = 'Order cancelled successfully.';
          this.loading = false;
        },
        error: (err) => {
          console.error('Error cancelling order:', err);
          this.error = 'Failed to cancel order.';
          this.loading = false;
        }
      });
    }
  }

  // Get appropriate button text based on current status
  getActionButtonText(): string {
    if (!this.order) return '';
    
    switch (this.order.orderStatus) {
      case OrderStatus.PENDING:
        return 'Accept Order';
      case OrderStatus.IN_PROGRESS:
        return 'Mark as Delivered';
      case OrderStatus.DELIVERED:
        return 'Order Completed';
      case OrderStatus.CANCELLED:
        return 'Order Cancelled';
      default:
        return 'Update Status';
    }
  }

  // Perform action based on current status
  performAction(): void {
    if (!this.order) return;
    
    switch (this.order.orderStatus) {
      case OrderStatus.PENDING:
        this.acceptOrder();
        break;
      case OrderStatus.IN_PROGRESS:
        this.markAsDelivered();
        break;
      // No actions for completed or cancelled orders
    }
  }

  // Navigate back to supplier dashboard
  goBack(): void {
    this.router.navigate(['/supplier']);
  }
}