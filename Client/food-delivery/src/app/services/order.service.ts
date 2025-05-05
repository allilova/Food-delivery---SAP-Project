import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, throwError, of, map, filter } from 'rxjs';
import { environment } from '../../environments/environment.development';
import { Order, OrderStatus } from '../types/order';
import { Food } from '../types/food';
import { NotificationService } from './notification.service';
import { ErrorHandlerService } from './error-handler.service';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private apiUrl = environment.apiUrl;

  constructor(
    private http: HttpClient,
    private errorHandler: ErrorHandlerService,
    private notificationService: NotificationService,
    private authService: AuthService
  ) { }

  // Helper method to get auth token
  private getAuthToken(): string | null {
    return localStorage.getItem('jwt_token');
  }

  // Add headers for authenticated requests
  private getHeaders(): HttpHeaders {
    const token = this.getAuthToken();
    let headers = new HttpHeaders();
    
    // Add content type header
    headers = headers.set('Content-Type', 'application/json');
    
    // Add authorization header if token exists
    if (token) {
      headers = headers.set('Authorization', `Bearer ${token}`);
      console.log('Added auth token to headers, token length:', token.length);
    } else {
      console.warn('No auth token available for request');
    }
    
    return headers;
  }

  // Create a new order
  createOrder(orderData: any): Observable<Order> {
    return this.http.post<Order>(`${this.apiUrl}/api/orders`, orderData, {
      headers: this.getHeaders()
    }).pipe(
      catchError(error => {
        console.error('Error creating order:', error);
        return throwError(() => error);
      })
    );
  }

  // Get all orders for the current user with pagination
  getUserOrders(page: number = 0, size: number = 10): Observable<any> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
      
    return this.http.get<any>(`${this.apiUrl}/api/orders/user`, {
      headers: this.getHeaders(),
      params: params
    }).pipe(
      catchError(error => {
        console.error('Error loading previous orders:', error);
        return throwError(() => error);
      })
    );
  }

  // Get order details by ID
  getOrderById(orderId: string): Observable<Order> {
    this.notificationService.info('Loading order details...', true, 1500);
    
    // Use supplier endpoint for restaurant users
    const endpoint = this.authService.userRole === 'ROLE_RESTAURANT' 
      ? `${this.apiUrl}/api/supplier/orders/${orderId}` 
      : `${this.apiUrl}/api/orders/${orderId}`;
    
    return this.http.get<Order>(endpoint, {
      headers: this.getHeaders()
    }).pipe(
      catchError(error => {
        console.error(`Error getting order ${orderId}:`, error);
        
        // FALLBACK TO MOCK DATA FOR DEMO PURPOSES
        if (orderId.startsWith('10')) {
          this.notificationService.info('Backend API is not available. Using demo data instead.');
          return this.getMockOrderById(orderId);
        }
        
        this.notificationService.error('Could not load order details.');
        return this.errorHandler.handleError(error, 'order retrieval');
      })
    );
  }
  
  // Get mock order by ID from localStorage
  private getMockOrderById(orderId: string): Observable<Order> {
    // Get all mock orders
    const allOrders = this.getSavedOrNewMockOrders();
    
    // Find the order by ID
    const order = allOrders.find(o => o.id === orderId);
    
    if (order) {
      return of(order);
    }
    
    // Create a base food object with required properties
    const createFood = (id: number, name: string, description: string, price: number): Food => ({
      id,
      name,
      description,
      price,
      imageUrl: '',
      isAvailable: true,
      categoryName: 'Demo Category',
      preparationTime: 15,
      ingredients: ['Demo Ingredient 1', 'Demo Ingredient 2']
    });
    
    // If not found, return a generic mock order
    const restaurant = {
      id: '1',
      name: 'Demo Restaurant',
      phone: '0888123456'
    };
    
    const newOrder = {
      id: orderId,
      items: [
        {
          id: 1,
          food: createFood(1, 'Demo Food Item', 'For demonstration purposes', 10.99),
          quantity: 1,
          price: 10.99,
          totalPrice: 10.99
        }
      ],
      totalAmount: 10.99,
      status: OrderStatus.PENDING,
      orderDate: new Date().toISOString(),
      deliveryAddress: 'Demo Address, Sofia',
      estimatedDeliveryTime: new Date(Date.now() + 30 * 60000).toISOString(),
      restaurant: restaurant
    };
    
    // Add this order to our saved orders
    allOrders.push(newOrder);
    this.saveMockOrders(allOrders);
    
    return of(newOrder);
  }
  
  // Validate order tracking information
  validateOrderTracking(orderId: string, phone: string): Observable<boolean> {
    // In a real app, this would call the backend to validate
    // For demo purposes, we'll just simulate a successful validation
    
    // Implement actual validation in production
    return of(true);
  }
  
  // Get order status updates
  getOrderStatusUpdates(orderId: string): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/api/orders/${orderId}/status-history`, {
      headers: this.getHeaders()
    }).pipe(
      catchError(error => {
        console.error(`Error getting status updates for order ${orderId}:`, error);
        return this.errorHandler.handleError(error, 'status update retrieval');
      })
    );
  }

  // Update order status
  updateOrderStatus(orderId: string, status: OrderStatus): Observable<Order> {
    let params = new HttpParams().set('status', status);
    
    // Use supplier endpoint for restaurant users
    const endpoint = this.authService.userRole === 'ROLE_DRIVER' 
      ? `${this.apiUrl}/api/supplier/orders/${orderId}/status` 
      : `${this.apiUrl}/api/orders/${orderId}/status`;
    
    return this.http.put<Order>(endpoint, {}, {
      headers: this.getHeaders(),
      params: params
    }).pipe(
      catchError(error => {
        console.error(`Error updating order ${orderId} status:`, error);
        
        // FALLBACK TO MOCK DATA FOR DEMO PURPOSES
        if (orderId.startsWith('10')) {
          this.notificationService.info('Backend API is not available. Using demo data instead.');
          return this.updateMockOrderStatus(orderId, status);
        }
        
        this.notificationService.error(`Could not update order status to ${status}.`);
        return throwError(() => error);
      })
    );
  }
  
  // Update mock order status in localStorage and return the updated order
  private updateMockOrderStatus(orderId: string, newStatus: OrderStatus): Observable<Order> {
    // Get all mock orders
    const allOrders = this.getSavedOrNewMockOrders();
    
    // Find the order to update
    const orderIndex = allOrders.findIndex(order => order.id === orderId);
    
    if (orderIndex === -1) {
      this.notificationService.error(`Order ${orderId} not found.`);
      return throwError(() => new Error(`Order ${orderId} not found`));
    }
    
    // Update the order status
    allOrders[orderIndex].status = newStatus;
    
    // Add delivery time if order is delivered
    if (newStatus === OrderStatus.DELIVERED) {
      allOrders[orderIndex].deliveryTime = new Date().toISOString();
    }
    
    // Save updated orders to localStorage
    this.saveMockOrders(allOrders);
    
    // Return the updated order
    return of(allOrders[orderIndex]);
  }
  
  // Local storage key for mock orders
  private readonly MOCK_ORDERS_KEY = 'mock_supplier_orders';
  
  // For restaurant owners: get orders by status
  getOrdersByStatus(status: OrderStatus): Observable<Order[]> {
    // Create params with status
    let params = new HttpParams().set('status', status);
    
    console.log(`Fetching orders with status ${status} from ${this.apiUrl}/api/supplier/orders`);
    
    // Get headers and log them
    const headers = this.getHeaders();
    const tokenAvailable = this.getAuthToken() !== null;
    console.log('Auth token available:', tokenAvailable);
    console.log('Auth headers for supplier request:', headers);
    
    // If token is not available, add allowMockData=true parameter
    if (!tokenAvailable) {
      params = params.set('allowMockData', 'true');
      console.log('Added allowMockData parameter due to missing auth token');
    }
    
    // First try to get data from the backend
    return this.http.get<any>(`${this.apiUrl}/api/supplier/orders`, {
      headers: headers,
      params: params
    }).pipe(
      map(response => {
        console.log(`Successfully retrieved orders with status ${status}:`, response);
        // Handle pagination response from Spring Data
        if (response && response.content) {
          return response.content as Order[];
        }
        if (response && Array.isArray(response)) {
          return response as Order[];
        }
        if (response && Object.keys(response).length === 0) {
          console.log('Empty response from backend, returning empty array');
          return [] as Order[];
        }
        // If we received something but it's not in the expected format
        console.warn('Unexpected response format:', response);
        return response as Order[];
      }),
      catchError(error => {
        console.error(`Error getting orders with status ${status}:`, error);
        
        // Debug the error in more detail
        if (error.error && error.error.message) {
          console.error('Server error message:', error.error.message);
        }
        
        if (error.status === 401) {
          console.warn('Authentication failed (401 Unauthorized). User might not be logged in or token is invalid.');
          this.notificationService.warning('Authentication failed. Please log in again.');
          
          // Clear token if it's invalid and try again later
          if (this.getAuthToken()) {
            console.warn('Auth token exists but was rejected. It might be invalid or expired.');
            // Check token expiration logic here if needed
          } else {
            console.warn('No auth token found. User might need to log in.');
          }
        } else if (error.status === 500) {
          console.error('Internal server error detected. Response:', error.error);
        }
        
        // Add a debug call to the auth debug endpoint
        this.authService.debugAuthToken().subscribe({
          next: (info) => console.log('Auth debug info:', info),
          error: (err) => console.error('Auth debug error:', err)
        });
        
        // FALLBACK TO MOCK DATA FOR DEMO PURPOSES
        this.notificationService.info('Backend API is not available. Using demo data instead.');
        return this.getMockOrdersByStatus(status);
      })
    );
  }
  
  // Get saved mock orders from localStorage or create new ones if none exist
  private getSavedOrNewMockOrders(): Order[] {
    if (typeof localStorage === 'undefined') return [];
    
    const savedOrders = localStorage.getItem(this.MOCK_ORDERS_KEY);
    if (savedOrders) {
      try {
        return JSON.parse(savedOrders);
      } catch (e) {
        console.error('Error parsing saved mock orders', e);
      }
    }
    
    // Create initial mock data
    const initialMockOrders = [
      ...this.createMockOrdersByStatus(OrderStatus.PENDING),
      ...this.createMockOrdersByStatus(OrderStatus.CONFIRMED),
      ...this.createMockOrdersByStatus(OrderStatus.PREPARING),
      ...this.createMockOrdersByStatus(OrderStatus.READY),
      ...this.createMockOrdersByStatus(OrderStatus.OUT_FOR_DELIVERY),
      ...this.createMockOrdersByStatus(OrderStatus.DELIVERED)
    ];
    
    // Save to localStorage
    localStorage.setItem(this.MOCK_ORDERS_KEY, JSON.stringify(initialMockOrders));
    
    return initialMockOrders;
  }
  
  // Save mock orders to localStorage
  private saveMockOrders(orders: Order[]): void {
    if (typeof localStorage === 'undefined') return;
    
    localStorage.setItem(this.MOCK_ORDERS_KEY, JSON.stringify(orders));
  }
  
  // Create mock orders for a specific status
  private createMockOrdersByStatus(status: OrderStatus): Order[] {
    // Create a base food object with required properties
    const createFood = (id: number, name: string, description: string, price: number): Food => ({
      id,
      name,
      description,
      price,
      imageUrl: '',
      isAvailable: true,
      categoryName: 'Demo Category',
      preparationTime: 15,
      ingredients: ['Demo Ingredient 1', 'Demo Ingredient 2']
    });
    
    // Demo restaurant data
    const restaurant = {
      id: '1',
      name: 'Demo Restaurant',
      phone: '0888123456'
    };
    
    // Create mock orders based on status
    const orders: Order[] = [];
    
    if (status === OrderStatus.PENDING) {
      orders.push(
        {
          id: '101',
          items: [
            {
              id: 1,
              food: createFood(1, 'Margherita Pizza', 'Classic Italian pizza', 12.99),
              quantity: 2,
              price: 25.98,
              totalPrice: 25.98
            },
            {
              id: 2,
              food: createFood(5, 'Coca Cola', '330ml', 2.50),
              quantity: 2,
              price: 5.00,
              totalPrice: 5.00
            }
          ],
          totalAmount: 30.98,
          status: OrderStatus.PENDING,
          orderDate: new Date().toISOString(),
          deliveryAddress: '123 Demo Street, Sofia',
          estimatedDeliveryTime: new Date(Date.now() + 45 * 60000).toISOString(),
          restaurant: restaurant
        },
        {
          id: '102',
          items: [
            {
              id: 3,
              food: createFood(2, 'Chicken Wrap', 'Grilled chicken with fresh vegetables', 8.99),
              quantity: 1,
              price: 8.99,
              totalPrice: 8.99
            }
          ],
          totalAmount: 8.99,
          status: OrderStatus.PENDING,
          orderDate: new Date().toISOString(),
          deliveryAddress: '456 Sample Road, Sofia',
          estimatedDeliveryTime: new Date(Date.now() + 30 * 60000).toISOString(),
          restaurant: restaurant
        }
      );
    } else if (status === OrderStatus.CONFIRMED) {
      orders.push(
        {
          id: '103',
          items: [
            {
              id: 4,
              food: createFood(3, 'Beef Burger', 'Premium beef patty with cheese', 14.99),
              quantity: 2,
              price: 29.98,
              totalPrice: 29.98
            },
            {
              id: 5,
              food: createFood(6, 'French Fries', 'Crispy potato fries', 4.50),
              quantity: 1,
              price: 4.50,
              totalPrice: 4.50
            }
          ],
          totalAmount: 34.48,
          status: OrderStatus.CONFIRMED,
          orderDate: new Date(Date.now() - 15 * 60000).toISOString(),
          deliveryAddress: '789 Example Blvd, Sofia',
          estimatedDeliveryTime: new Date(Date.now() + 25 * 60000).toISOString(),
          restaurant: restaurant,
          driver: {
            id: '1',
            name: 'John Driver',
            phone: '0888987654'
          }
        }
      );
    } else if (status === OrderStatus.DELIVERED) {
      orders.push(
        {
          id: '104',
          items: [
            {
              id: 6,
              food: createFood(4, 'Caesar Salad', 'Fresh salad with chicken', 9.99),
              quantity: 1,
              price: 9.99,
              totalPrice: 9.99
            }
          ],
          totalAmount: 9.99,
          status: OrderStatus.DELIVERED,
          orderDate: new Date(Date.now() - 90 * 60000).toISOString(),
          deliveryAddress: '101 Test Street, Sofia',
          deliveryTime: new Date(Date.now() - 45 * 60000).toISOString(),
          restaurant: restaurant
        },
        {
          id: '105',
          items: [
            {
              id: 7,
              food: createFood(7, 'Pasta Carbonara', 'Italian pasta with creamy sauce', 13.50),
              quantity: 2,
              price: 27.00,
              totalPrice: 27.00
            }
          ],
          totalAmount: 27.00,
          status: OrderStatus.DELIVERED,
          orderDate: new Date(Date.now() - 120 * 60000).toISOString(),
          deliveryAddress: '202 Sample Avenue, Sofia',
          deliveryTime: new Date(Date.now() - 60 * 60000).toISOString(),
          restaurant: restaurant
        }
      );
    }
    
    return orders;
  }
  
  // Get mock orders from localStorage filtered by status
  private getMockOrdersByStatus(status: OrderStatus): Observable<Order[]> {
    const allOrders = this.getSavedOrNewMockOrders();
    
    // Filter orders by status
    const filteredOrders = allOrders.filter(order => {
      // Special case for active orders - include multiple statuses
      if (status === OrderStatus.IN_PROGRESS) {
        return [
          OrderStatus.CONFIRMED, 
          OrderStatus.PREPARING, 
          OrderStatus.READY, 
          OrderStatus.OUT_FOR_DELIVERY
        ].includes(order.status);
      }
      return order.status === status;
    });
    
    return of(filteredOrders);
  }
}