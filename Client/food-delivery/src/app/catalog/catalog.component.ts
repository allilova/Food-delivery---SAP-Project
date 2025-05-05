import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, Router } from '@angular/router';
import { RestaurantService } from '../services/restaurant.service';
import { AuthService } from '../services/auth.service';
import { NotificationService } from '../services/notification.service';
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
  isDriver = false;

  constructor(
    private restaurantService: RestaurantService,
    private authService: AuthService,
    private router: Router,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    // Check if user is logged in
    this.isLoggedIn = this.authService.isLoggedIn;
    
    // Check user role - we need to check if user is admin too since admins can create restaurants
    if (this.isLoggedIn) {
      const userRole = this.authService.userRole;
      this.isRestaurantOwner = userRole === USER_ROLE.ROLE_RESTAURANT;
      this.isDriver = userRole === USER_ROLE.ROLE_DRIVER;
      
      
      if (this.isDriver && userRole !== USER_ROLE.ROLE_ADMIN) {
        this.router.navigate(['/supplier']);
        return;
      }
    }

    // Check for success message from restaurant creation
    const successParam = new URLSearchParams(window.location.search).get('success');
    if (successParam === 'restaurant_created') {
      if (this.notificationService) {
        this.notificationService.success('Restaurant created successfully! It is now listed in the catalog.');
      }
      // Remove the query parameter from URL without page reload
      window.history.replaceState({}, document.title, window.location.pathname);
    }

    // Load all restaurants (for both logged in and non-logged in users)
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
    
    // Show loading state
    const targetBtn = document.querySelector(`.add-favorite[data-restaurant-id="${restaurantId}"]`) as HTMLButtonElement;
    if (targetBtn) {
      targetBtn.disabled = true;
      targetBtn.innerHTML = '<i class="fa-solid fa-spinner fa-spin"></i> Adding...';
    }
    
    this.restaurantService.toggleFavorite(restaurantId).subscribe({
      next: () => {
        // Update UI to show success
        if (targetBtn) {
          targetBtn.disabled = false;
          targetBtn.innerHTML = '<i class="fa-solid fa-heart" style="color: red;"></i> Favorited';
          targetBtn.classList.add('favorited');
        }
        // Show success notification via a service
        if (this.notificationService) {
          this.notificationService.success('Restaurant added to favorites successfully!');
        } else {
          console.log('Restaurant added to favorites');
        }
      },
      error: (err) => {
        console.error('Error adding to favorites:', err);
        if (targetBtn) {
          targetBtn.disabled = false;
          targetBtn.innerHTML = '<i class="fa-solid fa-heart"></i> Favorite';
        }
        // Show error notification
        if (this.notificationService) {
          this.notificationService.error('Failed to add restaurant to favorites. Please try again.');
        }
      }
    });
  }
}