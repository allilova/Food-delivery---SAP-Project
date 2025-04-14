import { Component, OnInit } from '@angular/core';
import { ApiService } from '../../api.service';
import { Restaurant } from '../../types/restaurants';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-item-list',
  imports: [RouterLink],
  templateUrl: './item-list.component.html',
  styleUrl: './item-list.component.css'
})
export class ItemListComponent implements OnInit{
  restaurants: Restaurant[] = [];
  constructor(private apiService: ApiService){}

  ngOnInit(): void {
      this.apiService. getRestaurants().subscribe((restaurants) =>{
        this.restaurants = restaurants;

        console.log({ restaurants });
      });
  }
}
