// src/app/interceptors/jwt-interceptor.function.ts
import { HttpHandlerFn, HttpInterceptorFn, HttpRequest, HttpErrorResponse } from '@angular/common/http';
import { inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';

export const jwtInterceptor: HttpInterceptorFn = (
  req: HttpRequest<unknown>, 
  next: HttpHandlerFn
) => {
  const platformId = inject(PLATFORM_ID);
  const router = inject(Router);
  
  // Only try to add token in browser environment
  if (isPlatformBrowser(platformId)) {
    const token = localStorage.getItem('jwt_token');
    
    if (token) {
      // Clone the request and add Authorization header
      // Make sure the token is properly formatted with Bearer prefix
      console.log(`Adding auth token to request: ${req.url}`);
      const authReq = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
      
      return next(authReq).pipe(
        catchError((error: HttpErrorResponse) => {
          // Handle 401 Unauthorized errors
          if (error.status === 401) {
            console.log('Authorization error. Logging out.');
            // Clear stored tokens
            localStorage.removeItem('currentUser');
            localStorage.removeItem('jwt_token');
            router.navigate(['/login']);
          }
          
          // Handle 403 Forbidden errors
          if (error.status === 403) {
            console.log('Permission denied. Redirecting to home.');
            router.navigate(['/home']);
          }
          
          return throwError(() => error);
        })
      );
    }
  }
  
  return next(req);
};