import { Injectable } from '@angular/core';
import { Subject, Observable } from 'rxjs';

export interface DialogConfig {
  title?: string;
  message: string;
  confirmText?: string;
  cancelText?: string;
  type?: 'danger' | 'warning' | 'info' | 'success';
}

export interface DialogRef {
  afterClosed(): Observable<boolean>;
  close(result: boolean): void;
}

@Injectable({
  providedIn: 'root'
})
export class DialogService {
  private dialogSubject = new Subject<{
    isOpen: boolean;
    config?: DialogConfig;
    dialogId?: string;
  }>();
  
  private dialogResponses = new Map<string, Subject<boolean>>();
  
  constructor() { }
  
  // Open a confirmation dialog
  confirm(config: DialogConfig): DialogRef {
    const dialogId = this.generateId();
    const responseSubject = new Subject<boolean>();
    
    this.dialogResponses.set(dialogId, responseSubject);
    
    this.dialogSubject.next({
      isOpen: true,
      config,
      dialogId
    });
    
    return {
      afterClosed: () => responseSubject.asObservable(),
      close: (result: boolean) => {
        this.dialogSubject.next({ isOpen: false, dialogId });
        responseSubject.next(result);
        responseSubject.complete();
        this.dialogResponses.delete(dialogId);
      }
    };
  }
  
  // Get the dialog observable
  getDialog(): Observable<{
    isOpen: boolean;
    config?: DialogConfig;
    dialogId?: string;
  }> {
    return this.dialogSubject.asObservable();
  }
  
  // Generate a unique dialog ID
  private generateId(): string {
    return Math.random().toString(36).substring(2, 9);
  }
}