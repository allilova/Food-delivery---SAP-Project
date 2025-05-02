// src/app/supplier/orders/orders.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { OrderService } from '../../services/order.service';
import { AuthService } from '../../services/auth.service';
import { Order, OrderStatus } from '../../types/order';
import { LoadingSpinnerComponent } from '../../components/loading-spinner.component';
import { NotificationService } from '../../services/notification.service';

@Component({
  selector: 'app-orders',
  standalone: true,
  imports: [CommonModule, RouterLink, LoadingSpinnerComponent, FormsModule],
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
    private authService: AuthService,
    private notificationService: NotificationService
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
        this.router.navigate(['/supplier']);
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
        setTimeout(() => this.router.navigate(['/supplier']), 3000);
      }
    });
  }

  // Accept order (change status from PENDING to CONFIRMED)
  acceptOrder(): void {
    if (!this.order) return;
    
    this.loading = true;
    this.orderService.updateOrderStatus(this.order.id.toString(), OrderStatus.CONFIRMED).subscribe({
      next: (updatedOrder) => {
        this.order = updatedOrder;
        this.successMessage = 'Order accepted successfully.';
        this.notificationService.success('Order accepted successfully!');
        this.loading = false;
      },
      error: (err) => {
        console.error('Error accepting order:', err);
        this.error = 'Failed to accept order.';
        this.notificationService.error('Failed to accept order.');
        this.loading = false;
      }
    });
  }

  // Update order status to a specific status
  updateOrderStatus(status: OrderStatus): void {
    if (!this.order) return;
    
    this.loading = true;
    this.orderService.updateOrderStatus(this.order.id.toString(), status).subscribe({
      next: (updatedOrder) => {
        this.order = updatedOrder;
        this.successMessage = `Order status updated to ${status}.`;
        this.notificationService.success(`Order status updated to ${status}!`);
        this.loading = false;
      },
      error: (err) => {
        console.error('Error updating order status:', err);
        this.error = 'Failed to update order status.';
        this.notificationService.error('Failed to update order status.');
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
        this.notificationService.success('Order marked as delivered!');
        this.loading = false;
        
        // Navigate back to supplier dashboard after a short delay
        setTimeout(() => {
          this.router.navigate(['/supplier']);
        }, 2000);
      },
      error: (err) => {
        console.error('Error updating order status:', err);
        this.error = 'Failed to update order status.';
        this.notificationService.error('Failed to update order status.');
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
          this.notificationService.success('Order cancelled successfully.');
          this.loading = false;
          
          // Navigate back to supplier dashboard after a short delay
          setTimeout(() => {
            this.router.navigate(['/supplier']);
          }, 2000);
        },
        error: (err) => {
          console.error('Error cancelling order:', err);
          this.error = 'Failed to cancel order.';
          this.notificationService.error('Failed to cancel order.');
          this.loading = false;
        }
      });
    }
  }

  // Get appropriate button text based on current status
  getActionButtonText(): string {
    if (!this.order) return '';
    
    switch (this.order.status) {
      case OrderStatus.PENDING:
        return 'Accept Order';
      case OrderStatus.CONFIRMED:
        return 'Start Preparing';
      case OrderStatus.PREPARING:
        return 'Mark as Ready';
      case OrderStatus.READY:
        return 'Mark as Out for Delivery';
      case OrderStatus.OUT_FOR_DELIVERY:
        return 'Mark as Delivered';
      case OrderStatus.DELIVERED:
        return 'Order Completed';
      case OrderStatus.CANCELLED:
        return 'Order Cancelled';
      default:
        return 'Update Status';
    }
  }

  // Navigate back to supplier dashboard
  goBack(): void {
    this.router.navigate(['/supplier']);
  }
}