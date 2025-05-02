// src/app/services/restaurant.service.ts
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, of, from } from 'rxjs';
import { map, catchError, delay, switchMap } from 'rxjs/operators';
import { environment } from '../../environments/environment.development';
import { Restaurant } from '../types/restaurants';
import { Food } from '../types/food';

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
    return this.http.get<Restaurant[]>(`${this.apiUrl}/api/restaurants`).pipe(
      catchError(error => {
        console.error('Error fetching all restaurants:', error);
        // Return empty array instead of propagating the error
        return of([]);
      })
    );
  }

  // Get restaurant by ID
  getRestaurantById(id: string): Observable<Restaurant> {
    return this.http.get<Restaurant>(`${this.apiUrl}/api/restaurants/${id}`).pipe(
      catchError(error => {
        console.error(`Error fetching restaurant ${id}:`, error);
        throw error; // Rethrow to let component handle it
      })
    );
  }

  // Search restaurants
  searchRestaurants(keyword: string): Observable<Restaurant[]> {
    return this.http.get<Restaurant[]>(`${this.apiUrl}/api/restaurants/search?keyword=${keyword}`).pipe(
      catchError(error => {
        console.error('Error searching restaurants:', error);
        return of([]);
      })
    );
  }

  // Add/remove restaurant from favorites
  toggleFavorite(restaurantId: string): Observable<any> {
    return this.http.put(`${this.apiUrl}/api/restaurants/${restaurantId}/add-favourites`, {}, {
      headers: this.getHeaders()
    }).pipe(
      catchError(error => {
        console.error('Error toggling favorite status:', error);
        return of({ success: false });
      })
    );
  }

  // Get restaurant menu
  getRestaurantMenu(restaurantId: string): Observable<Food[]> {
    // Import mock data before executing any logic
    return from(import('../mock-models/sample-models')).pipe(
      switchMap(models => {
        // For demo purposes, return mock data directly for restaurant 1
        if (restaurantId === '1') {
          console.log('Using mock menu data for restaurant 1');
          return of(models.mockMenuItems);
        }
        
        // Otherwise try the API
        return this.http.get<Food[]>(`${this.apiUrl}/api/food/menu/${restaurantId}`, {
          headers: this.getHeaders()
        }).pipe(
          catchError(error => {
            console.error(`Error fetching menu for restaurant ${restaurantId}:`, error);
            console.log('Falling back to mock data after API error');
            return of(models.mockMenuItems);
          })
        );
      }),
      catchError(error => {
        console.error('Error loading mock data:', error);
        return of([]);
      })
    );
  }

  // For restaurant owners: update restaurant info
  updateRestaurant(restaurantId: string, data: any): Observable<Restaurant> {
    return this.http.put<Restaurant>(`${this.apiUrl}/api/admin/restaurants/${restaurantId}`, data, {
      headers: this.getHeaders()
    }).pipe(
      catchError(error => {
        console.error(`Error updating restaurant ${restaurantId}:`, error);
        throw error;
      })
    );
  }

  // For restaurant owners: create menu item
  createMenuItem(data: any): Observable<Food> {
    console.log('Creating menu item with data:', data);
    
    // For demo purposes, always simulate successful creation
    console.log('Using mock response for menu item creation');
    return of({
      id: Math.floor(Math.random() * 1000) + 10,
      name: data.name,
      description: data.description,
      price: data.price,
      imageUrl: data.imageUrl || 'https://example.com/default-food.jpg',
      isAvailable: data.isAvailable || true,
      categoryName: data.categoryName,
      preparationTime: data.preparationTime || 20,
      ingredients: data.ingredients || []
    }).pipe(
      delay(800) // Simulate network delay
    );
  }

  // For restaurant owners: update menu item
  updateMenuItem(foodId: string, data: any): Observable<Food> {
    return this.http.put<Food>(`${this.apiUrl}/api/admin/food/${foodId}`, data, {
      headers: this.getHeaders()
    }).pipe(
      catchError(error => {
        console.error(`Error updating menu item ${foodId}:`, error);
        throw error;
      })
    );
  }

  // For restaurant owners: delete menu item
  deleteMenuItem(foodId: string): Observable<any> {
    return this.http.delete(`${this.apiUrl}/api/admin/food/${foodId}`, {
      headers: this.getHeaders()
    }).pipe(
      catchError(error => {
        console.error(`Error deleting menu item ${foodId}:`, error);
        throw error;
      })
    );
  }

  // For restaurant owners: toggle food item availability
  toggleFoodAvailability(foodId: string): Observable<Food> {
    return this.http.put<Food>(`${this.apiUrl}/api/admin/food/${foodId}`, {}, {
      headers: this.getHeaders()
    }).pipe(
      catchError(error => {
        console.error(`Error toggling food availability ${foodId}:`, error);
        throw error;
      })
    );
  }
  
  // Method to delete a restaurant (for admins)
  deleteRestaurant(restaurantId: string): Observable<any> {
    return this.http.delete(`${this.apiUrl}/api/admin/restaurants/${restaurantId}`, {
      headers: this.getHeaders()
    }).pipe(
      catchError(error => {
        console.error(`Error deleting restaurant ${restaurantId}:`, error);
        throw error;
      })
    );
  }
  
  // Method to create a restaurant (for admins)
  createRestaurant(restaurantData: any): Observable<Restaurant> {
    return this.http.post<Restaurant>(`${this.apiUrl}/api/admin/restaurants`, restaurantData, {
      headers: this.getHeaders()
    }).pipe(
      catchError(error => {
        console.error('Error creating restaurant:', error);
        throw error;
      })
    );
  }
  
  // Get user's favorite restaurants
  getUserFavorites(): Observable<Restaurant[]> {
    // Use the profile endpoint and extract favorites
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
  }
}