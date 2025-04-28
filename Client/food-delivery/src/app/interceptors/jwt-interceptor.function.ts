// src/app/interceptors/jwt-interceptor.function.ts
import { HttpHandlerFn, HttpInterceptorFn, HttpRequest } from '@angular/common/http';
import { inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { AuthService } from '../services/auth.service';

export const jwtInterceptor: HttpInterceptorFn = (
  req: HttpRequest<unknown>, 
  next: HttpHandlerFn
) => {
  const platformId = inject(PLATFORM_ID);
  const authService = inject(AuthService);
  
  // Only try to add token in browser environment or if auth service has a value
  if ((isPlatformBrowser(platformId) || authService.currentUserValue) && authService.isLoggedIn) {
    const token = authService.currentUserValue?.token;
    
    if (token) {
      const authReq = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
      return next(authReq);
    }
  }
  
  return next(req);
};