// src/app/admin/create-menu-item/create-menu-item.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, Router, ActivatedRoute } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { RestaurantService } from '../../services/restaurant.service';
import { AuthService } from '../../services/auth.service';
import { USER_ROLE } from '../../types/user-role.enum';
import { LoadingSpinnerComponent } from '../../components/loading-spinner.component';
import { FileUploadService } from '../../services/file-upload.service';

@Component({
  selector: 'app-create-menu-item',
  standalone: true,
  imports: [RouterLink, CommonModule, ReactiveFormsModule, LoadingSpinnerComponent],
  templateUrl: './create-menu-item.component.html',
  styleUrl: './create-menu-item.component.css'
})
export class CreateMenuItemComponent implements OnInit {
  menuForm!: FormGroup;
  loading = false;
  error = '';
  successMessage = '';
  selectedFile: File | null = null;
  imagePreview: string | null = null;
  restaurantId: string = '';
  characterCount: number = 0;
  maxCharacters: number = 500;

  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private route: ActivatedRoute,
    private restaurantService: RestaurantService,
    private authService: AuthService,
    private fileUploadService: FileUploadService
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

    // Get restaurant ID from route
    this.route.params.subscribe(params => {
      this.restaurantId = params['restaurantId'];
    });

    // Initialize form
    this.menuForm = this.formBuilder.group({
      name: ['', Validators.required],
      price: ['', [Validators.required, Validators.min(0.01)]],
      description: ['', [Validators.required, Validators.maxLength(this.maxCharacters)]],
      category: ['', Validators.required],
      preparationTime: ['', [Validators.required, Validators.min(1)]],
      ingredients: ['']
    });

    // Track character count for description
    this.menuForm.get('description')?.valueChanges.subscribe(value => {
      this.characterCount = value ? value.length : 0;
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
      
      console.log('File selected:', this.selectedFile.name);
    }
  }

  // Submit form
  onSubmit(): void {
    if (this.menuForm.invalid) {
      // Mark all fields as touched to trigger validation messages
      Object.keys(this.menuForm.controls).forEach(key => {
        this.menuForm.get(key)?.markAsTouched();
      });
      return;
    }

    this.loading = true;
    this.error = '';
    
    // Parse ingredients string into array
    const ingredientsValue = this.menuForm.value.ingredients;
    const ingredients = ingredientsValue ? 
      ingredientsValue.split(',').map((item: string) => item.trim()).filter((item: string) => item) : 
      [];

    // Handle image upload or use existing preview
    const processImageAndSubmit = (imageUrl: string | null) => {
      // Create menu item data matching the Food interface
      const menuItemData = {
        name: this.menuForm.value.name,
        description: this.menuForm.value.description,
        price: parseFloat(this.menuForm.value.price),
        preparationTime: parseInt(this.menuForm.value.preparationTime),
        categoryName: this.menuForm.value.category,
        restaurantId: parseInt(this.restaurantId) || 1,
        imageUrl: imageUrl || '/public/logo2.png',
        isAvailable: true,
        ingredients: ingredients
      };

      console.log('Submitting menu item:', menuItemData);

      // Call API to create menu item
      this.restaurantService.createMenuItem(menuItemData).subscribe({
        next: (response) => {
          this.loading = false;
          this.successMessage = 'Menu item created successfully!';
          console.log('Menu item created:', response);
          
          // Navigate back to menu page after a delay
          // Use longer delay and ensure notification is visible to user before navigation
          this.error = '';
          
          setTimeout(() => {
            // Navigate to correct catalog/menu URL using router
            console.log('Navigating to restaurant menu:', this.restaurantId);
            this.router.navigate(['/catalog', this.restaurantId]);
          }, 3000);
        },
        error: (err) => {
          this.loading = false;
          this.error = err?.error?.message || 'Failed to create menu item. Please try again.';
          console.error('Error creating menu item:', err);
          
          // Log the detailed error for debugging
          console.error('Detailed error:', JSON.stringify(err));
        }
      });
    };

    // If there's a selected file, upload it first
    if (this.selectedFile) {
      this.fileUploadService.fileToBase64(this.selectedFile).subscribe({
        next: (base64Image) => {
          processImageAndSubmit(base64Image);
        },
        error: (err) => {
          console.error('Error converting image to base64:', err);
          // Continue with null image if conversion fails
          processImageAndSubmit(null);
        }
      });
    } else {
      // No image selected, continue with null
      processImageAndSubmit(null);
    }
  }
}