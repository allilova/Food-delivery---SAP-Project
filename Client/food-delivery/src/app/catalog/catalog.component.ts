import { Component } from '@angular/core';
import {ItemListComponent} from './item-list/item-list.component'
import { RouterLink } from '@angular/router';
@Component({
  selector: 'app-catalog',
  imports: [ItemListComponent, RouterLink],
  templateUrl: './catalog.component.html',
  styleUrl: './catalog.component.css'
})
export class CatalogComponent {

}
