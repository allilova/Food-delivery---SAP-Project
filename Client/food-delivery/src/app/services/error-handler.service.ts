import { Injectable } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError, EMPTY, of } from 'rxjs';

export interface ErrorHandlerOptions {
  /** Context where the error occurred */
  context?: string;
  /** Whether to return an empty response instead of throwing an error */
  returnEmpty?: boolean;
  /** Whether to return a fallback value instead of throwing an error */
  fallbackValue?: any;
  /** Whether to log the error to the console (default: true) */
  logError?: boolean;
  /** Custom message for specific error */
  customMessage?: string;
}

/**
 * Enhanced error response with additional context
 */
export interface ErrorResponse {
  /** User-friendly error message */
  message: string;
  /** Original error object */
  original: any;
  /** Error status code if available */
  status?: number;
  /** Context where the error occurred */
  context?: string;
  /** Whether this is a network error */
  isNetworkError?: boolean;
  /** Whether this is a server error */
  isServerError?: boolean;
  /** Whether this is an authentication error */
  isAuthError?: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class ErrorHandlerService {
  
  constructor() { }

  /**
   * Handles HTTP errors and returns standardized error messages with options for graceful degradation
   * @param error The HTTP error from the API
   * @param options Additional options for error handling
   * @returns Observable with error, empty, or fallback value based on options
   */
  handleError(error: any, optionsOrContext: ErrorHandlerOptions | string = {}): Observable<any> {
    // Allow passing a string as context for backward compatibility
    const options: ErrorHandlerOptions = typeof optionsOrContext === 'string' 
      ? { context: optionsOrContext } 
      : optionsOrContext;
    
    const {
      context = 'operation',
      returnEmpty = false,
      fallbackValue = null,
      logError = true,
      customMessage = null
    } = options;
    
    // Create error response object
    const errorResponse: ErrorResponse = {
      message: '',
      original: error,
      context
    };
    
    try {
      // Handle different types of errors
      if (error instanceof ErrorEvent) {
        // Client-side error like network issues
        errorResponse.message = customMessage || `Client Error: ${error.message}`;
        errorResponse.isNetworkError = true;
        
        if (logError) {
          console.error(`Client-side error during ${context}:`, error.message);
        }
      } else if (error instanceof HttpErrorResponse) {
        // Server-side HTTP errors
        errorResponse.status = error.status;
        errorResponse.isServerError = error.status >= 500;
        errorResponse.isAuthError = error.status === 401 || error.status === 403;
        
        // Handle 500 errors specifically for supplier dashboard endpoints
        if (error.status === 500) {
          console.warn('Detected 500 error for endpoint', error.url);
          
          // Special handling for supplier/orders endpoint errors
          if (error.url && error.url.includes('/api/supplier/orders')) {
            console.info('This is a supplier order endpoint - returning empty data for better UX');
            // Return empty array for supplier endpoints when they fail
            return of(fallbackValue || []) as any;
          }
          
          // Special handling for debug endpoint errors
          if (error.url && error.url.includes('/api/auth/debug')) {
            console.info('Auth debug endpoint failed - continuing with fallback');
            return of(fallbackValue || {
              fallback: true,
              error: 'Auth debug endpoint not available',
              mockData: true
            }) as any;
          }
        }
        
        errorResponse.message = customMessage || this.getMessageForHttpError(error, context);
        
        if (logError) {
          console.error(`Server Error (${error.status}) for ${context}:`, error);
          this.logErrorDetails(error);
        }
      } else {
        // Generic errors
        errorResponse.message = customMessage || `An unexpected error occurred during ${context}. Please try again.`;
        
        if (logError) {
          console.error(`Unexpected error during ${context}:`, error);
        }
      }
      
      // Return appropriate response type based on options
      if (returnEmpty) {
        return EMPTY;
      } else if (fallbackValue !== null) {
        return of(fallbackValue);
      } else {
        return throwError(() => errorResponse);
      }
    } catch (e) {
      // Handle errors in error handling
      console.error('Error in error handler:', e);
      
      if (returnEmpty) {
        return EMPTY;
      } else if (fallbackValue !== null) {
        return of(fallbackValue);
      } else {
        return throwError(() => ({
          message: `An unexpected error occurred during ${context}.`,
          original: error
        }));
      }
    }
  }

  /**
   * Get an appropriate error message based on HTTP status code and error content
   */
  private getMessageForHttpError(error: HttpErrorResponse, context: string): string {
    // Try to extract message from response
    if (error.error && typeof error.error === 'object') {
      if (error.error.message) {
        return error.error.message;
      }
    }
    
    // Default messages based on status code
    switch (error.status) {
      case 0:
        return 'Could not connect to the server. Please check your internet connection.';
      case 400:
        return this.handleBadRequest(error);
      case 401:
        return 'You are not authorized. Please log in again.';
      case 403:
        return 'You do not have permission to perform this action.';
      case 404:
        return 'The requested resource was not found.';
      case 422:
        return this.handleValidationError(error);
      case 429:
        return 'Too many requests. Please try again later.';
      case 500:
        return 'An internal server error occurred. Please try again later.';
      case 502:
        return 'Bad gateway. The server is temporarily unavailable.';
      case 503:
        return 'Service unavailable. Please try again later.';
      case 504:
        return 'Gateway timeout. The server is taking too long to respond.';
      default:
        return `Error occurred during ${context}. Please try again.`;
    }
  }

  /**
   * Log additional error details for debugging
   */
  private logErrorDetails(error: HttpErrorResponse): void {
    try {
      console.group('Error Details');
      console.log('Status:', error.status);
      console.log('Status Text:', error.statusText);
      console.log('URL:', error.url);
      console.log('Error object:', error.error);
      console.log('Headers:', error.headers);
      console.groupEnd();
    } catch (e) {
      // Ignore errors in logging
    }
  }

  /**
   * Process 400 bad request errors, typically validation errors
   */
  private handleBadRequest(error: HttpErrorResponse): string {
    if (error.error && typeof error.error === 'object') {
      if (error.error.message) {
        return error.error.message;
      }
      
      // If there's a structured validation error
      if (error.error.errors) {
        return this.extractValidationErrors(error.error.errors);
      }
    }
    
    return 'Invalid request. Please check your data and try again.';
  }

  /**
   * Process 422 validation errors
   */
  private handleValidationError(error: HttpErrorResponse): string {
    if (error.error && error.error.errors) {
      return this.extractValidationErrors(error.error.errors);
    }
    
    return 'Validation failed. Please check your data and try again.';
  }

  /**
   * Extract meaningful validation error messages from an error object
   */
  private extractValidationErrors(errors: any): string {
    try {
      if (Array.isArray(errors)) {
        return errors.join('. ');
      }
      
      if (typeof errors === 'object') {
        const messages = [];
        
        for (const field in errors) {
          if (Object.prototype.hasOwnProperty.call(errors, field)) {
            const fieldErrors = errors[field];
            if (Array.isArray(fieldErrors)) {
              messages.push(`${field}: ${fieldErrors.join(', ')}`);
            } else if (typeof fieldErrors === 'string') {
              messages.push(`${field}: ${fieldErrors}`);
            }
          }
        }
        
        return messages.join('. ');
      }
      
      return 'Validation failed. Please check your data and try again.';
    } catch (e) {
      console.error('Error parsing validation errors:', e);
      return 'Validation failed. Please check your data and try again.';
    }
  }
}