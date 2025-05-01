// register.component.ts
import { Component, OnInit } from '@angular/core';
import { RouterLink, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, AbstractControl, ValidationErrors } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';
import { USER_ROLE } from '../../types/user-role.enum';
import { LoadingSpinnerComponent } from '../../components/loading-spinner.component';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [RouterLink, ReactiveFormsModule, CommonModule, LoadingSpinnerComponent],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent implements OnInit {
  registerForm!: FormGroup;
  submitted = false;
  loading = false;
  error = '';
  successMessage = '';

  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private authService: AuthService
  ) {
    // Redirect to home if already logged in
    if (this.authService.isLoggedIn) {
      this.router.navigate(['/']);
    }
  }

  ngOnInit(): void {
    this.registerForm = this.formBuilder.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      gender: ['', Validators.required],
      address: ['', Validators.required],
      phone_number: ['', [Validators.required, Validators.pattern('^[0-9]{10,}$')]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [
        Validators.required, 
        Validators.minLength(8),
        Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&#^()_+\-=\[\]{};':"\\|,.<>\/?])[A-Za-z\d@$!%*?&#^()_+\-=\[\]{};':"\\|,.<>\/?]{8,}$/)
      ]],
      confirmPassword: ['', Validators.required]
    }, {
      validators: this.passwordMatchValidator
    });
  }

  // Custom validator for password match
  passwordMatchValidator(control: AbstractControl): ValidationErrors | null {
    const password = control.get('password')?.value;
    const confirmPassword = control.get('confirmPassword')?.value;

    if (password !== confirmPassword) {
      control.get('confirmPassword')?.setErrors({ passwordMismatch: true });
      return { passwordMismatch: true };
    } else {
      // Make sure to remove the passwordMismatch error if passwords match
      const confirmPasswordControl = control.get('confirmPassword');
      if (confirmPasswordControl?.errors) {
        // Create new errors object without passwordMismatch
        const errors = { ...confirmPasswordControl.errors };
        delete errors['passwordMismatch'];
        
        // Set errors to null if no errors left, otherwise set the remaining errors
        confirmPasswordControl.setErrors(Object.keys(errors).length ? errors : null);
      }
      return null;
    }
  }

  // Convenience getter for easy access to form fields
  get f() { return this.registerForm.controls; }

  onSubmit() {
    this.submitted = true;
    this.error = '';
    this.successMessage = '';

    // stop here if form is invalid
    if (this.registerForm.invalid) {
      return;
    }

    this.loading = true;
    
    // Prepare the data to match the backend expectations
    const userData = {
      name: `${this.registerForm.value.firstName} ${this.registerForm.value.lastName}`,
      email: this.registerForm.value.email,
      password: this.registerForm.value.password,
      phoneNumber: this.registerForm.value.phone_number, // Changed from phone_number to phoneNumber
      address: this.registerForm.value.address,
      role: USER_ROLE.ROLE_CUSTOMER
    };

    // Register the user
    this.authService.register(userData)
      .subscribe({
        next: (response) => {
          this.successMessage = response.message || 'Registration successful! You can now log in.';
          this.loading = false;
          
          // Redirect to login page after a short delay
          setTimeout(() => {
            this.router.navigate(['/login']);
          }, 2000);
        },
        error: error => {
          this.error = error?.error?.message || 'Registration failed. Please try again.';
          this.loading = false;
        }
      });
  }
}