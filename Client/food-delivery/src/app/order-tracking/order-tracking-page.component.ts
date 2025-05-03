// src/app/order-tracking/order-tracking-page.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { OrderTrackingComponent } from '../components/order-tracking/order-tracking.component';
import { OrderService } from '../services/order.service';
import { LoadingSpinnerComponent } from '../components/loading-spinner.component';

@Component({
  selector: 'app-order-tracking-page',
  standalone: true,
  imports: [
    CommonModule, 
    RouterLink,
    ReactiveFormsModule,
    OrderTrackingComponent,
    LoadingSpinnerComponent
  ],
  template: `
    <div class="order-tracking-page">
      <h1>Track Your Order</h1>
      
      <div *ngIf="!orderIdFromRoute && !trackingSubmitted" class="tracking-form-container">
        <p>Enter your order number and phone number to track your order.</p>
        
        <form [formGroup]="trackingForm" (ngSubmit)="trackOrder()">
          <div class="form-group">
            <label for="orderId">Order Number</label>
            <input 
              type="text" 
              id="orderId" 
              formControlName="orderId" 
              placeholder="e.g., ORD-123456"
              [ngClass]="{'is-invalid': submitted && f['orderId'].errors}"
            >
            <div *ngIf="submitted && f['orderId'].errors" class="error-text">
              <span *ngIf="f['orderId'].errors['required']">Order number is required</span>
            </div>
          </div>
          
          <div class="form-group">
            <label for="phone">Phone Number</label>
            <input 
              type="tel" 
              id="phone" 
              formControlName="phone" 
              placeholder="e.g., 0888123456"
              [ngClass]="{'is-invalid': submitted && f['phone'].errors}"
            >
            <div *ngIf="submitted && f['phone'].errors" class="error-text">
              <span *ngIf="f['phone'].errors['required']">Phone number is required</span>
              <span *ngIf="f['phone'].errors['pattern']">Please enter a valid phone number</span>
            </div>
          </div>
          
          <button 
            type="submit" 
            class="track-button" 
            [disabled]="loading"
          >
            <span *ngIf="loading"><app-loading-spinner [size]="'small'"></app-loading-spinner> Tracking...</span>
            <span *ngIf="!loading">Track Order</span>
          </button>
        </form>
        
        <div *ngIf="error" class="error-message">
          {{ error }}
        </div>
      </div>
      
      <div *ngIf="orderIdToShow" class="order-details-container">
        <app-order-tracking [orderId]="orderIdToShow"></app-order-tracking>
        
        <div class="actions">
          <a routerLink="/orders" class="secondary-button">View All Orders</a>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .order-tracking-page {
      max-width: 800px;
      margin: 0 auto;
      padding: 30px 20px;
    }
    
    h1 {
      margin-bottom: 30px;
      color: rgb(59, 4, 4);
      text-align: center;
    }
    
    .tracking-form-container {
      background: #FFDBDB;
      border-radius: 8px;
      box-shadow: 0 2px 10px rgba(0,0,0,0.1);
      padding: 30px;
      margin-bottom: 30px;
    }
    
    .tracking-form-container p {
      margin-bottom: 20px;
      color: rgb(59, 4, 4);
    }
    
    .form-group {
      margin-bottom: 20px;
    }
    
    label {
      display: block;
      margin-bottom: 8px;
      font-weight: 500;
      color:rgb(59, 4, 4);
    }
    
    input {
      width: 100%;
      padding: 12px 15px;
      border: 1px solid #ddd;
      border-radius: 4px;
      font-size: 16px;
      transition: border-color 0.2s;
    }
    
    input:focus {
      outline: none;
      border-color: #ff6b00;
    }
    
    input.is-invalid {
      border-color: #dc3545;
    }
    
    .error-text {
      color: #dc3545;
      font-size: 14px;
      margin-top: 5px;
    }
    
    .track-button {
      background-color:rgb(59, 4, 4);
      color: white;
      border: none;
      padding: 12px 25px;
      border-radius: 4px;
      font-size: 16px;
      font-weight: 500;
      cursor: pointer;
      width: 100%;
      display: flex;
      justify-content: center;
      align-items: center;
    }
    
    .track-button:hover {
      background-color: #e06000;
    }
    
    .track-button:disabled {
      background-color: #ffab7a;
      cursor: not-allowed;
    }
    
    .error-message {
      background-color: #f8d7da;
      color: #721c24;
      padding: 12px 15px;
      border-radius: 4px;
      margin-top: 20px;
    }
    
    .actions {
      margin-top: 30px;
      display: flex;
      justify-content: center;
    }
    
    .secondary-button {
      display: inline-block;
      background-color: #f5f5f5;
      color: rgb(59, 4, 4);
      border: 1px solid #ddd;
      padding: 10px 20px;
      border-radius: 4px;
      text-decoration: none;
      font-weight: 500;
      transition: background-color 0.2s;
    }
    
    .secondary-button:hover {
      background-color: #e0e0e0;
    }
  `]
})
export class OrderTrackingPageComponent implements OnInit {
  trackingForm!: FormGroup;
  submitted = false;
  loading = false;
  error = '';
  
  orderIdFromRoute: string | null = null;
  trackingSubmitted = false;
  orderIdToShow: string | null = null;
  
  constructor(
    private formBuilder: FormBuilder,
    private route: ActivatedRoute,
    private orderService: OrderService
  ) {}
  
  ngOnInit(): void {
    // Check if we have an order ID from the route
    this.route.paramMap.subscribe(params => {
      this.orderIdFromRoute = params.get('id');
      if (this.orderIdFromRoute) {
        this.orderIdToShow = this.orderIdFromRoute;
      }
    });
    
    // Initialize the form
    this.trackingForm = this.formBuilder.group({
      orderId: ['', Validators.required],
      phone: ['', [Validators.required, Validators.pattern('^[0-9]{10,}$')]]
    });
  }
  
  // Convenience getter for form fields
  get f() { return this.trackingForm.controls; }
  
  trackOrder(): void {
    this.submitted = true;
    this.error = '';
    
    // Stop if form is invalid
    if (this.trackingForm.invalid) {
      return;
    }
    
    this.loading = true;
    
    // In a real application, we would validate the order number and phone against the backend
    // Here we're simulating a successful lookup
    const orderId = this.trackingForm.value.orderId;
    const phone = this.trackingForm.value.phone;
    
    this.orderService.validateOrderTracking(orderId, phone).subscribe({
      next: (isValid) => {
        if (isValid) {
          this.trackingSubmitted = true;
          this.orderIdToShow = orderId;
        } else {
          this.error = 'No order found with the provided details. Please check and try again.';
        }
        this.loading = false;
      },
      error: (err) => {
        console.error('Error validating order tracking:', err);
        this.error = 'There was an error processing your request. Please try again.';
        this.loading = false;
      }
    });
  }
}