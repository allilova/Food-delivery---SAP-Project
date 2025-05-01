// src/app/edit/edit-restaurant/edit-restaurant.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { RestaurantService } from '../../services/restaurant.service';
import { AuthService } from '../../services/auth.service';
import { USER_ROLE } from '../../types/user-role.enum';
import { Restaurant } from '../../types/restaurants';
import { LoadingSpinnerComponent } from '../../components/loading-spinner.component';

@Component({
  selector: 'app-edit-restaurant',
  standalone: true,
  imports: [RouterLink, CommonModule, ReactiveFormsModule, LoadingSpinnerComponent],
  templateUrl: './edit-resturant.component.html',
  styleUrl: './edit-resturant.component.css'
})
export class EditRestaurantComponent implements OnInit {
  restaurantForm!: FormGroup;
  restaurant: Restaurant | null = null;
  loading = false;
  loadingData = false;
  error = '';
  successMessage = '';
  selectedFile: File | null = null;
  imagePreview: string | null = null;
  restaurantId: string = '';

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
    this.restaurantForm = this.formBuilder.group({
      name: ['', Validators.required],
      foodType: ['', Validators.required],
      address: ['', Validators.required],
      phone_number: ['', [Validators.required, Validators.pattern('^[0-9]{10,}$')]],
      timeDelivery: ['', Validators.required]
    });

    // Load restaurant data
    this.loadRestaurantData();
  }

  loadRestaurantData(): void {
    // In a real app, you would get the restaurantId from a route parameter or user context
    // For now, we'll just get the first restaurant for the current user
    this.loadingData = true;
    
    // This is a simplification - in a real app, you'd likely get the specific restaurant
    // either from route parameters or based on the logged-in restaurant owner
    this.restaurantService.getAllRestaurants().subscribe({
      next: (restaurants) => {
        if (restaurants.length > 0) {
          this.restaurant = restaurants[0];
          this.restaurantId = this.restaurant.id;
          
          // Populate form with restaurant data
          this.restaurantForm.patchValue({
            name: this.restaurant.name,
            foodType: this.restaurant.foodType,
            address: this.restaurant.address,
            // You might not have all these fields in your model
            // phone_number: this.restaurant.phone_number,
            timeDelivery: this.restaurant.timeDelivery
          });
          
          // Set image preview if available
          this.imagePreview = this.restaurant.imgUrl;
        }
        this.loadingData = false;
      },
      error: (err) => {
        console.error('Error loading restaurant data:', err);
        this.error = 'Failed to load restaurant data.';
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
    if (this.restaurantForm.invalid || !this.restaurantId) {
      return;
    }

    this.loading = true;
    
    // Create data object for update
    const restaurantData = {
      restaurantName: this.restaurantForm.value.name,
      type: this.restaurantForm.value.foodType,
      // Format address for backend
      restaurantAddress: {
        street: this.restaurantForm.value.address,
        city: 'Default City' // You might want to add a city field to your form
      },
      // Format contact info for backend
      contactInfo: {
        phone: this.restaurantForm.value.phone_number,
        email: '', // Add email field if needed
        socialMedia: '' // Add social media fields if needed
      },
      openingHours: '09:00', // Add fields if needed
      closingHours: '22:00', // Add fields if needed
      // If you have an array of images
      images: this.selectedFile ? [this.imagePreview!] : undefined
    };

    // Call API to update restaurant
    this.restaurantService.updateRestaurant(this.restaurantId, restaurantData).subscribe({
      next: (response) => {
        this.loading = false;
        this.successMessage = 'Restaurant updated successfully!';
        
        // Navigate back or to another page after a delay
        setTimeout(() => {
          this.router.navigate(['/supplier']);
        }, 2000);
      },
      error: (err) => {
        this.loading = false;
        this.error = err?.error?.message || 'Failed to update restaurant. Please try again.';
        console.error('Error updating restaurant:', err);
      }
    });
  }
}