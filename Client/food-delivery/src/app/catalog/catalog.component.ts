// src/app/catalog/catalog.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, Router } from '@angular/router';
import { ItemListComponent } from './item-list/item-list.component';
import { RestaurantService } from '../services/restaurant.service';
import { AuthService } from '../services/auth.service';
import { USER_ROLE } from '../types/user-role.enum';

@Component({
  selector: 'app-catalog',
  standalone: true,
  imports: [ItemListComponent, RouterLink, CommonModule],
  templateUrl: './catalog.component.html',
  styleUrl: './catalog.component.css'
})
export class CatalogComponent implements OnInit {
  categories = [
    { name: 'burgers', img: 'burgers.png' },
    { name: 'pizza', img: 'pizza.png' },
    { name: 'pasta', img: 'passta.png' },
    { name: 'healthy', img: 'healty.png' },
    { name: 'asian', img: 'assian.png' },
    { name: 'desserts', img: 'desserts.png' }
  ];
  selectedCategory: string | null = null;
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
  }

  // Method to filter restaurants by category
  filterByCategory(category: string): void {
    if (this.selectedCategory === category) {
      // If clicking the already selected category, clear the filter
      this.selectedCategory = null;
    } else {
      // Otherwise set the new category filter
      this.selectedCategory = category;
    }
  }
}