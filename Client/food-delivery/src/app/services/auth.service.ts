// src/app/services/auth.service.ts
import { Injectable, PLATFORM_ID, Inject } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { catchError, tap, map, finalize } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { isPlatformBrowser } from '@angular/common';
import { Router } from '@angular/router';
import { USER_ROLE } from '../types/user-role.enum';
import { ErrorHandlerService } from './error-handler.service';
import { NotificationService } from './notification.service';

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
    private errorHandler: ErrorHandlerService,
    private notificationService: NotificationService,
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
    // Show loading notification
    this.notificationService.info('Logging in...', true, 2000);
    
    console.log(`Sending login request to: ${this.apiUrl}/auth/login`);
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    });
    
    return this.http.post<AuthResponse>(`${this.apiUrl}/auth/login`, credentials, { headers })
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
            
            // Show success notification
            this.notificationService.success('Login successful! Welcome back.');
          } else {
            console.warn('Login response did not contain JWT token:', response);
            this.notificationService.warning('Login successful but token was not received properly');
          }
        }),
        catchError((error: HttpErrorResponse) => {
          console.error('Login error details:', error);
          
          // Handle common login errors more elegantly
          let errorMessage = 'Login failed. Please check your credentials and try again.';
          
          if (error.status === 0) {
            errorMessage = 'Cannot connect to the server. Please check your connection or ensure the backend is running.';
          } else if (error.status === 401) {
            errorMessage = 'Invalid email or password. Please try again.';
          } else if (error.status === 403) {
            errorMessage = 'Your account is suspended or inactive. Please contact support.';
          } else if (error.status >= 500) {
            errorMessage = 'Server error. Please try again later.';
          }
          
          // Show error notification
          this.notificationService.error(errorMessage);
          
          // Use our error handler service
          return this.errorHandler.handleError(error, 'login');
        })
      );
  }

  register(user: RegisterUser): Observable<AuthResponse> {
    console.log('Sending registration request with data:', user);
    // Show loading notification
    this.notificationService.info('Creating your account...', true, 2000);
    
    return this.http.post<AuthResponse>(`${this.apiUrl}/auth/register`, user)
      .pipe(
        tap(response => {
          if (response.jwt) {
            // Show success notification
            this.notificationService.success('Account created successfully! Welcome to Food Delivery.');
          }
        }),
        catchError((error: HttpErrorResponse) => {
          let errorMessage = 'Registration failed. Please try again.';
          
          if (error.status === 400) {
            // Look for common validation errors
            if (error.error && error.error.message) {
              errorMessage = error.error.message;
            } else if (error.error && error.error.errors) {
              // Join all validation errors
              errorMessage = Object.values(error.error.errors).join('. ');
            }
          } else if (error.status === 409) {
            errorMessage = 'An account with this email already exists.';
          } else if (error.status >= 500) {
            errorMessage = 'Server error. Please try again later.';
          }
          
          // Show error notification
          this.notificationService.error(errorMessage);
          
          // Use our error handler service
          return this.errorHandler.handleError(error, 'registration');
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
      catchError((error: HttpErrorResponse) => {
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
          this.notificationService.error('Your session has expired. Please login again.');
        } else if (error.status === 403) {
          this.notificationService.error('You do not have permission to access your profile.');
        } else if (error.status === 404) {
          this.notificationService.error('Profile not found. Please complete your registration.');
        } else {
          this.notificationService.error('Could not load profile. Please try again later.');
        }
        
        return this.errorHandler.handleError(error, 'profile retrieval');
      })
    );
  }

  // Update user profile
  updateUserProfile(profileData: any): Observable<UserProfile> {
    const token = this.getToken();
    return this.http.put<UserProfile>(`${this.apiUrl}/api/user/profile`, profileData, {
      headers: { 
        'Authorization': `Bearer ${token}`
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
  
  // Debug auth token - useful for troubleshooting
  debugAuthToken(): Observable<any> {
    const token = this.getToken();
    let headers = new HttpHeaders();
    
    if (token) {
      headers = headers.set('Authorization', `Bearer ${token}`);
      this.notificationService.info('Debugging authentication token...', true, 2000);
    } else {
      this.notificationService.info('No token available, checking auth system...', true, 2000);
    }
    
    // First try the new debug endpoint that doesn't require auth
    return this.http.get<any>(`${this.apiUrl}/api/auth/debug`, {
      headers: headers
    }).pipe(
      tap(response => {
        console.log('Auth debug response:', response);
        
        if (response.error) {
          this.notificationService.warning('Authentication debug encountered an error: ' + response.error);
        } else if (response.auth && response.auth.isAuthenticated) {
          this.notificationService.success('Authentication is working properly');
        } else {
          this.notificationService.info('System is working but you are not authenticated');
        }
        
        // Check token info
        if (response.token && response.token.user) {
          const user = response.token.user;
          this.notificationService.success(`Found user: ${user.name} with role: ${user.role}`);
          
          if (user.hasRestaurant) {
            this.notificationService.success(`User has a restaurant: ${user.restaurantName}`);
          } else {
            this.notificationService.warning('No restaurant found for this user');
          }
        } else if (response.token) {
          this.notificationService.error('Token is invalid or user not found');
        }
      }),
      catchError((error: HttpErrorResponse) => {
        console.error('Error debugging auth token:', error);
        
        // Try a fallback approach with just basic info
        this.notificationService.warning('Trying fallback approach...');
        
        return this.http.get<any>(`${this.apiUrl}/api/test`).pipe(
          map(() => {
            return { 
              fallback: true, 
              apiWorking: true,
              token: token ? { present: true, value: token.substring(0, 10) + '...' } : { present: false },
              tokenLength: token ? token.length : 0
            };
          }),
          tap(response => {
            this.notificationService.info('API is working but authentication debug failed');
            console.log('Fallback debug info:', response);
          }),
          catchError(fallbackError => {
            console.error('Even fallback failed:', fallbackError);
            this.notificationService.error('Failed to debug auth token and API seems to be down');
            return throwError(() => fallbackError);
          })
        );
      })
    );
  }
  
  // Original debug method using the token-requiring endpoint
  debugAuthTokenOriginal(): Observable<any> {
    const token = this.getToken();
    if (!token) {
      this.notificationService.error('No authentication token found');
      return throwError(() => new Error('No authentication token found'));
    }
    
    this.notificationService.info('Debugging authentication token...', true, 2000);
    
    return this.http.get<any>(`${this.apiUrl}/api/auth/debug-token`, {
      headers: { 
        'Authorization': `Bearer ${token}`
      }
    }).pipe(
      tap(response => {
        console.log('Auth debug response:', response);
        this.notificationService.success('Authentication token debug successful');
      }),
      catchError((error: HttpErrorResponse) => {
        console.error('Error debugging auth token:', error);
        this.notificationService.error('Failed to debug auth token: ' + (error.error?.message || error.message));
        return throwError(() => error);
      })
    );
  }
}