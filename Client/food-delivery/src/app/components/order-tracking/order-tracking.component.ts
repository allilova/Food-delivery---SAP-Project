// src/app/components/order-tracking/order-tracking.component.ts
import { Component, Input, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { interval, Subscription } from 'rxjs';
import { OrderService } from '../../services/order.service';
import { Order, OrderStatus } from '../../types/order';
import { LoadingSpinnerComponent } from '../loading-spinner.component';

@Component({
  selector: 'app-order-tracking',
  standalone: true,
  imports: [CommonModule, LoadingSpinnerComponent],
  template: `
    <div class="order-tracking-container">
      <h3>Order Tracking</h3>
      
      <div *ngIf="loading" class="loading-container">
        <app-loading-spinner></app-loading-spinner>
        <p>Loading order status...</p>
      </div>
      
      <div *ngIf="error" class="error-message">
        {{ error }}
      </div>
      
      <div *ngIf="!loading && !error" class="order-status-container">
        <div class="order-info">
          <p class="order-number">Order #{{ orderNumber }}</p>
          <p class="order-date">Placed on {{ orderDate | date:'medium' }}</p>
          <p class="order-total">Total: {{ orderTotal | currency }}</p>
        </div>
        
        <div class="tracking-steps">
          <div class="tracking-step" 
               *ngFor="let step of steps; let i = index" 
               [ngClass]="{
                 'completed': currentStepIndex > i,
                 'active': currentStepIndex === i,
                 'upcoming': currentStepIndex < i
               }">
            <div class="step-icon">
              <i [class]="step.icon"></i>
            </div>
            <div class="step-details">
              <h4>{{ step.title }}</h4>
              <p *ngIf="step.description">{{ step.description }}</p>
              <p *ngIf="step.time && currentStepIndex > i" class="step-time">
                {{ step.time | date:'shortTime' }}
              </p>
              <p *ngIf="currentStepIndex === i && estimatedTime" class="estimated-time">
                Estimated: {{ estimatedTime }} min
              </p>
            </div>
          </div>
        </div>
        
        <div *ngIf="isDelivered" class="delivery-confirmation">
          <h4>Order Delivered!</h4>
          <p>Your order was delivered on {{ deliveryTime | date:'medium' }}</p>
          <button (click)="rateOrder()" class="rate-button">Rate Your Experience</button>
        </div>
        
        <div *ngIf="isActive" class="estimated-delivery">
          <p>Estimated Delivery Time: {{ estimatedDeliveryTime | date:'shortTime' }}</p>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .order-tracking-container {
      background: #FFDBDB;
      border-radius: 8px;
      box-shadow: 0 2px 10px rgba(0,0,0,0.1);
      padding: 20px;
      margin-bottom: 30px;
    }
    
    h3 {
      margin-top: 0;
      color: rgb(59, 4, 4);
      font-size: 1.5rem;
      font-weight: 600;
      margin-bottom: 20px;
      border-bottom: 1px solid #eee;
      padding-bottom: 10px;
    }
    
    .order-info {
      margin-bottom: 24px;
    }
  
    .order-number {
      font-size: 1.1rem;
      font-weight: 600;
      margin-bottom: 5px;
    }
    
    .tracking-steps {
      display: flex;
      flex-direction: column;
      gap: 30px;
      padding: 20px 0;
      position: relative;
    }
    
    .tracking-steps:before {
      content: '';
      position: absolute;
      top: 0;
      bottom: 0;
      left: 18px;
      width: 2px;
      background-color: #FFDBDB;
      z-index: 1;
    }
    
    .tracking-step {
      display: flex;
      position: relative;
      z-index: 2;
    }
    
    .step-icon {
      width: 36px;
      height: 36px;
      border-radius: 50%;
      background-color:  #FFDBDB;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 15px;
      z-index: 2;
    }
    
    .step-icon i {
      color: rgb(59, 4, 4);
      font-size: 16px;
    }
    
    .step-details {
      flex-grow: 1;
    }
    
    .step-details h4 {
      margin: 0 0 5px;
      font-size: 1rem;
      font-weight: 600;
    }
    
    .step-details p {
      margin: 0;
      color: rgb(59, 4, 4);
      font-size: 0.9rem;
    }
    
    .step-time {
      color: rgb(59, 4, 4);
      font-size: 0.8rem !important;
      margin-top: 5px !important;
    }
    
    .estimated-time {
      color:rgb(59, 4, 4);
      font-weight: 500;
    }
    
    /* Step States */
    .tracking-step.completed .step-icon {
      background-color: #4caf50;
    }
    
    .tracking-step.active .step-icon {
      background-color: #2196f3;
      box-shadow: 0 0 0 4px rgba(33, 150, 243, 0.2);
    }
    
    .tracking-step.upcoming .step-icon {
      background-color: #e0e0e0;
    }
    
    .tracking-step.completed .step-details h4,
    .tracking-step.active .step-details h4 {
      color: rgb(59, 4, 4);
    }
    
    .tracking-step.upcoming .step-details h4,
    .tracking-step.upcoming .step-details p {
      color: rgb(59, 4, 4);
    }
    
    /* Delivery confirmation */
    .delivery-confirmation {
      margin-top: 30px;
      padding: 15px;
      background-color: #e8f5e9;
      border-radius: 6px;
      text-align: center;
    }
    
    .delivery-confirmation h4 {
      color: #2e7d32;
      margin-top: 0;
    }
    
    .rate-button {
      background-color: rgb(59, 4, 4);
      color: white;
      border: none;
      padding: 8px 16px;
      border-radius: 4px;
      font-weight: 500;
      cursor: pointer;
      margin-top: 10px;
    }
    
    .rate-button:hover {
      background-color: rgb(59, 4, 4);
    }
    
    /* Estimated delivery */
    .estimated-delivery {
      margin-top: 20px;
      font-weight: 500;
      color: rgb(59, 4, 4)
      text-align: center;
    }
    
    /* Loading and error */
    .loading-container {
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: 30px 0;
    }
    
    .error-message {
      color: #d32f2f;
      background-color: #ffebee;
      padding: 15px;
      border-radius: 6px;
      margin: 15px 0;
    }
  `]
})
export class OrderTrackingComponent implements OnInit, OnDestroy {
  @Input() orderId!: string;
  
  loading = false;
  error = '';
  
  // Order details
  orderNumber = '';
  orderDate = new Date();
  orderTotal = 0;
  orderStatus = OrderStatus.PENDING;
  
  // Tracking steps
  steps = [
    { title: 'Order Placed', description: 'Your order has been received', icon: 'fa fa-receipt', time: null as Date | null },
    { title: 'Order Confirmed', description: 'Restaurant has confirmed your order', icon: 'fa fa-check-circle', time: null as Date | null },
    { title: 'Preparing', description: 'Your food is being prepared', icon: 'fa fa-utensils', time: null as Date | null },
    { title: 'Ready for Pickup', description: 'Your order is ready for delivery', icon: 'fa fa-shopping-bag', time: null as Date | null },
    { title: 'Out for Delivery', description: 'Driver is on the way', icon: 'fa fa-truck', time: null as Date | null },
    { title: 'Delivered', description: 'Enjoy your meal!', icon: 'fa fa-home', time: null as Date | null }
  ];
  
  currentStepIndex = 0;
  estimatedTime: number | null = null;
  estimatedDeliveryTime: Date | null = null;
  deliveryTime: Date | null = null;
  
  // State flags
  get isDelivered(): boolean {
    return this.orderStatus === OrderStatus.DELIVERED;
  }
  
  get isActive(): boolean {
    return !this.isDelivered && this.orderStatus !== OrderStatus.CANCELLED;
  }
  
  // Auto-refresh
  private refreshInterval = 60000; // 1 minute
  private refreshSubscription: Subscription | null = null;
  
  constructor(private orderService: OrderService) { }
  
  ngOnInit(): void {
    this.loadOrderDetails();
    
    // Set up auto-refresh if order is active
    if (this.isActive) {
      this.refreshSubscription = interval(this.refreshInterval).subscribe(() => {
        this.loadOrderDetails();
      });
    }
  }
  
  ngOnDestroy(): void {
    if (this.refreshSubscription) {
      this.refreshSubscription.unsubscribe();
    }
  }
  
  loadOrderDetails(): void {
    if (!this.orderId) {
      this.error = 'Order ID is required';
      return;
    }
    
    this.loading = true;
    
    this.orderService.getOrderById(this.orderId).subscribe({
      next: (order) => {
        this.updateOrderDetails(order);
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading order:', err);
        this.error = 'Failed to load order details. Please try again.';
        this.loading = false;
      }
    });
  }
  
  updateOrderDetails(order: Order): void {
    this.orderNumber = order.id;
    this.orderDate = new Date(order.orderDate);
    this.orderTotal = order.totalAmount;
    this.orderStatus = order.status;
    
    // Update step index based on order status
    this.updateStepIndex(order.status);
    
    // Update step timestamps if available
    if (order.statusHistory?.length) {
      this.updateStepTimes(order.statusHistory);
    }
    
    // Set delivery time for completed orders
    if (this.isDelivered && order.deliveryTime) {
      this.deliveryTime = new Date(order.deliveryTime);
    }
    
    // Calculate estimated time
    this.calculateEstimatedTimes(order);
  }
  
  updateStepIndex(status: OrderStatus): void {
    switch (status) {
      case OrderStatus.PENDING:
        this.currentStepIndex = 0;
        break;
      case OrderStatus.CONFIRMED:
        this.currentStepIndex = 1;
        break;
      case OrderStatus.PREPARING:
        this.currentStepIndex = 2;
        break;
      case OrderStatus.READY:
        this.currentStepIndex = 3;
        break;
      case OrderStatus.OUT_FOR_DELIVERY:
        this.currentStepIndex = 4;
        break;
      case OrderStatus.DELIVERED:
        this.currentStepIndex = 5;
        break;
      case OrderStatus.CANCELLED:
        this.error = 'This order has been cancelled.';
        break;
      default:
        this.currentStepIndex = 0;
    }
  }
  
  updateStepTimes(statusHistory: any[]): void {
    // Simplified implementation - in a real app, you'd map status updates to steps
    statusHistory.forEach(update => {
      const stepIndex = this.getStepIndexForStatus(update.status);
      if (stepIndex !== -1 && update.timestamp) {
        this.steps[stepIndex].time = new Date(update.timestamp);
      }
    });
  }
  
  getStepIndexForStatus(status: string): number {
    switch (status) {
      case OrderStatus.PENDING: return 0;
      case OrderStatus.CONFIRMED: return 1;
      case OrderStatus.PREPARING: return 2;
      case OrderStatus.READY: return 3;
      case OrderStatus.OUT_FOR_DELIVERY: return 4;
      case OrderStatus.DELIVERED: return 5;
      default: return -1;
    }
  }
  
  calculateEstimatedTimes(order: Order): void {
    if (this.isDelivered || this.orderStatus === OrderStatus.CANCELLED) {
      this.estimatedTime = null;
      this.estimatedDeliveryTime = null;
      return;
    }
    
    // This would normally come from the backend
    // Here we're just simulating it
    if (order.estimatedDeliveryTime) {
      this.estimatedDeliveryTime = new Date(order.estimatedDeliveryTime);
      
      // Calculate remaining time in minutes
      const now = new Date();
      const diffMs = this.estimatedDeliveryTime.getTime() - now.getTime();
      this.estimatedTime = Math.max(0, Math.round(diffMs / 60000)); // Convert ms to minutes
    } else {
      // Default estimated times based on status
      switch (this.orderStatus) {
        case OrderStatus.PENDING:
          this.estimatedTime = 45;
          break;
        case OrderStatus.CONFIRMED:
          this.estimatedTime = 35;
          break;
        case OrderStatus.PREPARING:
          this.estimatedTime = 25;
          break;
        case OrderStatus.READY:
          this.estimatedTime = 15;
          break;
        case OrderStatus.OUT_FOR_DELIVERY:
          this.estimatedTime = 10;
          break;
        default:
          this.estimatedTime = null;
      }
      
      if (this.estimatedTime) {
        const deliveryDate = new Date();
        deliveryDate.setMinutes(deliveryDate.getMinutes() + this.estimatedTime);
        this.estimatedDeliveryTime = deliveryDate;
      }
    }
  }
  
  rateOrder(): void {
    // This would navigate to a rating page or open a rating modal
    console.log('Rate order clicked for order:', this.orderId);
  }
}