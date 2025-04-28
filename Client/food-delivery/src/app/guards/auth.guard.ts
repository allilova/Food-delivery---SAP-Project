// src/app/guards/auth.guard.ts
import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { PLATFORM_ID } from '@angular/core';
import { isPlatformServer } from '@angular/common';

// Modern Angular guard using functional approach
export const authGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const authService = inject(AuthService);
  const platformId = inject(PLATFORM_ID);
  
  // If we're on the server during SSR, allow navigation
  // This prevents auth redirection during SSR which can cause issues
  if (isPlatformServer(platformId)) {
    return true;
  }
  
  if (authService.isLoggedIn) {
    // User is logged in, so return true
    return true;
  }

  // Not logged in, redirect to login page with return URL
  router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
  return false;
};

// Role-based guard
export const roleGuard = (allowedRoles: string[]): CanActivateFn => {
  return (route, state) => {
    const router = inject(Router);
    const authService = inject(AuthService);
    const platformId = inject(PLATFORM_ID);
    
    // If we're on the server during SSR, allow navigation
    // This prevents auth redirection during SSR which can cause issues
    if (isPlatformServer(platformId)) {
      return true;
    }
    
    if (authService.isLoggedIn) {
      // Check if the user has one of the required roles
      const userRole = authService.userRole;
      if (userRole && allowedRoles.includes(userRole)) {
        return true;
      }
      
      // User doesn't have the required role, redirect to home
      router.navigate(['/home']);
      return false;
    }
    
    // Not logged in, redirect to login
    router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
    return false;
  };
};