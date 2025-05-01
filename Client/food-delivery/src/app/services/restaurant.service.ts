// src/app/services/restaurant.service.ts
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment.development';
import { Restaurant } from '../types/restaurants';
import { Food } from '../types/food';
import { map, catchError } from 'rxjs/operators';
import { of } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class RestaurantService {
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

  // Get all restaurants
  getAllRestaurants(): Observable<Restaurant[]> {
    return this.http.get<Restaurant[]>(`${this.apiUrl}/api/restaurants`, {
      headers: this.getHeaders()
    });
  }

  // Get restaurant by ID
  getRestaurantById(id: string): Observable<Restaurant> {
    return this.http.get<Restaurant>(`${this.apiUrl}/api/restaurants/${id}`, {
      headers: this.getHeaders()
    });
  }

  // Search restaurants
  searchRestaurants(keyword: string): Observable<Restaurant[]> {
    return this.http.get<Restaurant[]>(`${this.apiUrl}/api/restaurants/search?keyword=${keyword}`, {
      headers: this.getHeaders()
    });
  }

  // Add/remove restaurant from favorites
  toggleFavorite(restaurantId: string): Observable<any> {
    return this.http.put(`${this.apiUrl}/api/restaurants/${restaurantId}/add-favourites`, {}, {
      headers: this.getHeaders()
    });
  }

  // Get restaurant menu
  getRestaurantMenu(restaurantId: string): Observable<Food[]> {
    return this.http.get<Food[]>(`${this.apiUrl}/api/food/menu/${restaurantId}`, {
      headers: this.getHeaders()
    });
  }

  // For restaurant owners: update restaurant info
  updateRestaurant(restaurantId: string, data: any): Observable<Restaurant> {
    return this.http.put<Restaurant>(`${this.apiUrl}/api/admin/restaurants/${restaurantId}`, data, {
      headers: this.getHeaders()
    });
  }

  // For restaurant owners: create menu item
  createMenuItem(data: any): Observable<Food> {
    return this.http.post<Food>(`${this.apiUrl}/api/admin/food`, data, {
      headers: this.getHeaders()
    });
  }

  // For restaurant owners: update menu item
  updateMenuItem(foodId: string, data: any): Observable<Food> {
    return this.http.put<Food>(`${this.apiUrl}/api/admin/food/${foodId}`, data, {
      headers: this.getHeaders()
    });
  }

  // For restaurant owners: delete menu item
  deleteMenuItem(foodId: string): Observable<any> {
    return this.http.delete(`${this.apiUrl}/api/admin/food/${foodId}`, {
      headers: this.getHeaders()
    });
  }

  // For restaurant owners: toggle food item availability
  toggleFoodAvailability(foodId: string): Observable<Food> {
    return this.http.put<Food>(`${this.apiUrl}/api/admin/food/${foodId}`, {}, {
      headers: this.getHeaders()
    });
  }
// Method to delete a restaurant (for admins)
deleteRestaurant(restaurantId: string): Observable<any> {
    return this.http.delete(`${this.apiUrl}/api/admin/restaurants/${restaurantId}`, {
      headers: this.getHeaders()
    });
  }
  
  // Method to get restaurant details with full menu
  getRestaurantWithMenu(restaurantId: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/api/restaurants/${restaurantId}?includeMenu=true`, {
      headers: this.getHeaders()
    });
  }
  
  // Method to rate a restaurant
  rateRestaurant(restaurantId: string, rating: number, comment?: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/api/restaurants/${restaurantId}/rate`, 
      { rating, comment }, 
      { headers: this.getHeaders() }
    );
  }
  
  // Get user's favorite restaurants
  getUserFavorites(): Observable<Restaurant[]> {
    // If the API endpoint doesn't exist, we can use the profile endpoint and extract favorites
    return this.http.get<any>(`${this.apiUrl}/api/users/profile`, {
      headers: this.getHeaders()
    }).pipe(
      map(profile => {
        // Extract favorites from profile using either property name
        return profile?.favorites || profile?.favourites || [];
      }),
      catchError(error => {
        console.error('Error fetching user favorites:', error);
        return of([]);
      })
    );
  };
}


