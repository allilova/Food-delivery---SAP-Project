import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

export enum NotificationType {
  SUCCESS = 'success',
  ERROR = 'error',
  WARNING = 'warning',
  INFO = 'info'
}

export interface Notification {
  id: number;
  type: NotificationType;
  message: string;
  autoClose?: boolean;
  timeout?: number;
}

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private notificationsSubject = new BehaviorSubject<Notification[]>([]);
  private lastId = 0;

  constructor() { }

  /**
   * Get the observable for notifications
   */
  get notifications$(): Observable<Notification[]> {
    return this.notificationsSubject.asObservable();
  }

  /**
   * Show a success notification
   */
  success(message: string, autoClose: boolean = true, timeout: number = 5000): number {
    return this.addNotification({
      id: ++this.lastId,
      type: NotificationType.SUCCESS,
      message,
      autoClose,
      timeout
    });
  }

  /**
   * Show an error notification
   */
  error(message: string, autoClose: boolean = true, timeout: number = 8000): number {
    return this.addNotification({
      id: ++this.lastId,
      type: NotificationType.ERROR,
      message,
      autoClose,
      timeout
    });
  }

  /**
   * Show a warning notification
   */
  warning(message: string, autoClose: boolean = true, timeout: number = 6000): number {
    return this.addNotification({
      id: ++this.lastId,
      type: NotificationType.WARNING,
      message,
      autoClose,
      timeout
    });
  }

  /**
   * Show an info notification
   */
  info(message: string, autoClose: boolean = true, timeout: number = 4000): number {
    return this.addNotification({
      id: ++this.lastId,
      type: NotificationType.INFO,
      message,
      autoClose,
      timeout
    });
  }

  /**
   * Close a notification by ID
   */
  close(id: number): void {
    const notifications = this.notificationsSubject.value;
    const updatedNotifications = notifications.filter(notification => notification.id !== id);
    this.notificationsSubject.next(updatedNotifications);
  }

  /**
   * Clear all notifications
   */
  clearAll(): void {
    this.notificationsSubject.next([]);
  }

  /**
   * Add a notification to the list
   */
  private addNotification(notification: Notification): number {
    const notifications = [...this.notificationsSubject.value, notification];
    this.notificationsSubject.next(notifications);

    // Auto-close if needed
    if (notification.autoClose) {
      setTimeout(() => {
        this.close(notification.id);
      }, notification.timeout || 5000);
    }

    return notification.id;
  }
}