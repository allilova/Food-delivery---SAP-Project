// src/app/catalog/catalog.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ItemListComponent } from './item-list/item-list.component';
import { RestaurantService } from '../services/restaurant.service';

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

  constructor(private restaurantService: RestaurantService) {}

  ngOnInit(): void {
    // You could pre-load category data from the backend if needed
  }

  // Method to filter restaurants by category
  filterByCategory(category: string): void {
    this.selectedCategory = category;
    // Trigger filtering in child component
    // This would require some communication mechanism with the ItemListComponent
    // For example, using a shared service or @Input binding
  }

  // Reset category filter
  resetFilter(): void {
    this.selectedCategory = null;
  }
}