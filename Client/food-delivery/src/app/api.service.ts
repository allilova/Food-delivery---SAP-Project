import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { environment } from '../environments/environment';
import { Restaurant } from './types/restaurants';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { isPlatformBrowser } from '@angular/common';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private http = inject(HttpClient);

  // Helper method to get auth token from localStorage
  private getAuthToken(): string | null {
    if (typeof window !== 'undefined') {
      return localStorage.getItem('jwt_token');
    }
    return null;
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

  // Error handling function that works in both browser and server environments
  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'An unknown error occurred';
    
    if (error.error instanceof Error) {
      // Client-side or network error
      errorMessage = `Error: ${error.error.message}`;
    } else {
      // Backend returned unsuccessful response code
      errorMessage = `Server returned code ${error.status}, message: ${error.message}`;
    }
    
    console.error(errorMessage);
    return throwError(() => new Error(errorMessage));
  }

  getRestaurants(): Observable<Restaurant[]> {
    const { apiUrl } = environment;
    // Public endpoint, no auth needed
    return this.http.get<Restaurant[]>(`${apiUrl}/api/restaurants`)
      .pipe(
        catchError(this.handleError)
      );
  }

  getMenu(id: string): Observable<Restaurant> {
    const { apiUrl } = environment;
    // Public endpoint, no auth needed
    return this.http.get<Restaurant>(`${apiUrl}/api/restaurants/${id}`)
      .pipe(
        catchError(this.handleError)
      );
  }

  // Methods that require authentication would use the headers
  getUserProfile() {
    const { apiUrl } = environment;
    return this.http.get(`${apiUrl}/api/user/profile`, { headers: this.getHeaders() })
      .pipe(
        catchError(this.handleError)
      );
  }

  // Example of POST with authentication
  placeOrder(orderData: any) {
    const { apiUrl } = environment;
    return this.http.post(`${apiUrl}/api/orders`, orderData, { headers: this.getHeaders() })
      .pipe(
        catchError(this.handleError)
      );
  }
}