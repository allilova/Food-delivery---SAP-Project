import { Component, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {NavigationBarComponent} from './core/nav-bar/nav-bar.component';
import{FooterComponent} from './core/footer/footer.component';
import { HomeComponent } from './home/home.component';
import {LoginComponent} from './user/login/login.component';



@Component({
  selector: 'app-root',
  imports: [RouterOutlet, NavigationBarComponent, FooterComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'food-delivery';
}
