// src/app/search/search.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { RestaurantService } from '../services/restaurant.service';
import { AuthService } from '../services/auth.service';
import { Restaurant } from '../types/restaurants';
import { LoadingSpinnerComponent } from '../components/loading-spinner.component';

@Component({
  selector: 'app-search',
  standalone: true,
  imports: [RouterLink, CommonModule, FormsModule, LoadingSpinnerComponent],
  templateUrl: './search.component.html',
  styleUrl: './search.component.css'
})
export class SearchComponent implements OnInit {
  restaurants: Restaurant[] = [];
  filteredRestaurants: Restaurant[] = [];
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
  addressFilter: string = '';
  loading = false;
  error = '';
  isLoggedIn = false;

  constructor(
    private restaurantService: RestaurantService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.isLoggedIn = this.authService.isLoggedIn;
    this.loadAllRestaurants();
  }

  loadAllRestaurants(): void {
    this.loading = true;
    this.restaurantService.getAllRestaurants().subscribe({
      next: (data) => {
        this.restaurants = data;
        this.filteredRestaurants = [...this.restaurants];
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading restaurants:', err);
        this.error = 'Failed to load restaurants.';
        this.loading = false;
      }
    });
  }

  searchRestaurants(): void {
    if (!this.searchTerm && !this.selectedCategory && !this.addressFilter) {
      this.filteredRestaurants = [...this.restaurants];
      return;
    }

    // If search term is entered, use backend search
    if (this.searchTerm) {
      this.loading = true;
      this.restaurantService.searchRestaurants(this.searchTerm).subscribe({
        next: (results) => {
          this.filteredRestaurants = results;
          
          // Apply additional filters client-side
          if (this.selectedCategory) {
            this.filteredRestaurants = this.filteredRestaurants.filter(r => 
              r.foodType.toLowerCase() === this.selectedCategory?.toLowerCase()
            );
          }
          
          if (this.addressFilter) {
            this.filteredRestaurants = this.filteredRestaurants.filter(r => 
              r.address.toLowerCase().includes(this.addressFilter.toLowerCase())
            );
          }
          
          this.loading = false;
        },
        error: (err) => {
          console.error('Error searching restaurants:', err);
          this.error = 'Failed to search restaurants.';
          this.loading = false;
        }
      });
    } else {
      // Apply filters client-side
      this.filteredRestaurants = [...this.restaurants];
      
      if (this.selectedCategory) {
        this.filteredRestaurants = this.filteredRestaurants.filter(r => 
          r.foodType.toLowerCase() === this.selectedCategory?.toLowerCase()
        );
      }
      
      if (this.addressFilter) {
        this.filteredRestaurants = this.filteredRestaurants.filter(r => 
          r.address.toLowerCase().includes(this.addressFilter.toLowerCase())
        );
      }
    }
  }

  selectCategory(category: string): void {
    if (this.selectedCategory === category) {
      this.selectedCategory = null;
    } else {
      this.selectedCategory = category;
    }
    this.searchRestaurants();
  }

  // Add restaurant to favorites
  addToFavorites(event: Event, restaurantId: string): void {
    event.preventDefault();
    event.stopPropagation();
    
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