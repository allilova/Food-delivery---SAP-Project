// src/app/user/profile/profile.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { RestaurantService } from '../../services/restaurant.service';
import { OrderService } from '../../services/order.service';
import { User } from '../../types/user';
import { Restaurant } from '../../types/restaurants';
import { Order } from '../../types/order';
import { LoadingSpinnerComponent } from '../../components/loading-spinner.component';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, RouterLink, LoadingSpinnerComponent],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent implements OnInit {
  user: User | null = null;
  favoriteRestaurants: Restaurant[] = [];
  previousOrders: Order[] = [];
  loading = {
    profile: false,
    favorites: false,
    orders: false
  };
  error = {
    profile: '',
    favorites: '',
    orders: ''
  };

  constructor(
    private authService: AuthService,
    private restaurantService: RestaurantService,
    private orderService: OrderService
  ) {}

  ngOnInit(): void {
    this.loadUserProfile();
    this.loadFavoriteRestaurants();
    this.loadPreviousOrders();
  }

  loadUserProfile(): void {
    this.loading.profile = true;
    this.authService.getUserProfile().subscribe({
      next: (profile) => {
        this.user = profile;
        this.loading.profile = false;
      },
      error: (err) => {
        console.error('Error loading user profile:', err);
        this.error.profile = 'Failed to load profile information.';
        this.loading.profile = false;
      }
    });
  }

  loadFavoriteRestaurants(): void {
    this.loading.favorites = true;
    // Depending on your API, you might need to adjust this to get user favorites
    this.restaurantService.getAllRestaurants().subscribe({
      next: (restaurants) => {
        // This is just a placeholder - your backend might have a specific endpoint for favorites
        // or you might need to filter the restaurants based on user favorites
        this.favoriteRestaurants = restaurants.slice(0, 3); // Just showing 3 for example
        this.loading.favorites = false;
      },
      error: (err) => {
        console.error('Error loading favorite restaurants:', err);
        this.error.favorites = 'Failed to load favorite restaurants.';
        this.loading.favorites = false;
      }
    });
  }

  loadPreviousOrders(): void {
    this.loading.orders = true;
    this.orderService.getUserOrders().subscribe({
      next: (orders) => {
        this.previousOrders = orders;
        this.loading.orders = false;
      },
      error: (err) => {
        console.error('Error loading previous orders:', err);
        this.error.orders = 'Failed to load order history.';
        this.loading.orders = false;
      }
    });
  }

  // This is used to navigate to editing the profile
  editProfile(): void {
    // Implement navigation to edit profile page
    // or show edit form in a modal, etc.
  }
}