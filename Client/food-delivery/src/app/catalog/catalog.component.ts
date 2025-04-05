import { Component } from '@angular/core';
import {ItemListComponent} from './item-list/item-list.component'
@Component({
  selector: 'app-catalog',
  imports: [ItemListComponent],
  templateUrl: './catalog.component.html',
  styleUrl: './catalog.component.css'
})
export class CatalogComponent {

}
