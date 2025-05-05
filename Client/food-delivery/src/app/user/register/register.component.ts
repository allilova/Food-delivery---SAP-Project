import { Component, OnInit } from '@angular/core';
import { RouterLink, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, AbstractControl, ValidationErrors } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';
import { USER_ROLE } from '../../types/user-role.enum';
import { LoadingSpinnerComponent } from '../../components/loading-spinner.component';
import { NotificationService } from '../../services/notification.service';

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
  
  // Password strength indicators
  passwordStrength = {
    hasMinLength: false,
    hasUpperCase: false,
    hasLowerCase: false,
    hasNumber: false,
    hasSpecialChar: false
  };

  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private authService: AuthService,
    private notificationService: NotificationService
  ) {
    // Redirect to home if already logged in
    if (this.authService.isLoggedIn) {
      this.router.navigate(['/']);
    }
  }

  ngOnInit(): void {
    this.registerForm = this.formBuilder.group({
      firstName: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(50)]],
      lastName: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(50)]],
      gender: ['', Validators.required],
      address: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(200)]],
      phone: ['', [
        Validators.required, 
        Validators.pattern('^[0-9]{10,}$'),
        Validators.maxLength(15)
      ]],
      email: ['', [
        Validators.required, 
        Validators.email,
        Validators.pattern('^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$')
      ]],
      password: ['', [
        Validators.required, 
        Validators.minLength(8),
        Validators.maxLength(128)
      ]],
      confirmPassword: ['', Validators.required]
    }, {
      validators: this.passwordMatchValidator
    });
    
    // Subscribe to password changes to validate in real-time
    this.registerForm.get('password')?.valueChanges.subscribe(password => {
      if (password) {
        this.checkPasswordStrength(password);
      }
      this.registerForm.get('confirmPassword')?.updateValueAndValidity();
    });
  }
  
  // Check password strength
  checkPasswordStrength(password: string): void {
    this.passwordStrength = {
      hasMinLength: password.length >= 8,
      hasUpperCase: /[A-Z]/.test(password),
      hasLowerCase: /[a-z]/.test(password),
      hasNumber: /[0-9]/.test(password),
      hasSpecialChar: /[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(password)
    };
  }
  
  // Get password strength score (0-5)
  getPasswordStrengthScore(): number {
    return Object.values(this.passwordStrength).filter(Boolean).length;
  }
  
  // Get password strength description
  getPasswordStrengthText(): string {
    const score = this.getPasswordStrengthScore();
    if (score === 0) return 'Very Weak';
    if (score === 1) return 'Weak';
    if (score === 2) return 'Fair';
    if (score === 3) return 'Good';
    if (score === 4) return 'Strong';
    return 'Very Strong';
  }
  
  // Get password strength color
  getPasswordStrengthColor(): string {
    const score = this.getPasswordStrengthScore();
    if (score <= 1) return '#dc3545'; // red
    if (score === 2) return '#fd7e14'; // orange
    if (score === 3) return '#ffc107'; // yellow
    if (score === 4) return '#28a745'; // green
    return '#20c997'; // teal
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

    // If password strength isn't at least "Good" (3), show warning
    if (this.getPasswordStrengthScore() < 3) {
      this.notificationService.warning('Your password is weak. Consider using a stronger password for better security.');
    }

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
      phone: this.registerForm.value.phone,
      role: 'ROLE_CUSTOMER'
    };

    // For debug - log the form values
    console.log('Form Values:', this.registerForm.value);
    console.log('Prepared User Data:', userData);

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
          this.error = error?.message || 'Registration failed. Please try again.';
          this.loading = false;
        }
      });
  }
}