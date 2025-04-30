import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../environments/environment.development';
import { Restaurant } from './types/restaurants';
import { Observable, throwError, TimeoutError } from 'rxjs';
import { catchError, timeout, retry } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private readonly REQUEST_TIMEOUT = 10000; // 10 seconds
  private readonly MAX_RETRIES = 3;

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

  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'An error occurred';
    
    if (error.error instanceof ErrorEvent) {
      // Client-side error
      errorMessage = `Error: ${error.error.message}`;
    } else if (error instanceof TimeoutError) {
      errorMessage = 'Request timed out. Please try again.';
    } else {
      // Server-side error
      errorMessage = `Error Code: ${error.status}\nMessage: ${error.message}`;
    }
    
    console.error(errorMessage);
    return throwError(() => new Error(errorMessage));
  }

  getRestaurants(): Observable<Restaurant[]> {
    const { apiUrl } = environment;
    return this.http.get<Restaurant[]>(`${apiUrl}/api/restaurants`)
      .pipe(
        timeout(this.REQUEST_TIMEOUT),
        retry(this.MAX_RETRIES),
        catchError(this.handleError)
      );
  }

  getMenu(id: string): Observable<Restaurant> {
    const { apiUrl } = environment;
    return this.http.get<Restaurant>(`${apiUrl}/api/restaurants/${id}`)
      .pipe(
        timeout(this.REQUEST_TIMEOUT),
        retry(this.MAX_RETRIES),
        catchError(this.handleError)
      );
  }

  // Methods that require authentication would use the headers
  getUserProfile(): Observable<any> {
    const { apiUrl } = environment;
    return this.http.get(`${apiUrl}/api/user/profile`, { headers: this.getHeaders() })
      .pipe(
        timeout(this.REQUEST_TIMEOUT),
        retry(this.MAX_RETRIES),
        catchError(this.handleError)
      );
  }

  // Example of POST with authentication
  placeOrder(orderData: any): Observable<any> {
    const { apiUrl } = environment;
    return this.http.post(`${apiUrl}/api/orders`, orderData, { headers: this.getHeaders() })
      .pipe(
        timeout(this.REQUEST_TIMEOUT),
        retry(this.MAX_RETRIES),
        catchError(this.handleError)
      );
  }
}