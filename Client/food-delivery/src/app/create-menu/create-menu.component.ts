// src/app/create-menu/create-menu.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { RestaurantService } from '../services/restaurant.service';
import { AuthService } from '../services/auth.service';
import { USER_ROLE } from '../types/user-role.enum';
import { LoadingSpinnerComponent } from '../components/loading-spinner.component';

@Component({
  selector: 'app-create-menu',
  standalone: true,
  imports: [RouterLink, CommonModule, ReactiveFormsModule, LoadingSpinnerComponent],
  templateUrl: './create-menu.component.html',
  styleUrl: './create-menu.component.css'
})
export class CreateMenuComponent implements OnInit {
  menuForm!: FormGroup;
  loading = false;
  error = '';
  successMessage = '';
  selectedFile: File | null = null;
  imagePreview: string | null = null;

  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
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
      portionSize: ['', [Validators.required, Validators.min(1)]],
      categoryId: ['', Validators.required],
      preparationTime: ['', [Validators.required, Validators.min(1)]]
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
    if (this.menuForm.invalid) {
      return;
    }

    this.loading = true;
    
    // Create FormData object to send file
    const formData = new FormData();
    
    // Add form fields
    Object.keys(this.menuForm.value).forEach(key => {
      formData.append(key, this.menuForm.value[key]);
    });
    
    // Add file if selected
    if (this.selectedFile) {
      formData.append('foodImage', this.selectedFile);
    }

    // Convert to format expected by backend
    const menuData = {
      foodName: this.menuForm.value.foodName,
      foodDescription: this.menuForm.value.description,
      foodPrice: parseFloat(this.menuForm.value.price),
      preparationTime: parseInt(this.menuForm.value.preparationTime),
      categoryId: parseInt(this.menuForm.value.categoryId),
      portionSize: parseInt(this.menuForm.value.portionSize),
      // Additional fields would go here if needed by the backend
    };

    // Call API to create menu item
    this.restaurantService.createMenuItem(menuData).subscribe({
      next: (response) => {
        this.loading = false;
        this.successMessage = 'Menu item created successfully!';
        
        // Reset form after successful creation
        this.menuForm.reset();
        this.selectedFile = null;
        this.imagePreview = null;
        
        // Navigate back or to another page after a delay
        setTimeout(() => {
          this.router.navigate(['/supplier']);
        }, 2000);
      },
      error: (err) => {
        this.loading = false;
        this.error = err?.error?.message || 'Failed to create menu item. Please try again.';
        console.error('Error creating menu item:', err);
      }
    });
  }
}