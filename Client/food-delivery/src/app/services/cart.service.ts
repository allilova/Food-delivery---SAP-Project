// src/app/services/cart.service.ts
import { Injectable, PLATFORM_ID, Inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Cart, CartItem } from '../types/cart';
import { Food } from '../types/food';
import { isPlatformBrowser } from '@angular/common';

@Injectable({
  providedIn: 'root'
})
export class CartService {
  private cartItemsSubject = new BehaviorSubject<CartItem[]>([]);
  public cartItems = this.cartItemsSubject.asObservable();
  
  private apiUrl = environment.apiUrl;
  private isBrowser: boolean;
  
  constructor(
    private http: HttpClient,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {
    this.isBrowser = isPlatformBrowser(this.platformId);
    
    // Only load from localStorage in browser environment
    if (this.isBrowser) {
      this.loadCartFromLocalStorage();
    }
  }

  // Helper method to get auth token from localStorage
  private getAuthToken(): string | null {
    if (!this.isBrowser) return null;
    return localStorage.getItem('jwt_token');
  }

  // Add headers for requests that need authentication
  private getHeaders(): HttpHeaders {
    const token = this.getAuthToken();
    let headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });
    
    if (token) {
      headers = headers.set('Authorization', `Bearer ${token}`);
    }
    
    return headers;
  }

  // Load cart from localStorage
  private loadCartFromLocalStorage() {
    if (!this.isBrowser) return;
    
    const savedCart = localStorage.getItem('cart');
    if (savedCart) {
      try {
        this.cartItemsSubject.next(JSON.parse(savedCart));
      } catch (e) {
        console.error('Error parsing cart from localStorage', e);
        localStorage.removeItem('cart');
        this.cartItemsSubject.next([]);
      }
    }
  }

  // Save cart to localStorage
  private saveCartToLocalStorage() {
    if (!this.isBrowser) return;
    
    localStorage.setItem('cart', JSON.stringify(this.cartItemsSubject.value));
  }

  // Add item to cart
  addToCart(food: Food, quantity: number = 1): void {
    const currentItems = this.cartItemsSubject.value;
    const existingItemIndex = currentItems.findIndex(item => item.food.id === food.id);
    
    if (existingItemIndex > -1) {
      // Item already exists, update quantity
      const updatedItems = [...currentItems];
      updatedItems[existingItemIndex].quantity += quantity;
      updatedItems[existingItemIndex].price = updatedItems[existingItemIndex].quantity * food.price;
      this.cartItemsSubject.next(updatedItems);
    } else {
      // Item doesn't exist, add it
      const newItem: CartItem = {
        id: Date.now(), // temporary id
        food: food,
        quantity: quantity,
        price: food.price * quantity
      };
      this.cartItemsSubject.next([...currentItems, newItem]);
    }
    
    if (this.isBrowser) {
      this.saveCartToLocalStorage();
      this.syncWithBackend();
    }
  }

  // Remove item from cart
  removeFromCart(foodId: number): void {
    const currentItems = this.cartItemsSubject.value;
    const updatedItems = currentItems.filter(item => item.food.id !== foodId);
    this.cartItemsSubject.next(updatedItems);
    
    if (this.isBrowser) {
      this.saveCartToLocalStorage();
      this.syncWithBackend();
    }
  }

  // Update item quantity
  updateQuantity(foodId: number, quantity: number): void {
    if (quantity <= 0) {
      this.removeFromCart(foodId);
      return;
    }
    
    const currentItems = this.cartItemsSubject.value;
    const updatedItems = currentItems.map(item => {
      if (item.food.id === foodId) {
        return {
          ...item,
          quantity: quantity,
          price: quantity * item.food.price
        };
      }
      return item;
    });
    
    this.cartItemsSubject.next(updatedItems);
    
    if (this.isBrowser) {
      this.saveCartToLocalStorage();
      this.syncWithBackend();
    }
  }

  // Clear cart
  clearCart(): void {
    this.cartItemsSubject.next([]);
    
    if (this.isBrowser) {
      localStorage.removeItem('cart');
      this.syncWithBackend();
    }
  }

  // Get cart total
  getCartTotal(): number {
    return this.cartItemsSubject.value.reduce((total, item) => total + item.price, 0);
  }

  // Get cart items count
  getCartItemsCount(): number {
    return this.cartItemsSubject.value.reduce((count, item) => count + item.quantity, 0);
  }

  // Sync cart with backend if user is logged in
  private syncWithBackend(): void {
    if (!this.isBrowser) return;
    
    if (this.getAuthToken()) {
      const cartData = {
        items: this.cartItemsSubject.value.map(item => ({
          foodId: item.food.id,
          quantity: item.quantity
        }))
      };
      
      this.http.post(`${this.apiUrl}/api/cart/items`, cartData, {
        headers: this.getHeaders()
      }).subscribe({
        error: err => console.error('Error syncing cart with backend:', err)
      });
    }
  }

  // Get cart from backend
  fetchCartFromBackend(): Observable<Cart> {
    return this.http.get<Cart>(`${this.apiUrl}/api/cart`, {
      headers: this.getHeaders()
    });
  }

  // Load cart from backend and update local cart
  loadCartFromBackend(): void {
    if (!this.isBrowser) return;
    
    if (this.getAuthToken()) {
      this.fetchCartFromBackend().subscribe({
        next: (cart) => {
          if (cart && cart.items) {
            this.cartItemsSubject.next(cart.items);
            this.saveCartToLocalStorage();
          }
        },
        error: err => console.error('Error loading cart from backend:', err)
      });
    }
  }
}