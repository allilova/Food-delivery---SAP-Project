import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {NavigationBarComponent} from './clients/core/nav-bar/nav-bar.component';
import{FooterComponent} from './clients/core/footer/footer.component';
import { HomeComponent } from './clients/home/home.component';
import {LoginComponent} from './clients/login/login.component';
import {CatalogComponent} from './clients/catalog/catalog.component'



@Component({
  selector: 'app-root',
  imports: [RouterOutlet, NavigationBarComponent, FooterComponent, CatalogComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'food-delivery';

}
