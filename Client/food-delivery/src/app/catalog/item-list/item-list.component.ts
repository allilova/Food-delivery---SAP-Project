// src/app/catalog/item-list/item-list.component.ts
import { Component, OnInit, Input, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, Router } from '@angular/router';
import { RestaurantService } from '../../services/restaurant.service';
import { AuthService } from '../../services/auth.service';
import { Restaurant } from '../../types/restaurants';
import { LoadingSpinnerComponent } from '../../components/loading-spinner.component';
import { USER_ROLE } from '../../types/user-role.enum';

@Component({
  selector: 'app-item-list',
  standalone: true,
  imports: [RouterLink, CommonModule, LoadingSpinnerComponent],
  templateUrl: './item-list.component.html',
  styleUrl: './item-list.component.css'
})
export class ItemListComponent implements OnInit, OnChanges {
  @Input() category: string | null = null;
  
  restaurants: Restaurant[] = [];
  favoriteRestaurantIds: Set<string> = new Set();
  loading = false;
  error = '';
  isLoggedIn = false;
  isAdmin = false;

  constructor(
    private restaurantService: RestaurantService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.isLoggedIn = this.authService.isLoggedIn;
    this.isAdmin = this.authService.userRole === USER_ROLE.ROLE_ADMIN;
    this.loadRestaurants();
    if (this.isLoggedIn) {
      this.loadFavorites();
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    // If category changes, reload restaurants with filter
    if (changes['category']) {
      this.loadRestaurants();
    }
  }

  loadRestaurants(): void {
    this.loading = true;
    this.error = '';

    this.restaurantService.getAllRestaurants().subscribe({
      next: (restaurants) => {
        this.restaurants = restaurants || [];
        
        // Client-side filtering if a category is selected
        if (this.category && this.restaurants.length > 0) {
          this.restaurants = this.restaurants.filter(r => 
            r.foodType && r.foodType.toLowerCase() === this.category?.toLowerCase()
          );
        }
        
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading restaurants:', err);
        this.error = 'Failed to load restaurants. Please try again later.';
        this.loading = false;
        this.restaurants = []; // Empty array when there's an error
      }
    });
  }

  // Load user's favorite restaurants
  loadFavorites(): void {
    if (!this.isLoggedIn) {
      return;
    }

    // Use the restaurant service to get user favorites
    this.restaurantService.getUserFavorites().subscribe({
      next: (favorites) => {
        if (favorites && favorites.length) {
          this.favoriteRestaurantIds = new Set(
            favorites.map(restaurant => restaurant.id.toString())
          );
        }
      },
      error: (err) => {
        console.error('Error loading favorites:', err);
        // Create empty set if there was an error
        this.favoriteRestaurantIds = new Set();
      }
    });
  }

  // Check if a restaurant is in favorites
  isFavorite(restaurantId: string): boolean {
    return this.favoriteRestaurantIds.has(restaurantId);
  }

  // Add or remove restaurant from favorites
  toggleFavorite(event: Event, restaurantId: string): void {
    event.preventDefault(); // Prevent navigation
    event.stopPropagation(); // Prevent event bubbling
    
    if (!this.isLoggedIn) {
      return;
    }
    
    this.restaurantService.toggleFavorite(restaurantId).subscribe({
      next: () => {
        // Toggle in local state
        if (this.favoriteRestaurantIds.has(restaurantId)) {
          this.favoriteRestaurantIds.delete(restaurantId);
        } else {
          this.favoriteRestaurantIds.add(restaurantId);
        }
      },
      error: (err) => {
        console.error('Error toggling favorite status:', err);
      }
    });
  }

  // Delete restaurant (Admin only)
  deleteRestaurant(event: Event, restaurantId: string): void {
    event.preventDefault();
    event.stopPropagation();
    
    if (!this.isAdmin) {
      return;
    }
    
    if (confirm('Are you sure you want to delete this restaurant?')) {
      this.restaurantService.deleteRestaurant(restaurantId).subscribe({
        next: () => {
          // Remove from local array
          this.restaurants = this.restaurants.filter(r => r.id !== restaurantId);
        },
        error: (err) => {
          console.error('Error deleting restaurant:', err);
        }
      });
    }
  }

  // Add restaurant to favorites
  addToFavorites(restaurantId: string): void {
    if (!this.isLoggedIn) {
      this.router.navigate(['/login']);
      return;
    }
    
    this.restaurantService.toggleFavorite(restaurantId).subscribe({
      next: () => {
        // Show success message or update UI
        console.log('Restaurant added to favorites');
      },
      error: (err) => {
        console.error('Error adding to favorites:', err);
      }
    });
  }
}