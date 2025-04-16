import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Restaurant } from '../types/restaurants';
import { ApiService } from '../api.service';

@Component({
  selector: 'app-search',
  imports: [RouterLink],
  templateUrl: './search.component.html',
  styleUrl: './search.component.css'
})
export class SearchComponent implements OnInit{
  restaurants: Restaurant[] = [];
  constructor(private apiService: ApiService){}

  ngOnInit(): void {
      this.apiService. getRestaurants().subscribe((restaurants) =>{
        this.restaurants = restaurants;

        console.log({ restaurants });
      });
  }
}
