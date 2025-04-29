import { Injectable, PLATFORM_ID, Inject } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpErrorResponse
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AuthService } from '../services/auth.service';
import { isPlatformBrowser } from '@angular/common';
import { Router } from '@angular/router';

@Injectable()
export class JwtInterceptor implements HttpInterceptor {
  constructor(
    private authService: AuthService,
    private router: Router,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {}

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // Only add auth token in browser environment
    if (isPlatformBrowser(this.platformId)) {
      // Get the current user from auth service
      const currentUser = this.authService.currentUserValue;
      
      if (currentUser && currentUser.token) {
        // Clone the request and add Authorization header with JWT token
        request = request.clone({
          setHeaders: {
            Authorization: `Bearer ${currentUser.token}`
          }
        });
      }
    }

    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        // Handle 401 Unauthorized errors by logging out user and redirecting to login
        if (error.status === 401) {
          // Only perform these operations in browser context
          if (isPlatformBrowser(this.platformId)) {
            console.log('Authentication error. Logging out.');
            this.authService.logout();
            this.router.navigate(['/login']);
          }
        }
        
        return throwError(() => error);
      })
    );
  }
}