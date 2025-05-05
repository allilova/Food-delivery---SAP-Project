import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subscription } from 'rxjs';
import { ConfirmationDialogComponent } from '../confirmation-dialog/confirmation-dialog.component';
import { DialogService, DialogConfig } from '../../services/dialog.service';

@Component({
  selector: 'app-dialog-host',
  standalone: true,
  imports: [CommonModule, ConfirmationDialogComponent],
  template: `
    <app-confirmation-dialog
      *ngIf="isDialogOpen"
      [isOpen]="isDialogOpen"
      [title]="activeDialog?.title || 'Confirm Action'"
      [message]="activeDialog?.message || 'Are you sure you want to proceed?'"
      [confirmText]="activeDialog?.confirmText || 'Confirm'"
      [cancelText]="activeDialog?.cancelText || 'Cancel'"
      [type]="activeDialog?.type || 'danger'"
      (confirm)="onConfirm()"
      (cancel)="onCancel()"
    ></app-confirmation-dialog>
  `,
})
export class DialogHostComponent implements OnInit, OnDestroy {
  isDialogOpen = false;
  activeDialog?: DialogConfig;
  currentDialogId?: string;
  
  private subscription = new Subscription();
  
  constructor(private dialogService: DialogService) { }
  
  ngOnInit(): void {
    this.subscription = this.dialogService.getDialog().subscribe(dialog => {
      this.isDialogOpen = dialog.isOpen;
      if (dialog.isOpen && dialog.config) {
        this.activeDialog = dialog.config;
        this.currentDialogId = dialog.dialogId;
      }
    });
  }
  
  onConfirm(): void {
    if (this.currentDialogId) {
      const dialogRef = this.dialogService.confirm({ message: '' });
      dialogRef.close(true);
    }
  }
  
  onCancel(): void {
    if (this.currentDialogId) {
      const dialogRef = this.dialogService.confirm({ message: '' });
      dialogRef.close(false);
    }
  }
  
  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }
}