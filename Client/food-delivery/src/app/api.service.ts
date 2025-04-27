import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../environments/environment.development';
import { Restaurant } from './types/restaurants';
import { Observable } from 'rxjs';

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

  getRestaurants(): Observable<Restaurant[]> {
    const { apiUrl } = environment;
    // Public endpoint, no auth needed
    return this.http.get<Restaurant[]>(`${apiUrl}/api/restaurants`);
  }

  getMenu(id: string): Observable<Restaurant> {
    const { apiUrl } = environment;
    // Public endpoint, no auth needed
    return this.http.get<Restaurant>(`${apiUrl}/api/restaurants/${id}`);
  }

  // Methods that require authentication would use the headers
  getUserProfile() {
    const { apiUrl } = environment;
    return this.http.get(`${apiUrl}/api/user/profile`, { headers: this.getHeaders() });
  }

  // Example of POST with authentication
  placeOrder(orderData: any) {
    const { apiUrl } = environment;
    return this.http.post(`${apiUrl}/api/orders`, orderData, { headers: this.getHeaders() });
  }
}