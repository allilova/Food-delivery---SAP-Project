// src/app/menu/menu-item/menu-item.component.ts
import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { RestaurantService } from '../../services/restaurant.service';
import { CartService } from '../../services/cart.service';
import { AuthService } from '../../services/auth.service';
import { Food } from '../../types/food';
import { USER_ROLE } from '../../types/user-role.enum';
import { LoadingSpinnerComponent } from '../../components/loading-spinner.component';

@Component({
  selector: 'app-menu-item',
  standalone: true,
  imports: [RouterLink, CommonModule, LoadingSpinnerComponent],
  templateUrl: './menu-item.component.html',
  styleUrl: './menu-item.component.css'
})
export class MenuItemComponent implements OnInit {
  @Input() menuItems: Food[] = [];
  @Input() restaurantId: string = '';
  
  isLoggedIn = false;
  isRestaurantOwner = false;
  isAdmin = false;
  loading = false;
  error = '';

  constructor(
    private restaurantService: RestaurantService,
    private cartService: CartService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.isLoggedIn = this.authService.isLoggedIn;
    
    // Check user role to enable restaurant owner features
    const userRole = this.authService.userRole;
    this.isRestaurantOwner = userRole === USER_ROLE.ROLE_RESTAURANT;
    this.isAdmin = userRole === USER_ROLE.ROLE_ADMIN;
  }

  // Add item to cart
  addToCart(food: Food): void {
    if (!this.isLoggedIn) {
      // Redirect to login or show login prompt
      return;
    }
    
    this.cartService.addToCart(food);
    // Show success message or feedback
  }

  // For restaurant owners: delete menu item
  deleteMenuItem(event: Event, foodId: string): void {
    event.preventDefault();
    event.stopPropagation();
    
    if (!this.isRestaurantOwner && !this.isAdmin) {
      return;
    }
    
    if (confirm('Are you sure you want to delete this item?')) {
      this.loading = true;
      this.restaurantService.deleteMenuItem(foodId).subscribe({
        next: () => {
          // Remove the item from the local array
          this.menuItems = this.menuItems.filter(item => item.id.toString() !== foodId);
          this.loading = false;
        },
        error: (err) => {
          console.error('Error deleting menu item:', err);
          this.error = 'Failed to delete menu item.';
          this.loading = false;
        }
      });
    }
  }

  // For restaurant owners: toggle food availability
  toggleAvailability(event: Event, foodId: string): void {
    event.preventDefault();
    event.stopPropagation();
    
    if (!this.isRestaurantOwner && !this.isAdmin) {
      return;
    }
    
    this.loading = true;
    this.restaurantService.toggleFoodAvailability(foodId).subscribe({
      next: (updatedFood) => {
        // Update the item in the local array
        const index = this.menuItems.findIndex(item => item.id.toString() === foodId);
        if (index !== -1) {
          this.menuItems[index] = updatedFood;
        }
        this.loading = false;
      },
      error: (err) => {
        console.error('Error toggling food availability:', err);
        this.error = 'Failed to update menu item.';
        this.loading = false;
      }
    });
  }
}