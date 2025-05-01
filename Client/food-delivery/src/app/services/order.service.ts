// src/app/services/order.service.ts
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment.development';
import { Order, OrderStatus } from '../types/order';

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) { }

  // Helper method to get auth token
  private getAuthToken(): string | null {
    return localStorage.getItem('jwt_token');
  }

  // Add headers for authenticated requests
  private getHeaders(): HttpHeaders {
    const token = this.getAuthToken();
    let headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });
    
    if (token) {
      headers = headers.set('Authorization', `Bearer ${token}`);
    }
    
    return headers;
  }

  // Create a new order
  createOrder(orderData: any): Observable<Order> {
    return this.http.post<Order>(`${this.apiUrl}/api/orders`, orderData, {
      headers: this.getHeaders()
    });
  }

  // Get all orders for the current user
  getUserOrders(): Observable<Order[]> {
    return this.http.get<Order[]>(`${this.apiUrl}/api/orders/user`, {
      headers: this.getHeaders()
    });
  }

  // Get order details by ID
  getOrderById(orderId: string): Observable<Order> {
    return this.http.get<Order>(`${this.apiUrl}/api/orders/${orderId}`, {
      headers: this.getHeaders()
    });
  }

  // Cancel an order
  cancelOrder(orderId: string): Observable<Order> {
    return this.http.put<Order>(`${this.apiUrl}/api/orders/${orderId}/cancel`, {}, {
      headers: this.getHeaders()
    });
  }

  // For restaurant owners: get restaurant orders
  getRestaurantOrders(): Observable<Order[]> {
    return this.http.get<Order[]>(`${this.apiUrl}/api/supplier/orders`, {
      headers: this.getHeaders()
    });
  }

  // For restaurant owners: update order status
  updateOrderStatus(orderId: string, status: OrderStatus): Observable<Order> {
    return this.http.put<Order>(`${this.apiUrl}/api/supplier/orders/${orderId}/status`, 
      { status: status },
      { headers: this.getHeaders() }
    );
  }
  
  // For restaurant owners: get orders by status
  getOrdersByStatus(status: OrderStatus): Observable<Order[]> {
    return this.http.get<Order[]>(`${this.apiUrl}/api/supplier/orders?status=${status}`, {
      headers: this.getHeaders()
    });
  }
  
  // For restaurant owners: get order statistics
  getOrderStatistics(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/api/supplier/statistics`, {
      headers: this.getHeaders()
    });
  }
  
  // For delivery drivers: get available orders
  getAvailableOrders(): Observable<Order[]> {
    return this.http.get<Order[]>(`${this.apiUrl}/api/driver/orders/available`, {
      headers: this.getHeaders()
    });
  }
  
  // For delivery drivers: accept an order
  acceptOrder(orderId: string): Observable<Order> {
    return this.http.put<Order>(`${this.apiUrl}/api/driver/orders/${orderId}/accept`, {}, {
      headers: this.getHeaders()
    });
  }
  
  // For delivery drivers: mark order as delivered
  markOrderAsDelivered(orderId: string): Observable<Order> {
    return this.http.put<Order>(`${this.apiUrl}/api/driver/orders/${orderId}/delivered`, {}, {
      headers: this.getHeaders()
    });
  }
}