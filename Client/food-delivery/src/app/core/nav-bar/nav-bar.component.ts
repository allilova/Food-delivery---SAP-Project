// src/app/core/nav-bar/nav-bar.component.ts
import { Component, OnInit, OnDestroy } from '@angular/core';
import { RouterLink, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { CommonModule } from '@angular/common';
import { CartService } from '../../services/cart.service';
import { USER_ROLE } from '../../types/user-role.enum';
import { Subscription } from 'rxjs';

@Component({
  selector: 'nav-bar-root',
  standalone: true,
  imports: [RouterLink, CommonModule],
  templateUrl: './nav-bar.component.html',
  styleUrl: './nav.bar.component.css'
})
export class NavigationBarComponent implements OnInit, OnDestroy {
  isLoggedIn = false;
  userRole: USER_ROLE | null = null;
  cartItemsCount = 0;
  private userSubscription: Subscription | null = null;
  private cartSubscription: Subscription | null = null;

  constructor(
    private authService: AuthService,
    private cartService: CartService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Get initial auth state
    this.isLoggedIn = this.authService.isLoggedIn;
    this.userRole = this.authService.userRole;
    
    // Subscribe to auth state changes
    this.userSubscription = this.authService.currentUser.subscribe(user => {
      console.log('Auth state changed:', user);
      this.isLoggedIn = !!user;
      const previousRole = this.userRole;
      this.userRole = user ? user.role as USER_ROLE : null;
      
      // If user logged in as customer, load cart
      if (this.isLoggedIn && this.userRole === USER_ROLE.ROLE_CUSTOMER) {
        // Only fetch cart if user is newly logged in or just changed to customer role
        if (!previousRole || previousRole !== USER_ROLE.ROLE_CUSTOMER) {
          this.cartService.loadCartFromBackend();
        }
      }
    });

    // Subscribe to cart updates for badge count
    this.cartSubscription = this.cartService.cartItems.subscribe(items => {
      this.cartItemsCount = items.reduce((count, item) => count + item.quantity, 0);
    });

    // Load cart from backend ONLY if logged in as CUSTOMER
    if (this.isLoggedIn && this.userRole === USER_ROLE.ROLE_CUSTOMER) {
      this.cartService.loadCartFromBackend();
    }
    
    console.log('Nav bar initialized, logged in:', this.isLoggedIn, 'role:', this.userRole);
  }
  
  ngOnDestroy(): void {
    // Clean up subscriptions to prevent memory leaks
    if (this.userSubscription) {
      this.userSubscription.unsubscribe();
    }
    if (this.cartSubscription) {
      this.cartSubscription.unsubscribe();
    }
  }

  logout(): void {
    console.log('Logging out...');
    this.authService.logout();
    this.router.navigate(['/']);
  }

  // Helper methods for UI
  isCustomer(): boolean {
    return this.userRole === USER_ROLE.ROLE_CUSTOMER;
  }

  isRestaurant(): boolean {
    return this.userRole === USER_ROLE.ROLE_RESTAURANT;
  }

  isAdmin(): boolean {
    return this.userRole === USER_ROLE.ROLE_ADMIN;
  }

  isDriver(): boolean {
    return this.userRole === USER_ROLE.ROLE_DRIVER;
  }
}