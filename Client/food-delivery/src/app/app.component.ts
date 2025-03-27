import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {NavigationBarComponent} from './core/nav-bar/nav-bar.component';
import{FooterComponent} from './core/footer/footer.component';
import { HomeComponent } from './clients/home/home.component';
import {LoginComponent} from './clients/login/login.component';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, NavigationBarComponent, FooterComponent, HomeComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'food-delivery';

  constructor(private http: HttpClient){}

  ngOnInit(){
    
  }
}
