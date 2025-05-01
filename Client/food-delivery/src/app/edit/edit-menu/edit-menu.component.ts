// src/app/edit/edit-menu/edit-menu.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, Router, ActivatedRoute } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { RestaurantService } from '../../services/restaurant.service';
import { AuthService } from '../../services/auth.service';
import { USER_ROLE } from '../../types/user-role.enum';
import { Food } from '../../types/food';
import { LoadingSpinnerComponent } from '../../components/loading-spinner.component';

@Component({
  selector: 'app-edit-menu',
  standalone: true,
  imports: [RouterLink, CommonModule, ReactiveFormsModule, LoadingSpinnerComponent],
  templateUrl: './edit-menu.component.html',
  styleUrl: './edit-menu.component.css'
})
export class EditMenuComponent implements OnInit {
  menuForm!: FormGroup;
  menuItem: Food | null = null;
  loading = false;
  loadingData = false;
  error = '';
  successMessage = '';
  selectedFile: File | null = null;
  imagePreview: string | null = null;
  foodId: string = '';

  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private route: ActivatedRoute,
    private restaurantService: RestaurantService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    // Check if user is logged in and has restaurant role
    if (!this.authService.isLoggedIn || 
        (this.authService.userRole !== USER_ROLE.ROLE_RESTAURANT && 
         this.authService.userRole !== USER_ROLE.ROLE_ADMIN)) {
      this.router.navigate(['/login']);
      return;
    }

    // Initialize form
    this.menuForm = this.formBuilder.group({
      foodName: ['', Validators.required],
      price: ['', [Validators.required, Validators.min(0.01)]],
      description: ['', [Validators.required, Validators.maxLength(500)]],
      portionSize: ['', [Validators.required, Validators.min(1)]]
    });

    // Get food ID from route parameters or query parameters
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) {
        this.foodId = id;
        this.loadMenuItemData(id);
      } else {
        // Check query parameters
        this.route.queryParamMap.subscribe(queryParams => {
          const queryId = queryParams.get('id');
          if (queryId) {
            this.foodId = queryId;
            this.loadMenuItemData(queryId);
          } else {
            this.error = 'No menu item ID provided.';
          }
        });
      }
    });
  }

  loadMenuItemData(foodId: string): void {
    this.loadingData = true;
    
    // In a real app, you'd have an endpoint to get a specific food item
    // This is a simplified approach
    this.restaurantService.getRestaurantMenu('1').subscribe({ // Using a dummy restaurant ID
      next: (menuItems) => {
        const item = menuItems.find(item => item.id.toString() === foodId);
        if (item) {
          this.menuItem = item;
          
          // Populate form with menu item data
          this.menuForm.patchValue({
            foodName: item.name,
            price: item.price,
            description: item.description,
            portionSize: item.preparationTime // Using preparationTime as a stand-in for portion size
          });
          
          // Set image preview if available
          this.imagePreview = item.imageUrl;
        } else {
          this.error = 'Menu item not found.';
        }
        this.loadingData = false;
      },
      error: (err) => {
        console.error('Error loading menu item data:', err);
        this.error = 'Failed to load menu item data.';
        this.loadingData = false;
      }
    });
  }

  // Handle file selection
  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];
      
      // Create preview
      const reader = new FileReader();
      reader.onload = () => {
        this.imagePreview = reader.result as string;
      };
      reader.readAsDataURL(this.selectedFile);
    }
  }

  // Submit form
  onSubmit(): void {
    if (this.menuForm.invalid || !this.foodId) {
      return;
    }

    this.loading = true;
    
    // Create data object for update
    const menuItemData = {
      foodName: this.menuForm.value.foodName,
      foodDescription: this.menuForm.value.description,
      foodPrice: parseFloat(this.menuForm.value.price),
      portionSize: parseInt(this.menuForm.value.portionSize)
    };

    // Call API to update menu item
    this.restaurantService.updateMenuItem(this.foodId, menuItemData).subscribe({
      next: (response) => {
        this.loading = false;
        this.successMessage = 'Menu item updated successfully!';
        
        // Navigate back or to another page after a delay
        setTimeout(() => {
          this.router.navigate(['/supplier']);
        }, 2000);
      },
      error: (err) => {
        this.loading = false;
        this.error = err?.error?.message || 'Failed to update menu item. Please try again.';
        console.error('Error updating menu item:', err);
      }
    });
  }
}