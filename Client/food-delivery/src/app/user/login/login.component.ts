import { Component, OnInit } from "@angular/core";
import { RouterLink, Router, ActivatedRoute } from "@angular/router";
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from "@angular/forms";
import { CommonModule } from "@angular/common";
import { AuthService } from "../../services/auth.service";
import { LoadingSpinnerComponent } from "../../components/loading-spinner.component";

@Component({
    selector: 'app-login',
    standalone: true,
    imports: [RouterLink, ReactiveFormsModule, CommonModule, LoadingSpinnerComponent],
    templateUrl: './login.component.html',
    styleUrl: './login.component.css'
})
export class LoginComponent implements OnInit {
    loginForm!: FormGroup;
    submitted = false;
    loading = false;
    error = '';
    returnUrl: string = '/';

    constructor(
        private formBuilder: FormBuilder,
        private router: Router,
        private route: ActivatedRoute,
        private authService: AuthService
    ) {
        // Redirect to catalog if already logged in
        if (this.authService.isLoggedIn) {
            this.router.navigate(['/catalog']);
        }
    }

    ngOnInit(): void {
        this.loginForm = this.formBuilder.group({
            email: ['', [Validators.required, Validators.email]],
            password: ['', Validators.required]
        });

        // Get return url from route parameters or default to '/'
        this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';
    }

    // Convenience getter for easy access to form fields
    get f() { return this.loginForm.controls; }

    onSubmit() {
        this.submitted = true;
        this.error = '';

        // stop here if form is invalid
        if (this.loginForm.invalid) {
            return;
        }

        this.loading = true;
        
        // Log the attempt for debugging
        console.log('Logging in with email:', this.f['email'].value);
        
        this.authService.login({
            email: this.f['email'].value,
            password: this.f['password'].value
        })
        .subscribe({
            next: (response) => {
                console.log('Login successful, role:', response.role);
                // Navigate to return url or default route
                this.router.navigate([this.returnUrl]);
            },
            error: error => {
                console.error('Login error:', error);
                this.error = error?.error?.message || 'Login failed. Please check your credentials and ensure the backend is running.';
                this.loading = false;
            }
        });
    }
}