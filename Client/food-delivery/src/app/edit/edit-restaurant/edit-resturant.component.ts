// src/app/edit/edit-restaurant/edit-restaurant.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, Router, ActivatedRoute } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { RestaurantService } from '../../services/restaurant.service';
import { AuthService } from '../../services/auth.service';
import { NotificationService } from '../../services/notification.service';
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
    private route: ActivatedRoute,
    private restaurantService: RestaurantService,
    private authService: AuthService,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    // Check if user is logged in and has appropriate role
    if (!this.authService.isLoggedIn || 
        (this.authService.userRole !== USER_ROLE.ROLE_RESTAURANT && 
         this.authService.userRole !== USER_ROLE.ROLE_ADMIN &&
         this.authService.userRole !== USER_ROLE.ROLE_DRIVER)) {
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

    // Get restaurant ID from route parameters
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) {
        this.restaurantId = id;
        console.log('Editing restaurant with ID:', this.restaurantId);
        this.loadRestaurantData(id);
      } else {
        console.error('No restaurant ID provided in route');
        this.notificationService.error('No restaurant ID provided');
        setTimeout(() => this.router.navigate(['/supplier']), 2000);
      }
    });
  }

  loadRestaurantData(restaurantId: string): void {
    this.loadingData = true;
    this.error = '';
    console.log('Loading data for restaurant ID:', restaurantId);
    
    // Get the specific restaurant by ID
    this.restaurantService.getRestaurantById(restaurantId).subscribe({
      next: (restaurant) => {
        console.log('Restaurant data loaded:', restaurant);
        this.restaurant = restaurant;
        
        // Populate form with restaurant data
        this.restaurantForm.patchValue({
          name: restaurant.name,
          foodType: restaurant.foodType,
          address: restaurant.address,
          // Use temporary phone number for demo
          phone_number: '1234567890', 
          timeDelivery: restaurant.timeDelivery || '30-45 min'
        });
        
        // Set image preview if available
        this.imagePreview = restaurant.imgUrl;
        this.loadingData = false;
      },
      error: (err) => {
        console.error('Error loading restaurant data:', err);
        this.error = 'Failed to load restaurant data. Using fallback data.';
        
        // Use fallback data from mock models
        import('../../mock-models/sample-models').then(models => {
          const mockRestaurant = models.mockRestaurants.find(r => r.id === restaurantId) || 
                                models.mockRestaurants[0];
                                
          if (mockRestaurant) {
            this.restaurant = mockRestaurant;
            
            // Populate form with mock restaurant data
            this.restaurantForm.patchValue({
              name: mockRestaurant.name,
              foodType: mockRestaurant.foodType,
              address: mockRestaurant.address,
              phone_number: '1234567890',
              timeDelivery: mockRestaurant.timeDelivery
            });
            
            // Set image preview if available
            this.imagePreview = mockRestaurant.imgUrl;
            this.error = ''; // Clear error if we found a fallback
          }
        }).catch(importErr => {
          console.error('Error loading mock data:', importErr);
        }).finally(() => {
          this.loadingData = false;
        });
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

    // Call API to update restaurant, but use simulated success for demo
    console.log('Updating restaurant with data:', restaurantData);
    this.loading = false;
    this.successMessage = 'Restaurant updated successfully!';
    this.notificationService.success('Restaurant updated successfully!');
    
    // Update the restaurant in session storage so the changes persist
    try {
      const storedRestaurants = sessionStorage.getItem('updatedRestaurants') || '[]';
      const restaurants = JSON.parse(storedRestaurants);
      const updatedData = {
        id: this.restaurantId,
        name: this.restaurantForm.value.name,
        foodType: this.restaurantForm.value.foodType,
        address: this.restaurantForm.value.address,
        imgUrl: this.imagePreview,
        timeDelivery: this.restaurantForm.value.timeDelivery,
        rating: this.restaurant?.rating || 4.5
      };
      
      // Add or update the restaurant
      const index = restaurants.findIndex((r: any) => r.id === this.restaurantId);
      if (index >= 0) {
        restaurants[index] = {...restaurants[index], ...updatedData};
      } else {
        restaurants.push(updatedData);
      }
      
      sessionStorage.setItem('updatedRestaurants', JSON.stringify(restaurants));
      console.log('Restaurant data saved to session storage');
      
      // Navigate back to the menu page for this restaurant after a delay
      setTimeout(() => {
        this.router.navigate(['/catalog', this.restaurantId]);
      }, 2000);
    } catch (e) {
      console.error('Error storing restaurant data:', e);
    }
  }
}