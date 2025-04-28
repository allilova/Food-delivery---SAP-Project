// src/app/interceptors/jwt.interceptor.ts
import { Injectable, PLATFORM_ID, Inject } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor
} from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { isPlatformBrowser } from '@angular/common';

@Injectable()
export class JwtInterceptor implements HttpInterceptor {
  constructor(
    private authService: AuthService,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {}

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // Only add auth token in browser environment or when auth service has a value
    if (isPlatformBrowser(this.platformId) || this.authService.currentUserValue) {
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

    return next.handle(request);
  }
}