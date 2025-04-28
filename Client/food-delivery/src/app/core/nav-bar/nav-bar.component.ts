import { Component, OnInit } from '@angular/core';
import { RouterLink, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { CommonModule } from '@angular/common';

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

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Subscribe to auth state changes
    this.authService.currentUser.subscribe(user => {
      this.isLoggedIn = !!user;
      this.userRole = user ? user.role : null;
    });
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/']);
  }

  // Helper methods for UI
  isCustomer(): boolean {
    return this.userRole === 'ROLE_CUSTOMER';
  }

  isRestaurant(): boolean {
    return this.userRole === 'ROLE_RESTAURANT';
  }

  isAdmin(): boolean {
    return this.userRole === 'ROLE_ADMIN';
  }

  isDriver(): boolean {
    return this.userRole === 'ROLE_DRIVER';
  }
}