// src/app/services/order.service.ts
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, throwError } from 'rxjs';
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
    }).pipe(
      catchError(error => {
        console.error('Error creating order:', error);
        return throwError(() => error);
      })
    );
  }

  // Get all orders for the current user with pagination
  getUserOrders(page: number = 0, size: number = 10): Observable<any> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
      
    return this.http.get<any>(`${this.apiUrl}/api/orders/user`, {
      headers: this.getHeaders(),
      params: params
    }).pipe(
      catchError(error => {
        console.error('Error loading previous orders:', error);
        return throwError(() => error);
      })
    );
  }

  // Get order details by ID
  getOrderById(orderId: string): Observable<Order> {
    return this.http.get<Order>(`${this.apiUrl}/api/orders/${orderId}`, {
      headers: this.getHeaders()
    }).pipe(
      catchError(error => {
        console.error(`Error getting order ${orderId}:`, error);
        return throwError(() => error);
      })
    );
  }

  // Update order status
  updateOrderStatus(orderId: string, status: OrderStatus): Observable<Order> {
    const params = new HttpParams().set('status', status);
    
    return this.http.put<Order>(`${this.apiUrl}/api/orders/${orderId}/status`, {}, {
      headers: this.getHeaders(),
      params: params
    }).pipe(
      catchError(error => {
        console.error(`Error updating order ${orderId} status:`, error);
        return throwError(() => error);
      })
    );
  }
  
  // For restaurant owners: get orders by status
  getOrdersByStatus(status: OrderStatus): Observable<Order[]> {
    const params = new HttpParams().set('status', status);
    
    return this.http.get<Order[]>(`${this.apiUrl}/api/supplier/orders`, {
      headers: this.getHeaders(),
      params: params
    }).pipe(
      catchError(error => {
        console.error(`Error getting orders with status ${status}:`, error);
        return throwError(() => error);
      })
    );
  }
}