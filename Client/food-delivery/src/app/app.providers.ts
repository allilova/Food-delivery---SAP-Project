// src/app/app.providers.ts - for shared services in standalone components approach
import { Provider } from '@angular/core';
import { AuthService } from './services/auth.service';
import { ApiService } from './api.service';

export const appProviders: Provider[] = [
  AuthService,
  ApiService
];