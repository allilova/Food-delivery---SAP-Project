// src/app/edit/edit-menu/edit-menu.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, Router, ActivatedRoute } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { RestaurantService } from '../../services/restaurant.service';
import { AuthService } from '../../services/auth.service';
import { NotificationService } from '../../services/notification.service';
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
    this.error = '';
    
    // First try to get item from session storage (should be available from menu component)
    try {
      const storedItem = sessionStorage.getItem('editMenuItem');
      if (storedItem) {
        const item = JSON.parse(storedItem);
        if (item.id.toString() === foodId) {
          console.log('Using stored menu item data from session storage');
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
          this.loadingData = false;
          return; // Exit early if we found the item
        }
      }
    } catch (e) {
      console.error('Error retrieving menu item from session storage:', e);
    }
    
    // Fallback to getting the item from the API/mock data
    console.log('Fetching menu item data from restaurant menu');
    
    // Try to get from all restaurants (we don't know which restaurant this item belongs to)
    const tryRestaurantIds = ['1', '2']; // Try common restaurant IDs
    
    // Function to load from a specific restaurant
    const loadFromRestaurant = (restaurantId: string) => {
      this.restaurantService.getRestaurantMenu(restaurantId).subscribe({
        next: (menuItems) => {
          const item = menuItems.find(item => item.id.toString() === foodId);
          if (item) {
            this.menuItem = item;
            
            // Populate form with menu item data
            this.menuForm.patchValue({
              foodName: item.name,
              price: item.price,
              description: item.description,
              portionSize: item.preparationTime
            });
            
            // Set image preview if available
            this.imagePreview = item.imageUrl;
            this.loadingData = false;
          } else {
            // If not found in this restaurant, try the next one
            const nextIndex = tryRestaurantIds.indexOf(restaurantId) + 1;
            if (nextIndex < tryRestaurantIds.length) {
              loadFromRestaurant(tryRestaurantIds[nextIndex]);
            } else {
              // Item not found in any restaurant
              this.fallbackToMockData(foodId);
            }
          }
        },
        error: (err) => {
          console.error(`Error loading menu for restaurant ${restaurantId}:`, err);
          // Try the next restaurant
          const nextIndex = tryRestaurantIds.indexOf(restaurantId) + 1;
          if (nextIndex < tryRestaurantIds.length) {
            loadFromRestaurant(tryRestaurantIds[nextIndex]);
          } else {
            // All restaurants failed, fall back to mock data
            this.fallbackToMockData(foodId);
          }
        }
      });
    };
    
    // Start with the first restaurant
    loadFromRestaurant(tryRestaurantIds[0]);
  }
  
  // Fallback to using mock data if we can't find the item elsewhere
  private fallbackToMockData(foodId: string): void {
    console.log('Falling back to mock data for menu item');
    import('../../mock-models/sample-models').then(models => {
      const mockItem = models.mockMenuItems.find(item => item.id.toString() === foodId);
      if (mockItem) {
        this.menuItem = mockItem;
        
        // Populate form with mock data
        this.menuForm.patchValue({
          foodName: mockItem.name,
          price: mockItem.price,
          description: mockItem.description,
          portionSize: mockItem.preparationTime
        });
        
        // Set image preview if available
        this.imagePreview = mockItem.imageUrl;
        this.loadingData = false;
      } else {
        // Create some default data if we still can't find it
        const defaultItem = {
          id: parseInt(foodId),
          name: 'Sample Menu Item',
          description: 'This is a sample menu item for editing purposes.',
          price: 9.99,
          imageUrl: 'https://example.com/default-food.jpg',
          isAvailable: true,
          categoryName: 'Other',
          preparationTime: 15,
          ingredients: ['Ingredient 1', 'Ingredient 2']
        };
        this.menuItem = defaultItem;
        
        // Populate form with default data
        this.menuForm.patchValue({
          foodName: defaultItem.name,
          price: defaultItem.price,
          description: defaultItem.description,
          portionSize: defaultItem.preparationTime
        });
        
        this.imagePreview = defaultItem.imageUrl;
        this.error = 'Using default data - item not found.';
        this.loadingData = false;
      }
    }).catch(err => {
      console.error('Error loading mock data:', err);
      this.error = 'Failed to load menu item data.';
      this.loadingData = false;
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
    
    // Create data object for update with all the updated fields
    const updatedMenuItem: Food = {
      id: parseInt(this.foodId),
      name: this.menuForm.value.foodName,
      description: this.menuForm.value.description,
      price: parseFloat(this.menuForm.value.price),
      preparationTime: parseInt(this.menuForm.value.portionSize),
      imageUrl: this.imagePreview || 'https://example.com/default-food.jpg',
      isAvailable: this.menuItem?.isAvailable || true,
      categoryName: this.menuItem?.categoryName || 'Other',
      ingredients: this.menuItem?.ingredients || [],
      restaurantId: this.menuItem?.restaurantId
    };

    console.log('Updating menu item:', updatedMenuItem);
    
    // For demo purposes, just simulate success and update local storage
    this.loading = false;
    this.successMessage = 'Menu item updated successfully!';
    this.notificationService.success('Menu item updated successfully!');
    
    // Store the updated item in session storage for persistence
    try {
      const storedItems = sessionStorage.getItem('updatedMenuItems') || '[]';
      const updatedItems = JSON.parse(storedItems);
      
      // Check if this item is already in the array
      const index = updatedItems.findIndex((item: Food) => item.id === updatedMenuItem.id);
      if (index >= 0) {
        // Update existing item
        updatedItems[index] = updatedMenuItem;
      } else {
        // Add new item to the array
        updatedItems.push(updatedMenuItem);
      }
      
      sessionStorage.setItem('updatedMenuItems', JSON.stringify(updatedItems));
      console.log('Updated menu item saved to session storage');
      
      // Also update the editMenuItem in session storage so it's available if we edit again
      sessionStorage.setItem('editMenuItem', JSON.stringify(updatedMenuItem));
      
      // Navigate back to the restaurant menu after a delay
      setTimeout(() => {
        // If we know the restaurant ID, navigate directly to it
        if (updatedMenuItem.restaurantId) {
          this.router.navigate(['/catalog', updatedMenuItem.restaurantId]);
        } else {
          // Otherwise try to navigate back to supplier dashboard
          this.router.navigate(['/supplier']);
        }
      }, 2000);
    } catch (e) {
      console.error('Error storing updated menu item in session storage:', e);
      this.error = 'Error saving changes. Please try again.';
    }
  }
}