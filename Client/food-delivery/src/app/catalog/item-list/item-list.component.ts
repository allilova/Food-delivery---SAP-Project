// src/app/catalog/item-list/item-list.component.ts
import { Component, OnInit, Input, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ApiService } from '../../api.service';
import { RestaurantService } from '../../services/restaurant.service';
import { AuthService } from '../../services/auth.service';
import { Restaurant } from '../../types/restaurants';
import { LoadingSpinnerComponent } from '../../components/loading-spinner.component';

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
  loading = false;
  error = '';
  isLoggedIn = false;

  constructor(
    private apiService: ApiService,
    private restaurantService: RestaurantService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.isLoggedIn = this.authService.isLoggedIn;
    this.loadRestaurants();
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

    // If category is selected, we could use it to filter
    // This might require a different endpoint or query parameter
    this.apiService.getRestaurants().subscribe({
      next: (restaurants) => {
        this.restaurants = restaurants;
        
        // Client-side filtering if backend doesn't support category filtering
        if (this.category) {
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
      }
    });
  }

  // Add restaurant to favorites
  addToFavorites(event: Event, restaurantId: string): void {
    event.preventDefault(); // Prevent navigation
    event.stopPropagation(); // Prevent event bubbling
    
    if (!this.isLoggedIn) {
      // Redirect to login or show login prompt
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