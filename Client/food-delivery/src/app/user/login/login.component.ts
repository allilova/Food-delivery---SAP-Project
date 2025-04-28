// login.component.ts
import { Component, OnInit } from "@angular/core";
import { RouterLink, Router } from "@angular/router";
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from "@angular/forms";
import { CommonModule } from "@angular/common";
import { AuthService } from "../../services/auth.service";

@Component({
    selector: 'login-root',
    standalone: true,
    imports: [RouterLink, ReactiveFormsModule, CommonModule],
    templateUrl: './login.component.html',
    styleUrl: './login.component.css'
})

export class LoginComponent implements OnInit {
    loginForm!: FormGroup;
    submitted = false;
    loading = false;
    error = '';

    constructor(
        private formBuilder: FormBuilder,
        private router: Router,
        private authService: AuthService
    ) {}

    ngOnInit(): void {
        this.loginForm = this.formBuilder.group({
            email: ['', [Validators.required, Validators.email]],
            password: ['', Validators.required]
        });
    }

    // Convenience getter for easy access to form fields
    get f() { return this.loginForm.controls; }

    onSubmit() {
        this.submitted = true;

        // stop here if form is invalid
        if (this.loginForm.invalid) {
            return;
        }

        this.loading = true;
        
        // Replace with actual authentication logic
        this.authService.login(this.loginForm.value)
            .subscribe({
                next: () => {
                    // Navigate to home page after successful login
                    this.router.navigate(['/home']);
                },
                error: error => {
                    this.error = error?.error?.message || 'Login failed. Please check your credentials.';
                    this.loading = false;
                }
            });
    }
}