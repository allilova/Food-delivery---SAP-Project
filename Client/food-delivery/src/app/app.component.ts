import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {NavigationBarComponent} from './nav-bar/nav-bar.component';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, NavigationBarComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'food-delivery';
}
