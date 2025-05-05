
import { Component, OnInit } from "@angular/core";
import { RouterLink } from "@angular/router";
import { HomeService, HomeData } from "../home.service";
import { CommonModule } from "@angular/common";
import { AuthService } from "../services/auth.service";

@Component({
    selector: 'home-root',
    imports: [RouterLink, CommonModule],
    standalone: true,
    templateUrl: './home.component.html',
    styleUrl: './home.component.css'
})

export class HomeComponent implements OnInit {
    homeData: HomeData | null = null;
    loading: boolean = true;
    error: string | null = null;
    isLoggedIn: boolean = false;
    
    constructor(
        private homeService: HomeService,
        private authService: AuthService
    ) {}
    
    ngOnInit(): void {
        // Check if user is logged in
        this.isLoggedIn = this.authService.isLoggedIn;
        
        // Subscribe to homeData changes
        this.loadHomeData();
    }
    
    loadHomeData(): void {
        this.loading = true;
        this.homeService.getHomeData().subscribe({
            next: (data) => {
                this.homeData = data;
                this.loading = false;
            },
            error: (err) => {
                console.error('Error loading home data:', err);
                this.error = 'Failed to load content. Please try again later.';
                this.loading = false;
            }
        });
    }
}