// src/app/catalog/catalog.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, Router } from '@angular/router';
import { RestaurantService } from '../services/restaurant.service';
import { AuthService } from '../services/auth.service';
import { Restaurant } from '../types/restaurants';
import { USER_ROLE } from '../types/user-role.enum';
import { LoadingSpinnerComponent } from '../components/loading-spinner.component';
import { ItemListComponent } from './item-list/item-list.component';

@Component({
  selector: 'app-catalog',
  standalone: true,
  imports: [RouterLink, CommonModule, LoadingSpinnerComponent, ItemListComponent],
  templateUrl: './catalog.component.html',
  styleUrl: './catalog.component.css'
})
export class CatalogComponent implements OnInit {
  restaurants: Restaurant[] = [];
  categories = [
    { name: 'burgers', img: 'burgers.png' },
    { name: 'pizza', img: 'pizza.png' },
    { name: 'pasta', img: 'passta.png' },
    { name: 'healthy', img: 'healty.png' },
    { name: 'asian', img: 'assian.png' },
    { name: 'desserts', img: 'desserts.png' }
  ];
  selectedCategory: string | null = null;
  searchTerm: string = '';
  loading = false;
  error = '';
  isLoggedIn = false;
  isRestaurantOwner = false;

  constructor(
    private restaurantService: RestaurantService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Check if user is logged in
    this.isLoggedIn = this.authService.isLoggedIn;
    
    // If not logged in, redirect to login page
    if (!this.isLoggedIn) {
      this.router.navigate(['/login'], { queryParams: { returnUrl: '/catalog' } });
      return;
    }
    
    // Check if user is a restaurant owner
    const userRole = this.authService.userRole;
    this.isRestaurantOwner = userRole === USER_ROLE.ROLE_RESTAURANT;
    
    // If user is a restaurant owner, redirect to their dashboard
    if (this.isRestaurantOwner) {
      this.router.navigate(['/supplier']);
      return;
    }

    // Load all restaurants
    this.loadRestaurants();
  }

  loadRestaurants(): void {
    this.loading = true;
    this.restaurants = [];
    this.error = '';

    this.restaurantService.getAllRestaurants().subscribe({
      next: (restaurants) => {
        this.restaurants = restaurants;
        
        if (this.selectedCategory) {
          this.filterByCategory(this.selectedCategory);
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

  // Method to filter restaurants by category
  filterByCategory(category: string): void {
    if (this.selectedCategory === category) {
      // If clicking the already selected category, clear the filter
      this.selectedCategory = null;
      this.loadRestaurants();
    } else {
      // Otherwise set the new category filter
      this.selectedCategory = category;
      
      // Filter the restaurants locally
      if (this.restaurants.length > 0) {
        this.restaurants = this.restaurants.filter(r => 
          r.foodType && r.foodType.toLowerCase() === category.toLowerCase()
        );
      }
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