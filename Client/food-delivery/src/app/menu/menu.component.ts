import { Component } from '@angular/core';
import { MenuItemComponent } from './menu-item/menu-item.component';

@Component({
  selector: 'app-menu',
  imports: [MenuItemComponent],
  templateUrl: './menu.component.html',
  styleUrl: './menu.component.css'
})
export class MenuComponent {

}
