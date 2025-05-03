import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavigationBarComponent } from './core/nav-bar/nav-bar.component';
import { FooterComponent } from './core/footer/footer.component';
import { HttpClientModule } from '@angular/common/http';
import { NotificationComponent } from './components/notifications/notification.component';
import { DialogHostComponent } from './components/dialog-host/dialog-host.component';
import { OrderTrackingComponent } from './components/order-tracking/order-tracking.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    RouterOutlet, 
    NavigationBarComponent, 
    FooterComponent,
    HttpClientModule,
    NotificationComponent,
    DialogHostComponent,
    OrderTrackingComponent
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'food-delivery';
}