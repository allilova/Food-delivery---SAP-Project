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
          return throwError(() => error);
        })
      );
    }
  }
  
  return next(req);
};