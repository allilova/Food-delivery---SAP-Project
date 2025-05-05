import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { RestaurantService } from '../../services/restaurant.service';
import { OrderService } from '../../services/order.service';
import { CartService } from '../../services/cart.service';
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
    private orderService: OrderService,
    private cartService: CartService,
    private router: Router
  ) {
    console.log('Profile component initialized');
  }

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
    this.restaurantService.getUserFavorites().subscribe({
      next: (restaurants) => {
        this.favoriteRestaurants = restaurants;
        console.log('Loaded user favorite restaurants:', this.favoriteRestaurants);
        
        // Store in session storage for consistent display across components
        try {
          sessionStorage.setItem('userFavorites', JSON.stringify(restaurants));
        } catch (e) {
          console.error('Error storing favorites in session storage:', e);
        }
        
        this.loading.favorites = false;
      },
      error: (err) => {
        console.error('Error loading favorite restaurants:', err);
        this.error.favorites = 'Failed to load favorite restaurants.';
        this.loading.favorites = false;
        
        // Try to get from session storage as fallback
        try {
          const storedFavorites = sessionStorage.getItem('userFavorites');
          if (storedFavorites) {
            this.favoriteRestaurants = JSON.parse(storedFavorites);
            console.log('Loaded favorites from session storage as fallback');
            this.error.favorites = ''; // Clear error if we got data from storage
          }
        } catch (e) {
          console.error('Error reading favorites from session storage:', e);
        }
      }
    });
  }
  
  // Remove a restaurant from favorites
  removeFromFavorites(restaurantId: string): void {
    this.restaurantService.toggleFavorite(restaurantId).subscribe({
      next: () => {
        // Filter out the removed restaurant
        this.favoriteRestaurants = this.favoriteRestaurants.filter(r => r.id !== restaurantId);
        
        // Update session storage
        try {
          sessionStorage.setItem('userFavorites', JSON.stringify(this.favoriteRestaurants));
        } catch (e) {
          console.error('Error updating favorites in session storage:', e);
        }
      },
      error: (err) => {
        console.error('Error removing from favorites:', err);
      }
    });
  }

  loadPreviousOrders(): void {
    this.loading.orders = true;
    this.orderService.getUserOrders(0, 5).subscribe({
      next: (response) => {
        // Check if response has content property (paginated response)
        if (response && response.content) {
          this.previousOrders = response.content;
        } else if (Array.isArray(response)) {
          // If response is an array, use it directly
          this.previousOrders = response;
        } else {
          this.previousOrders = [];
        }
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
    // For now, we'll just navigate to a hypothetical edit profile page
    // We can implement this later
    console.log('Edit profile requested');
    // Uncomment when edit profile page is created
    // this.router.navigate(['/user/edit-profile']);
  }
  
  // Add an item from a previous order to cart
  reorderItem(item: any): void {
    if (item && item.food) {
      // Call addToCart with food object and quantity
      this.cartService.addToCart(item.food, 1);
      console.log('Item added to cart');
      // Optional: navigate to cart
      // this.router.navigate(['/cart']);
    }
  }
}