// src/app/guards/auth.guard.ts
import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { PLATFORM_ID } from '@angular/core';
import { isPlatformServer } from '@angular/common';
import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const platformId = inject(PLATFORM_ID);
  const authService = inject(AuthService);
  
  // If we're on the server during SSR, allow navigation
  if (isPlatformServer(platformId)) {
    return true;
  }
  
  // Check if the user is logged in
  if (authService.isLoggedIn) {
    console.log('Auth guard: User is logged in, allowing access');
    return true;
  }

  // Not logged in, redirect to login page with return URL
  console.log('Auth guard: User not logged in, redirecting to login');
  router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
  return false;
};

// Role-based guard
export const roleGuard = (allowedRoles: string[]): CanActivateFn => {
  return (route, state) => {
    const router = inject(Router);
    const platformId = inject(PLATFORM_ID);
    const authService = inject(AuthService);
    
    // If we're on the server during SSR, allow navigation
    if (isPlatformServer(platformId)) {
      return true;
    }
    
    // First check if user is logged in
    if (authService.isLoggedIn) {
      // Check if the user has one of the required roles
      const userRole = authService.userRole;
      console.log('Role guard: Checking if user role', userRole, 'is in allowed roles', allowedRoles);
      
      if (userRole && allowedRoles.includes(userRole)) {
        return true;
      }
      
      // User doesn't have the required role, redirect to home
      console.log('Role guard: User does not have required role, redirecting to home');
      router.navigate(['/home']);
      return false;
    }
    
    // Not logged in, redirect to login
    console.log('Role guard: User not logged in, redirecting to login');
    router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
    return false;
  };
};

// Guard to prevent restaurant owners from accessing customer pages
export const customerOnlyGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const platformId = inject(PLATFORM_ID);
  const authService = inject(AuthService);
  
  // If we're on the server during SSR, allow navigation
  if (isPlatformServer(platformId)) {
    return true;
  }
  
  // First check if user is logged in
  if (authService.isLoggedIn) {
    const userRole = authService.userRole;
    
    // Block restaurant owners and drivers, allow all other roles
    if ( userRole === 'ROLE_DRIVER') {
      console.log('Customer only guard: Blocking restaurant owner or driver from customer page');
      router.navigate(['/supplier']);
      return false;
    }
    
    console.log('Customer only guard: Allowing access for non-restaurant user');
    return true;
  }
  
  // Not logged in, redirect to login
  console.log('Customer only guard: User not logged in, redirecting to login');
  router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
  return false;
};