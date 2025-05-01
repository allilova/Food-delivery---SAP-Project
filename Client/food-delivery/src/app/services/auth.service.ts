// src/app/services/auth.service.ts
import { Injectable, PLATFORM_ID, Inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { environment } from '../../environments/environment.development';
import { isPlatformBrowser } from '@angular/common';
import { Router } from '@angular/router';

export interface AuthResponse {
  jwt: string;
  message: string;
  role: string;
}

export interface LoginCredentials {
  email: string;
  password: string;
}

export interface RegisterUser {
  name: string;
  email: string;
  password: string;
  phoneNumber: string;  // Changed from phone_number to phoneNumber to match backend
  address: string;
  role: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private currentUserSubject: BehaviorSubject<any>;
  public currentUser: Observable<any>;
  private apiUrl = environment.apiUrl;
  private isBrowser: boolean;

  constructor(
    private http: HttpClient,
    private router: Router,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {
    this.isBrowser = isPlatformBrowser(this.platformId);
    
    // Initialize currentUserSubject - safely check if we're in browser environment
    let userData = null;
    if (this.isBrowser) {
      const userJson = localStorage.getItem('currentUser');
      if (userJson) {
        try {
          userData = JSON.parse(userJson);
        } catch (e) {
          console.error('Error parsing user data from localStorage', e);
          // If invalid JSON, remove it
          localStorage.removeItem('currentUser');
        }
      }
    }
    
    this.currentUserSubject = new BehaviorSubject<any>(userData);
    this.currentUser = this.currentUserSubject.asObservable();
  }

  public get currentUserValue() {
    return this.currentUserSubject.value;
  }

  public get isLoggedIn(): boolean {
    return !!this.currentUserValue;
  }

  public get userRole(): string | null {
    return this.currentUserValue?.role || null;
  }

  login(credentials: LoginCredentials): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/auth/login`, credentials)
      .pipe(
        tap(response => {
          if (response.jwt) {
            const userData = {
              email: credentials.email,
              token: response.jwt,
              role: response.role
            };
            
            // Only access localStorage in browser environment
            if (this.isBrowser) {
              localStorage.setItem('currentUser', JSON.stringify(userData));
            }
            
            this.currentUserSubject.next(userData);
          }
        }),
        catchError(error => {
          console.error('Login error:', error);
          return throwError(() => error);
        })
      );
  }

  register(user: RegisterUser): Observable<AuthResponse> {
    console.log('Sending registration data:', user);
    return this.http.post<AuthResponse>(`${this.apiUrl}/auth/register`, user)
      .pipe(
        catchError(error => {
          console.error('Registration error:', error);
          return throwError(() => error);
        })
      );
  }

  logout() {
    // Only access localStorage in browser environment
    if (this.isBrowser) {
      localStorage.removeItem('currentUser');
    }
    
    this.currentUserSubject.next(null);
    this.router.navigate(['/home']);
  }
}