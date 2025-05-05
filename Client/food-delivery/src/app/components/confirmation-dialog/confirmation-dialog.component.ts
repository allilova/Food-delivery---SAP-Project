import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-confirmation-dialog',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="dialog-overlay" *ngIf="isOpen" (click)="onOverlayClick($event)">
      <div class="dialog-content" [ngClass]="type">
        <div class="dialog-header">
          <h3>{{ title }}</h3>
          <button class="close-button" (click)="onCancel()">&times;</button>
        </div>
        <div class="dialog-body">
          <p>{{ message }}</p>
        </div>
        <div class="dialog-footer">
          <button class="cancel-button" (click)="onCancel()">{{ cancelText }}</button>
          <button class="confirm-button" (click)="onConfirm()">{{ confirmText }}</button>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .dialog-overlay {
      position: fixed;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background-color: rgba(0, 0, 0, 0.5);
      display: flex;
      justify-content: center;
      align-items: center;
      z-index: 1000;
    }
    
    .dialog-content {
      background-color: #fff;
      border-radius: 8px;
      width: 90%;
      max-width: 500px;
      box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
      animation: dialog-fade-in 0.3s ease-out;
    }
    
    .dialog-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 16px 20px;
      border-bottom: 1px solid #eee;
    }
    
    .dialog-header h3 {
      margin: 0;
      font-size: 18px;
      font-weight: 600;
    }
    
    .close-button {
      background: none;
      border: none;
      font-size: 24px;
      cursor: pointer;
      color: #666;
    }
    
    .dialog-body {
      padding: 20px;
      font-size: 16px;
      color: #333;
      line-height: 1.5;
    }
    
    .dialog-footer {
      display: flex;
      justify-content: flex-end;
      padding: 16px 20px;
      border-top: 1px solid #eee;
      gap: 12px;
    }
    
    button {
      padding: 8px 16px;
      border-radius: 4px;
      font-size: 14px;
      font-weight: 500;
      cursor: pointer;
      transition: background-color 0.2s ease;
    }
    
    .cancel-button {
      background-color: #f5f5f5;
      border: 1px solid #ddd;
      color: #333;
    }
    
    .confirm-button {
      border: none;
      color: white;
    }
    
    /* Dialog types */
    .danger .confirm-button {
      background-color: #dc3545;
    }
    
    .warning .confirm-button {
      background-color: #ffc107;
      color: #212529;
    }
    
    .info .confirm-button {
      background-color: #17a2b8;
    }
    
    .success .confirm-button {
      background-color: #28a745;
    }
    
    /* Animation */
    @keyframes dialog-fade-in {
      from { opacity: 0; transform: translateY(-20px); }
      to { opacity: 1; transform: translateY(0); }
    }
  `]
})
export class ConfirmationDialogComponent {
  @Input() isOpen = false;
  @Input() title = 'Confirm Action';
  @Input() message = 'Are you sure you want to proceed?';
  @Input() confirmText = 'Confirm';
  @Input() cancelText = 'Cancel';
  @Input() type: 'danger' | 'warning' | 'info' | 'success' = 'danger';
  
  @Output() confirm = new EventEmitter<void>();
  @Output() cancel = new EventEmitter<void>();
  
  onConfirm(): void {
    this.confirm.emit();
  }
  
  onCancel(): void {
    this.cancel.emit();
  }
  
  onOverlayClick(event: MouseEvent): void {
    if ((event.target as HTMLElement).className === 'dialog-overlay') {
      this.onCancel();
    }
  }
}