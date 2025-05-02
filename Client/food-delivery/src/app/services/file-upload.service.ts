// src/app/services/file-upload.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from '../../environments/environment.development';

@Injectable({
  providedIn: 'root'
})
export class FileUploadService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) { }

  // Helper method to get auth token
  private getAuthToken(): string | null {
    return localStorage.getItem('jwt_token');
  }

  // Add headers for authenticated requests
  private getHeaders(): HttpHeaders {
    const token = this.getAuthToken();
    let headers = new HttpHeaders();
    
    if (token) {
      headers = headers.set('Authorization', `Bearer ${token}`);
    }
    
    return headers;
  }

  // For development/testing: simulate file upload by returning a dummy URL
  uploadImageMock(file: File): Observable<string> {
    console.log('Mock uploading file:', file.name);
    // Simulate network delay
    return new Observable(observer => {
      setTimeout(() => {
        // Return a base64 data URL directly
        const reader = new FileReader();
        reader.onload = () => {
          const base64String = reader.result as string;
          observer.next(base64String);
          observer.complete();
        };
        reader.readAsDataURL(file);
      }, 1000);
    });
  }

  // Real implementation that would call an API endpoint
  uploadImage(file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);

    return this.http.post<any>(`${this.apiUrl}/api/upload`, formData, {
      headers: this.getHeaders(),
      reportProgress: true,
      observe: 'events'
    }).pipe(
      catchError(error => {
        console.error('Error uploading file:', error);
        // Fall back to mock implementation for now
        return this.uploadImageMock(file);
      })
    );
  }

  // Convert a File object to base64 string
  fileToBase64(file: File): Observable<string> {
    return new Observable(observer => {
      const reader = new FileReader();
      reader.onload = () => {
        const base64String = reader.result as string;
        observer.next(base64String);
        observer.complete();
      };
      reader.onerror = (error) => {
        observer.error(error);
      };
      reader.readAsDataURL(file);
    });
  }
}