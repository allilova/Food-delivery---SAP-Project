// src/app/core/nav-bar/nav-bar.component.ts
import { Component, OnInit } from '@angular/core';
import { RouterLink, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { CommonModule } from '@angular/common';
import { CartService } from '../../services/cart.service';
import { USER_ROLE } from '../../types/user-role.enum';

@Component({
  selector: 'nav-bar-root',
  standalone: true,
  imports: [RouterLink, CommonModule],
  templateUrl: './nav-bar.component.html',
  styleUrl: './nav.bar.component.css'
})
export class NavigationBarComponent implements OnInit {
  isLoggedIn = false;
  userRole: string | null = null;
  cartItemsCount = 0;

  constructor(
    private authService: AuthService,
    private cartService: CartService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Subscribe to auth state changes
    this.authService.currentUser.subscribe(user => {
      this.isLoggedIn = !!user;
      this.userRole = user ? user.role : null;
    });

    // Subscribe to cart updates for badge count
    this.cartService.cartItems.subscribe(items => {
      this.cartItemsCount = items.length;
    });
  }

  logout(): void {
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