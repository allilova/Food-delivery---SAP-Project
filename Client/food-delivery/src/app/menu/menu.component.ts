import { Component, OnInit } from '@angular/core';
import { MenuItemComponent } from './menu-item/menu-item.component';
import { ActivatedRoute } from '@angular/router';
import { ApiService } from '../api.service';
import { Restaurant } from '../types/restaurants';

@Component({
  selector: 'app-menu',
  imports: [MenuItemComponent],
  templateUrl: './menu.component.html',
  styleUrl: './menu.component.css'
})
export class MenuComponent implements OnInit {
  restaurant = {} as Restaurant;
  constructor(private route: ActivatedRoute, private apiService: ApiService){}

  ngOnInit(): void {
    const id = this.route.snapshot.params['restaurantId'];
    this.apiService.getMenu(id).subscribe((restaurant) =>{
     this.restaurant = restaurant;
    })
  }
}
