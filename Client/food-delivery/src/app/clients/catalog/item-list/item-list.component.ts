import { Component, OnInit } from '@angular/core';
import { ApiService } from '../../../api.service';
import { Item } from '../../../types/items';

@Component({
  selector: 'app-item-list',
  imports: [],
  templateUrl: './item-list.component.html',
  styleUrl: './item-list.component.css'
})
export class ItemListComponent implements OnInit{
  items: Item[] = [];
  constructor(private apiService: ApiService){}

  ngOnInit(): void {
      this.apiService.getItems().subscribe((items) =>{
        this.items = items;
      });
  }
}
