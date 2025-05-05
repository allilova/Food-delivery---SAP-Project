import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subscription } from 'rxjs';
import { 
  NotificationService, 
  Notification, 
  NotificationType 
} from '../../services/notification.service';

@Component({
  selector: 'app-notification',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="notifications-container">
      <div *ngFor="let notification of notifications" 
           class="notification"
           [ngClass]="notification.type">
        <div class="notification-content">
          <span class="notification-icon">
            <i *ngIf="notification.type === 'success'" class="fa fa-check-circle"></i>
            <i *ngIf="notification.type === 'error'" class="fa fa-exclamation-circle"></i>
            <i *ngIf="notification.type === 'warning'" class="fa fa-exclamation-triangle"></i>
            <i *ngIf="notification.type === 'info'" class="fa fa-info-circle"></i>
          </span>
          <span class="notification-message">{{ notification.message }}</span>
        </div>
        <span class="notification-close" (click)="close(notification.id)">×</span>
      </div>
    </div>
  `,
  styles: [`
    .notifications-container {
      position: fixed;
      top: 20px;
      right: 20px;
      z-index: 1050;
      max-width: 350px;
    }
    
    .notification {
      border-radius: 4px;
      margin-bottom: 10px;
      padding: 15px;
      display: flex;
      align-items: center;
      justify-content: space-between;
      box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
      animation: slide-in 0.3s ease-out;
    }
    
    .notification-content {
      display: flex;
      align-items: center;
    }
    
    .notification-icon {
      margin-right: 10px;
    }
    
    .notification-message {
      font-size: 14px;
    }
    
    .notification-close {
      cursor: pointer;
      font-size: 16px;
      margin-left: 10px;
    }
    
    .success {
      background-color: #d4edda;
      color: #155724;
      border-left: 4px solid #28a745;
    }
    
    .error {
      background-color: #f8d7da;
      color: #721c24;
      border-left: 4px solid #dc3545;
    }
    
    .warning {
      background-color: #fff3cd;
      color: #856404;
      border-left: 4px solid #ffc107;
    }
    
    .info {
      background-color: #d1ecf1;
      color: #0c5460;
      border-left: 4px solid #17a2b8;
    }
    
    @keyframes slide-in {
      0% {
        transform: translateX(100%);
        opacity: 0;
      }
      100% {
        transform: translateX(0);
        opacity: 1;
      }
    }
  `]
})
export class NotificationComponent implements OnInit, OnDestroy {
  notifications: Notification[] = [];
  private subscription: Subscription = new Subscription();

  constructor(private notificationService: NotificationService) { }

  ngOnInit(): void {
    this.subscription = this.notificationService.notifications$.subscribe(
      notifications => {
        this.notifications = notifications;
      }
    );
  }

  close(id: number): void {
    this.notificationService.close(id);
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }
}