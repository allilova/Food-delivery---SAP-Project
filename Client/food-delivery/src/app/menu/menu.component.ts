import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink, Router } from '@angular/router';
import { RestaurantService } from '../services/restaurant.service';
import { CartService } from '../services/cart.service';
import { AuthService } from '../services/auth.service';
import { NotificationService } from '../services/notification.service';
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
  isDriver = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private restaurantService: RestaurantService,
    private cartService: CartService,
    private authService: AuthService,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    // Get authentication status and user roles
    this.isLoggedIn = this.authService.isLoggedIn;
    const userRole = this.authService.userRole;
    this.isRestaurantOwner = userRole === USER_ROLE.ROLE_RESTAURANT;
    this.isAdmin = userRole === USER_ROLE.ROLE_ADMIN;
    this.isDriver = userRole === USER_ROLE.ROLE_DRIVER;
    
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
    this.error.menu = '';
    this.menuItems = []; // Clear existing items first
    
    console.log('Loading menu items for restaurant:', restaurantId);
    
    // Use restaurant service to get menu items
    this.restaurantService.getRestaurantMenu(restaurantId).subscribe({
      next: (foods) => {
        console.log('Menu items loaded successfully:', foods.length, 'items');
        this.menuItems = foods;
        this.loading.menu = false;
        
        // For newly created restaurants, we want to show an empty state 
        // so they can add their first menu item, so we DON'T use mock data here
      },
      error: (err) => {
        console.error('Error loading menu items:', err);
        
        // Check if we're logged in as a restaurant owner, admin, or driver
        // If so, we want to show an empty state for newly created restaurants
        if (this.isRestaurantOwner || this.isAdmin || this.isDriver) {
          console.log('Restaurant owner/admin/driver viewing - showing empty menu state');
          this.menuItems = []; // Keep the menu empty to show the empty state with "Add first item" button
          this.loading.menu = false;
          this.error.menu = ''; // Clear any error message
        } else {
          // For customers, we can fall back to mock data to provide a better experience
          console.log('Customer viewing - falling back to mock data');
          import('../mock-models/sample-models').then(models => {
            // Only use a subset of mock data to make it obvious it's demo data
            const mockData = JSON.parse(JSON.stringify(models.mockMenuItems));
            this.menuItems = mockData.slice(0, 2); // Just show 2 items to make it obvious it's demo data
            this.loading.menu = false;
            this.error.menu = ''; // Clear error message
          }).catch(importErr => {
            console.error('Error loading mock data:', importErr);
            this.error.menu = 'Failed to load menu items.';
            this.loading.menu = false;
          });
        }
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
  
  // Edit restaurant
  editRestaurant(): void {
    if (!this.isLoggedIn || (!this.isAdmin && !this.isRestaurantOwner && !this.isDriver)) {
      return;
    }
    
    console.log('Navigating to edit restaurant page, id:', this.restaurantId);
    this.router.navigate(['/edit-restaurant', this.restaurantId]);
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
  
  // Navigate to edit menu item page
  editMenuItem(food: Food): void {
    if (!this.isLoggedIn || (!this.isAdmin && !this.isRestaurantOwner && !this.isDriver)) {
      return;
    }
    
    console.log('Navigating to edit menu item, food:', food);
    
    // Store the food item in session storage to make it available to the edit component
    try {
      sessionStorage.setItem('editMenuItem', JSON.stringify(food));
    } catch (e) {
      console.error('Error storing menu item in session storage:', e);
    }
    
    // Navigate to the edit menu page
    this.router.navigate(['/edit-menu', food.id]);
  }
  
  // Delete menu item (for admin or restaurant owner)
  deleteMenuItem(foodId: number): void {
    if (!this.isLoggedIn || (!this.isAdmin && !this.isRestaurantOwner)) {
      return;
    }
    
    if (confirm('Are you sure you want to delete this menu item?')) {
      // For demo purposes, just update the UI without making an API call
      this.menuItems = this.menuItems.filter(item => item.id !== foodId);
      this.notificationService.success('Menu item deleted successfully');
      console.log('Menu item deleted successfully');
      
      // Store deleted item IDs in session storage
      try {
        const deletedIds = JSON.parse(sessionStorage.getItem('deletedMenuItems') || '[]');
        deletedIds.push(foodId);
        sessionStorage.setItem('deletedMenuItems', JSON.stringify(deletedIds));
      } catch (e) {
        console.error('Error storing deleted menu item ID in session storage:', e);
      }
    }
  }
  
  // Toggle food item availability
  toggleAvailability(food: Food): void {
    if (!this.isLoggedIn || (!this.isAdmin && !this.isRestaurantOwner && !this.isDriver)) {
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
        
      }
    });
  }
}