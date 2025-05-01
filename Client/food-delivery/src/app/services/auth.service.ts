// src/app/services/auth.service.ts
import { Injectable, PLATFORM_ID, Inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { catchError, tap, map } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { isPlatformBrowser } from '@angular/common';
import { Router } from '@angular/router';
import { USER_ROLE } from '../types/user-role.enum';

export interface AuthResponse {
  jwt: string;
  message: string;
  role: USER_ROLE;
}

export interface LoginCredentials {
  email: string;
  password: string;
}

export interface RegisterUser {
  name: string;
  email: string;
  password: string;
  phone: string;
  role?: string;
}

export interface UserProfile {
  id: number;
  name: string;
  email: string;
  phoneNumber: string;
  address: string;
  role: USER_ROLE;
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

  public get userRole(): USER_ROLE | null {
    return this.currentUserValue?.role || null;
  }

  login(credentials: LoginCredentials): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/auth/login`, credentials)
      .pipe(
        tap(response => {
          if (response.jwt) {
            console.log('Login successful, token received:', response.jwt.substring(0, 10) + '...');
            
            const userData = {
              email: credentials.email,
              token: response.jwt,
              role: response.role
            };
            
            // Only access localStorage in browser environment
            if (this.isBrowser) {
              localStorage.setItem('currentUser', JSON.stringify(userData));
              localStorage.setItem('jwt_token', response.jwt);
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
    console.log('Sending registration request with data:', user);
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
      localStorage.removeItem('jwt_token');
    }
    
    this.currentUserSubject.next(null);
    this.router.navigate(['/home']);
  }

  // Get user profile
  getUserProfile(): Observable<any> {
    const token = this.getToken();
    console.log('Getting user profile with token:', token ? token.substring(0, 10) + '...' : 'No token');
    
    return this.http.get<any>(`${this.apiUrl}/api/users/profile`, {
      headers: { 
        'Authorization': `Bearer ${token}`
      }
    }).pipe(
      map(response => {
        console.log('Profile response:', response);
        // Transform the response if needed
        // Add a property for favorites if it doesn't exist
        if (response && !response.favorites && response.favourites) {
          response.favorites = response.favourites;
        }
        return response;
      }),
      catchError(error => {
        console.error('Error fetching user profile:', error);
        
        // If 401 Unauthorized, clear token and navigate to login
        if (error.status === 401) {
          console.log('Unauthorized error getting profile, clearing tokens');
          if (this.isBrowser) {
            localStorage.removeItem('currentUser');
            localStorage.removeItem('jwt_token');
          }
          this.currentUserSubject.next(null);
          this.router.navigate(['/login']);
        }
        
        return throwError(() => error);
      })
    );
  }

  // Update user profile
  updateUserProfile(profileData: any): Observable<UserProfile> {
    return this.http.put<UserProfile>(`${this.apiUrl}/api/user/profile`, profileData, {
      headers: { 
        'Authorization': `Bearer ${this.getToken()}`
      }
    });
  }

  // Get the auth token
  private getToken(): string | null {
    if (this.isBrowser) {
      return localStorage.getItem('jwt_token');
    }
    return null;
  }
}