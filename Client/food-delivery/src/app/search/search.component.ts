import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { RestaurantService } from '../services/restaurant.service';
import { AuthService } from '../services/auth.service';
import { NotificationService } from '../services/notification.service';
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

  userFavorites: Restaurant[] = [];

  constructor(
    private restaurantService: RestaurantService,
    private authService: AuthService,
    private router: Router,
    private notificationService: NotificationService
  ) {
    console.log('Search component initialized');
  }

  ngOnInit(): void {
    this.isLoggedIn = this.authService.isLoggedIn;
    this.loadAllRestaurants();
    
    // If user is logged in, load their favorites
    if (this.isLoggedIn) {
      this.loadUserFavorites();
    }
  }
  
  loadUserFavorites(): void {
    this.restaurantService.getUserFavorites().subscribe({
      next: (favorites) => {
        this.userFavorites = favorites;
        console.log('Loaded user favorites:', this.userFavorites);
      },
      error: (err) => {
        console.error('Error loading user favorites:', err);
      }
    });
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
    console.log('Search triggered with:', { 
      searchTerm: this.searchTerm, 
      category: this.selectedCategory, 
      address: this.addressFilter 
    });
    
    // Reset any previous errors
    this.error = '';
    
    if (!this.searchTerm && !this.selectedCategory && !this.addressFilter) {
      console.log('No search criteria, showing all restaurants');
      this.filteredRestaurants = [...this.restaurants];
      return;
    }

    // If search term is entered, use backend search
    if (this.searchTerm) {
      this.loading = true;
      console.log(`Using search API with keyword: "${this.searchTerm}"`);
      
      this.restaurantService.searchRestaurants(this.searchTerm).subscribe({
        next: (results) => {
          console.log(`Search returned ${results.length} results`);
          this.filteredRestaurants = results;
          
          // Apply additional filters client-side
          if (this.selectedCategory) {
            console.log(`Filtering by category: ${this.selectedCategory}`);
            this.filteredRestaurants = this.filteredRestaurants.filter(r => 
              r.foodType?.toLowerCase() === this.selectedCategory?.toLowerCase()
            );
            console.log(`${this.filteredRestaurants.length} results after category filter`);
          }
          
          if (this.addressFilter) {
            console.log(`Filtering by address: ${this.addressFilter}`);
            this.filteredRestaurants = this.filteredRestaurants.filter(r => 
              r.address.toLowerCase().includes(this.addressFilter.toLowerCase())
            );
            console.log(`${this.filteredRestaurants.length} results after address filter`);
          }
          
          this.loading = false;
        },
        error: (err) => {
          console.error('Error searching restaurants:', err);
          this.error = 'Failed to search restaurants. Please try again.';
          this.loading = false;
        }
      });
    } else {
      console.log('Using client-side filtering only');
      // Apply filters client-side
      this.filteredRestaurants = [...this.restaurants];
      
      if (this.selectedCategory) {
        console.log(`Filtering by category: ${this.selectedCategory}`);
        this.filteredRestaurants = this.filteredRestaurants.filter(r => 
          r.foodType?.toLowerCase() === this.selectedCategory?.toLowerCase()
        );
        console.log(`${this.filteredRestaurants.length} results after category filter`);
      }
      
      if (this.addressFilter) {
        console.log(`Filtering by address: ${this.addressFilter}`);
        this.filteredRestaurants = this.filteredRestaurants.filter(r => 
          r.address.toLowerCase().includes(this.addressFilter.toLowerCase())
        );
        console.log(`${this.filteredRestaurants.length} results after address filter`);
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

  // Check if a restaurant is in favorites
  isFavorite(restaurantId: string): boolean {
    return this.userFavorites.some(restaurant => restaurant.id === restaurantId);
  }
  
  // Add or remove restaurant from favorites
  addToFavorites(event: Event, restaurantId: string): void {
    event.preventDefault();
    event.stopPropagation();
    
    console.log(`Toggle favorite for restaurant ${restaurantId}`);
    
    if (!this.isLoggedIn) {
      console.log('User not logged in, redirecting to login');
      this.notificationService.info('Please log in to add restaurants to favorites');
      this.router.navigate(['/login']);
      return;
    }
    
    // Show loading state on the button
    const button = event.target as HTMLElement;
    const originalText = button.innerText;
    button.innerHTML = '<i class="fa-solid fa-spinner fa-spin"></i> Processing...';
    button.setAttribute('disabled', 'true');
    
    // Check if it was already a favorite before calling API
    const wasAlreadyFavorite = this.isFavorite(restaurantId);
    console.log(`Restaurant ${restaurantId} was ${wasAlreadyFavorite ? 'already' : 'not'} a favorite`);
    
    // Call the toggle favorite API
    this.restaurantService.toggleFavorite(restaurantId).subscribe({
      next: () => {
        console.log('Favorite toggled successfully');
        
        // Update local favorites cache first for immediate UI feedback
        if (wasAlreadyFavorite) {
          // Remove from local favorites
          this.userFavorites = this.userFavorites.filter(r => r.id.toString() !== restaurantId.toString());
          console.log(`Removed restaurant ${restaurantId} from local favorites`);
        } else {
          // Find the restaurant in the filtered list or all restaurants
          const restaurant = this.filteredRestaurants.find(r => r.id.toString() === restaurantId.toString()) || 
                             this.restaurants.find(r => r.id.toString() === restaurantId.toString());
          
          if (restaurant) {
            this.userFavorites.push(restaurant);
            console.log(`Added restaurant ${restaurantId} to local favorites`);
          }
        }
        
        // Make sure session storage is updated
        try {
          sessionStorage.setItem('userFavorites', JSON.stringify(this.userFavorites));
        } catch (e) {
          console.error('Error updating favorites in session storage:', e);
        }
        
        // Also refresh the server-side favorites list
        this.loadUserFavorites();
        
        // Show success message
        if (wasAlreadyFavorite) {
          this.notificationService.success('Restaurant removed from favorites');
        } else {
          this.notificationService.success('Restaurant added to favorites');
        }
        
        // Reset button state
        setTimeout(() => {
          button.removeAttribute('disabled');
        }, 500);
      },
      error: (err) => {
        console.error('Error updating favorites:', err);
        this.notificationService.error('Failed to update favorites. Please try again.');
        
        // Reset button state
        button.innerHTML = originalText;
        button.removeAttribute('disabled');
      }
    });
  }
}