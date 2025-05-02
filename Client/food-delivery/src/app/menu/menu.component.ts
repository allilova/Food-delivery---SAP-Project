// src/app/menu/menu.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { RestaurantService } from '../services/restaurant.service';
import { CartService } from '../services/cart.service';
import { AuthService } from '../services/auth.service';
import { Restaurant } from '../types/restaurants';
import { Food } from '../types/food';
import { USER_ROLE } from '../types/user-role.enum';
import { LoadingSpinnerComponent } from '../components/loading-spinner.component';

@Component({
  selector: 'app-menu',
  standalone: true,
  imports: [RouterLink, CommonModule, LoadingSpinnerComponent],
  templateUrl: './menu.component.html',
  styleUrl: './menu.component.css'
})
export class MenuComponent implements OnInit {
  restaurant!: Restaurant;
  menuItems: Food[] = [];
  restaurantId: string = '';
  loading = {
    restaurant: false,
    menu: false
  };
  error = {
    restaurant: '',
    menu: ''
  };
  isLoggedIn = false;
  isRestaurantOwner = false;
  isAdmin = false;

  constructor(
    private route: ActivatedRoute,
    private restaurantService: RestaurantService,
    private cartService: CartService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    // Get authentication status and user roles
    this.isLoggedIn = this.authService.isLoggedIn;
    const userRole = this.authService.userRole;
    this.isRestaurantOwner = userRole === USER_ROLE.ROLE_RESTAURANT;
    this.isAdmin = userRole === USER_ROLE.ROLE_ADMIN;
    
    // Get restaurant ID from route parameters
    this.route.paramMap.subscribe(params => {
      const id = params.get('restaurantId');
      if (id) {
        this.restaurantId = id;
        this.loadRestaurantDetails(id);
      } else {
        this.error.restaurant = 'Restaurant ID not found in URL';
      }
    });
  }

  loadRestaurantDetails(id: string): void {
    this.loading.restaurant = true;
    this.restaurantService.getRestaurantById(id).subscribe({
      next: (restaurant) => {
        this.restaurant = restaurant;
        this.loading.restaurant = false;
        
        // After getting restaurant, load its menu
        this.loadMenuItems(id);
      },
      error: (err) => {
        console.error('Error loading restaurant details:', err);
        this.error.restaurant = 'Failed to load restaurant details.';
        this.loading.restaurant = false;
      }
    });
  }

  loadMenuItems(restaurantId: string): void {
    this.loading.menu = true;
    this.restaurantService.getRestaurantMenu(restaurantId).subscribe({
      next: (foods) => {
        this.menuItems = foods;
        this.loading.menu = false;
      },
      error: (err) => {
        console.error('Error loading menu items:', err);
        this.error.menu = 'Failed to load menu items.';
        this.loading.menu = false;
      }
    });
  }

  // Add restaurant to favorites
  addToFavorites(): void {
    if (!this.isLoggedIn) {
      return;
    }
    
    this.restaurantService.toggleFavorite(this.restaurantId).subscribe({
      next: () => {
        // Show success message or update UI
        console.log('Restaurant favorite status toggled');
      },
      error: (err) => {
        console.error('Error toggling favorite status:', err);
      }
    });
  }

  // Add menu item to cart
  addToCart(food: Food): void {
    if (!this.isLoggedIn) {
      return;
    }
    
    console.log('Adding to cart:', food);
    
    try {
      this.cartService.addToCart(food);
      // Show success notification
      alert(`Added ${food.name} to cart!`);
    } catch (error) {
      console.error('Error adding to cart:', error);
      alert('Unable to add item to cart. Please try again.');
    }
  }
  
  // Delete menu item (for admin or restaurant owner)
  deleteMenuItem(foodId: number): void {
    if (!this.isLoggedIn || (!this.isAdmin && !this.isRestaurantOwner)) {
      return;
    }
    
    if (confirm('Are you sure you want to delete this menu item?')) {
      this.restaurantService.deleteMenuItem(foodId.toString()).subscribe({
        next: () => {
          // Remove item from local array to update UI immediately
          this.menuItems = this.menuItems.filter(item => item.id !== foodId);
          console.log('Menu item deleted successfully');
        },
        error: (err) => {
          console.error('Error deleting menu item:', err);
          // Could show an error notification here
        }
      });
    }
  }
  
  // Toggle food item availability
  toggleAvailability(food: Food): void {
    if (!this.isLoggedIn || (!this.isAdmin && !this.isRestaurantOwner)) {
      return;
    }
    
    this.restaurantService.toggleFoodAvailability(food.id.toString()).subscribe({
      next: (updatedFood) => {
        // Update the item in the local array
        const index = this.menuItems.findIndex(item => item.id === food.id);
        if (index !== -1) {
          this.menuItems[index].isAvailable = updatedFood.isAvailable;
        }
        console.log('Food availability toggled successfully');
      },
      error: (err) => {
        console.error('Error toggling food availability:', err);
        // Could show an error notification here
      }
    });
  }
}