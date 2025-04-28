import { Component, OnInit } from "@angular/core";
import { RouterLink } from "@angular/router";
import { HomeService, HomeData } from "../home.service";
import { CommonModule } from "@angular/common";

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
    
    constructor(private homeService: HomeService) {}
    
    ngOnInit(): void {
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