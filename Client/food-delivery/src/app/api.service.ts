import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../environments/environment.development';
import { Restaurant } from './types/restaurants';
import { Observable, of, catchError } from 'rxjs';
import { Food } from './types/food';
import { Order } from './types/order';

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  constructor(private http: HttpClient) { }

  // Helper method to get auth token from localStorage
  private getAuthToken(): string | null {
    return localStorage.getItem('jwt_token');
  }

  // Add headers for requests that might need authentication
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

  // Get all restaurants
  getRestaurants(): Observable<Restaurant[]> {
    const { apiUrl } = environment;
    // Public endpoint, but we'll send the token if available
    return this.http.get<Restaurant[]>(`${apiUrl}/api/restaurants`, { 
      headers: this.getHeaders() 
    }).pipe(
      catchError(error => {
        console.error('Error fetching restaurants:', error);
        // Return empty array instead of propagating the error
        return of([]);
      })
    );
  }

  // Get restaurant menu by ID
  getMenu(id: string): Observable<Restaurant> {
    const { apiUrl } = environment;
    return this.http.get<Restaurant>(`${apiUrl}/api/restaurants/${id}`, { 
      headers: this.getHeaders() 
    }).pipe(
      catchError(error => {
        console.error(`Error fetching restaurant ${id}:`, error);
        throw error; // Rethrow to let component handle it
      })
    );
  }

  // Search restaurants
  searchRestaurants(keyword: string): Observable<Restaurant[]> {
    const { apiUrl } = environment;
    return this.http.get<Restaurant[]>(`${apiUrl}/api/restaurants/search?keyword=${keyword}`, {
      headers: this.getHeaders()
    }).pipe(
      catchError(error => {
        console.error('Error searching restaurants:', error);
        return of([]);
      })
    );
  }

  // Get food items by menu ID
  getFoodByMenu(menuId: string, category?: string): Observable<Food[]> {
    const { apiUrl } = environment;
    let url = `${apiUrl}/api/food/menu/${menuId}`;
    if (category) {
      url += `?foodCategory=${category}`;
    }
    return this.http.get<Food[]>(url, { 
      headers: this.getHeaders() 
    }).pipe(
      catchError(error => {
        console.error(`Error fetching menu items for ${menuId}:`, error);
        return of([]);
      })
    );
  }

  // Search food items
  searchFood(keyword: string): Observable<Food[]> {
    const { apiUrl } = environment;
    return this.http.get<Food[]>(`${apiUrl}/api/food/search?foodName=${keyword}`, {
      headers: this.getHeaders()
    }).pipe(
      catchError(error => {
        console.error('Error searching food items:', error);
        return of([]);
      })
    );
  }

  // Add restaurant to favorites
  addToFavorites(restaurantId: string): Observable<any> {
    const { apiUrl } = environment;
    return this.http.put(`${apiUrl}/api/restaurants/${restaurantId}/add-favourites`, {}, {
      headers: this.getHeaders()
    }).pipe(
      catchError(error => {
        console.error('Error adding restaurant to favorites:', error);
        return of({ success: false });
      })
    );
  }

  // Get user profile
  getUserProfile(): Observable<any> {
    const { apiUrl } = environment;
    return this.http.get(`${apiUrl}/api/users/profile`, { 
      headers: this.getHeaders() 
    }).pipe(
      catchError(error => {
        console.error('Error fetching user profile:', error);
        return of(null);
      })
    );
  }

  // Place order
  placeOrder(orderData: any): Observable<Order> {
    const { apiUrl } = environment;
    return this.http.post<Order>(`${apiUrl}/api/orders`, orderData, { 
      headers: this.getHeaders() 
    }).pipe(
      catchError(error => {
        console.error('Error placing order:', error);
        throw error; // We should rethrow in this case as we need to know if the order failed
      })
    );
  }

  // Get user orders
  getUserOrders(): Observable<Order[]> {
    const { apiUrl } = environment;
    return this.http.get<Order[]>(`${apiUrl}/api/orders/user`, {
      headers: this.getHeaders()
    }).pipe(
      catchError(error => {
        console.error('Error fetching user orders:', error);
        return of([]);
      })
    );
  }

  // For restaurant owners: get restaurant orders
  getRestaurantOrders(): Observable<Order[]> {
    const { apiUrl } = environment;
    return this.http.get<Order[]>(`${apiUrl}/api/supplier/orders`, {
      headers: this.getHeaders()
    }).pipe(
      catchError(error => {
        console.error('Error fetching restaurant orders:', error);
        return of([]);
      })
    );
  }

  // Update order status (for restaurant owners)
  updateOrderStatus(orderId: string, status: string): Observable<Order> {
    const { apiUrl } = environment;
    return this.http.put<Order>(`${apiUrl}/api/orders/${orderId}/status?status=${status}`, {}, {
      headers: this.getHeaders()
    }).pipe(
      catchError(error => {
        console.error(`Error updating order ${orderId} status:`, error);
        throw error;
      })
    );
  }
  
  // Create a menu item (for restaurant owners)
  createMenuItem(menuData: any): Observable<any> {
    const { apiUrl } = environment;
    return this.http.post(`${apiUrl}/api/admin/food`, menuData, {
      headers: this.getHeaders()
    }).pipe(
      catchError(error => {
        console.error('Error creating menu item:', error);
        throw error;
      })
    );
  }
  
  // Update a restaurant (for restaurant owners)
  updateRestaurant(restaurantId: string, restaurantData: any): Observable<any> {
    const { apiUrl } = environment;
    return this.http.put(`${apiUrl}/api/admin/restaurants/${restaurantId}`, restaurantData, {
      headers: this.getHeaders()
    }).pipe(
      catchError(error => {
        console.error(`Error updating restaurant ${restaurantId}:`, error);
        throw error;
      })
    );
  }
}