// src/app/guards/auth.guard.ts
import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { PLATFORM_ID } from '@angular/core';
import { isPlatformServer } from '@angular/common';

// Function to check if user is logged in
const isLoggedIn = (): boolean => {
  return !!localStorage.getItem('jwt_token');
};

// Function to get user role
const getUserRole = (): string | null => {
  const userData = localStorage.getItem('currentUser');
  if (!userData) return null;
  
  try {
    const user = JSON.parse(userData);
    return user.role;
  } catch (e) {
    console.error('Error parsing user data:', e);
    return null;
  }
};

// Auth guard for protected routes
export const authGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const platformId = inject(PLATFORM_ID);
  
  // If we're on the server during SSR, allow navigation
  if (isPlatformServer(platformId)) {
    return true;
  }
  
  if (isLoggedIn()) {
    // User is logged in, allow access
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
    const platformId = inject(PLATFORM_ID);
    
    // If we're on the server during SSR, allow navigation
    if (isPlatformServer(platformId)) {
      return true;
    }
    
    if (isLoggedIn()) {
      // Check if the user has one of the required roles
      const userRole = getUserRole();
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