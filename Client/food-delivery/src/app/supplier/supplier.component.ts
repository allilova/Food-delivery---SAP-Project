import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { OrderService } from '../services/order.service';
import { AuthService } from '../services/auth.service';
import { Order, OrderStatus } from '../types/order';
import { LoadingSpinnerComponent } from '../components/loading-spinner.component';
import { NotificationService } from '../services/notification.service';
import { environment } from '../../environments/environment';
import { Observable, of } from 'rxjs';

@Component({
  selector: 'app-supplier',
  standalone: true,
  imports: [RouterLink, CommonModule, LoadingSpinnerComponent, FormsModule],
  templateUrl: './supplier.component.html',
  styleUrl: './supplier.component.css'
})
export class SupplierComponent implements OnInit {
  newOrders: Order[] = [];
  activeOrders: Order[] = [];
  completedOrders: Order[] = [];
  loading = {
    newOrders: false,
    activeOrders: false,
    completedOrders: false
  };
  error = {
    newOrders: '',
    activeOrders: '',
    completedOrders: ''
  };
  OrderStatus = OrderStatus; // Make enum available in template
  isDevelopment = !environment.production; // Only show debug buttons in development mode
  authDebugInfo: any = null;

  constructor(
    private orderService: OrderService,
    private authService: AuthService,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    // Check if user is logged in and has the correct role  
    if (!this.authService.isLoggedIn || this.authService.userRole !== 'ROLE_DRIVER') {
      this.notificationService.warning('You must be logged in as a restaurant owner to access this page.');
      return;
    }

    // Debug auth token
    this.checkAuthToken();

    // Load orders
    this.loadNewOrders();
    this.loadActiveOrders();
    this.loadCompletedOrders();
  }
  
  private checkAuthToken(): void {
    const token = localStorage.getItem('jwt_token');
    if (!token) {
      this.notificationService.warning('No authentication token found. You may need to log in again.');
      console.warn('No JWT token found in localStorage');
    } else {
      console.log('JWT token found, length:', token.length);
      // Call debug endpoint to verify token
      this.authService.debugAuthToken().subscribe({
        next: (info) => {
          console.log('Auth debug info on component init:', info);
          if (info && info.auth && info.auth.isAuthenticated) {
            this.notificationService.success('Authentication verified');
          } else {
            this.notificationService.warning('Authentication token may be invalid. Using demo data.');
          }
        },
        error: (err) => {
          console.error('Error verifying auth token:', err);
          this.notificationService.error('Could not verify authentication. Using demo data.');
        }
      });
    }
  }

  loadNewOrders(): void {
    this.loading.newOrders = true;
    this.error.newOrders = '';  // Clear any previous errors
    
    this.orderService.getOrdersByStatus(OrderStatus.PENDING).subscribe({
      next: (orders) => {
        console.log('Received new orders:', orders);
        if (orders && Array.isArray(orders)) {
          this.newOrders = orders;
        } else {
          // If we get a non-array response, use an empty array
          console.warn('Received non-array response for new orders:', orders);
          this.newOrders = [];
        }
        this.loading.newOrders = false;
      },
      error: (err) => {
        console.error('Error loading new orders:', err);
        this.error.newOrders = 'Failed to load new orders. Using mock data instead.';
        this.loading.newOrders = false;
        
        // Use mock data as fallback
        this.loadMockOrdersWithStatus(OrderStatus.PENDING).subscribe((mockOrders: Order[]) => {
          this.newOrders = mockOrders;
          this.notificationService.info('Using demo data for new orders.');
        });
      }
    });
  }

  loadActiveOrders(): void {
    this.loading.activeOrders = true;
    this.error.activeOrders = '';  // Clear any previous errors
    
    // Get orders with any "active" status using IN_PROGRESS virtual status
    this.orderService.getOrdersByStatus(OrderStatus.IN_PROGRESS).subscribe({
      next: (orders) => {
        console.log('Received active orders:', orders);
        if (orders && Array.isArray(orders)) {
          this.activeOrders = orders;
        } else {
          // If we get a non-array response, use an empty array
          console.warn('Received non-array response for active orders:', orders);
          this.activeOrders = [];
        }
        
        // Also get orders with more specific statuses to ensure we have everything
        // Only do this if we got a valid response for IN_PROGRESS
        this.getOrdersWithStatus(OrderStatus.CONFIRMED);
        this.getOrdersWithStatus(OrderStatus.PREPARING);
        this.getOrdersWithStatus(OrderStatus.READY);
        this.getOrdersWithStatus(OrderStatus.OUT_FOR_DELIVERY);
        
        this.loading.activeOrders = false;
      },
      error: (err) => {
        console.error('Error loading active orders:', err);
        this.error.activeOrders = 'Failed to load active orders. Using mock data instead.';
        this.loading.activeOrders = false;
        
        // Use mock data as fallback for each active status
        this.loadMockActiveOrders();
      }
    });
  }

  getOrdersWithStatus(status: OrderStatus): void {
    this.orderService.getOrdersByStatus(status).subscribe({
      next: (orders) => {
        if (orders && Array.isArray(orders) && orders.length > 0) {
          console.log(`Found ${orders.length} orders with status ${status}`);
          // Add these orders to the active orders if they aren't already there
          orders.forEach(order => {
            if (!this.activeOrders.some(o => o.id === order.id)) {
              this.activeOrders.push(order);
            }
          });
        }
      },
      error: (err) => {
        console.error(`Error loading ${status} orders:`, err);
        // We don't set an error message here since this is a supplementary request
        
        // Try to get mock data for this specific status
        this.loadMockOrdersWithStatus(status).subscribe((mockOrders: Order[]) => {
          if (mockOrders && mockOrders.length > 0) {
            // Add mock orders to active orders
            mockOrders.forEach((order: Order) => {
              if (!this.activeOrders.some(o => o.id === order.id)) {
                this.activeOrders.push(order);
              }
            });
          }
        });
      }
    });
  }

  loadCompletedOrders(): void {
    this.loading.completedOrders = true;
    this.error.completedOrders = '';  // Clear any previous errors
    
    this.orderService.getOrdersByStatus(OrderStatus.DELIVERED).subscribe({
      next: (orders) => {
        console.log('Received completed orders:', orders);
        if (orders && Array.isArray(orders)) {
          this.completedOrders = orders;
        } else {
          // If we get a non-array response, use an empty array
          console.warn('Received non-array response for completed orders:', orders);
          this.completedOrders = [];
        }
        this.loading.completedOrders = false;
      },
      error: (err) => {
        console.error('Error loading completed orders:', err);
        this.error.completedOrders = 'Failed to load completed orders. Using mock data instead.';
        this.loading.completedOrders = false;
        
        // Use mock data as fallback
        this.loadMockOrdersWithStatus(OrderStatus.DELIVERED).subscribe((mockOrders: Order[]) => {
          this.completedOrders = mockOrders;
          this.notificationService.info('Using demo data for completed orders.');
        });
      }
    });
  }
  
  // Helper method to load mock active orders with all relevant statuses
  private loadMockActiveOrders(): void {
    // Start with an empty array
    this.activeOrders = [];
    
    // Load mock orders for each active status
    const activeStatuses = [
      OrderStatus.CONFIRMED,
      OrderStatus.PREPARING, 
      OrderStatus.READY,
      OrderStatus.OUT_FOR_DELIVERY
    ];
    
    // Show notification once
    this.notificationService.info('Using demo data for active orders.');
    
    // Load each status
    activeStatuses.forEach(status => {
      this.loadMockOrdersWithStatus(status).subscribe((mockOrders: Order[]) => {
        if (mockOrders && mockOrders.length > 0) {
          // Add mock orders to active orders without duplicates
          mockOrders.forEach((order: Order) => {
            if (!this.activeOrders.some(o => o.id === order.id)) {
              this.activeOrders.push(order);
            }
          });
        }
      });
    });
  }
  
  // Helper method to get mock orders of a specific status
  private loadMockOrdersWithStatus(status: OrderStatus): Observable<Order[]> {
    // This just delegates to the order service's mock data method
    return of(this.getMockOrdersWithStatus(status));
  }
  
  // Generate mock orders based on status - similar to the one in order.service.ts
  // but simplified for the component's use
  private getMockOrdersWithStatus(status: OrderStatus): Order[] {
    const mockOrders: Order[] = [];
    
    // Create base food item
    const createFood = (id: number, name: string): any => ({
      id: id,
      name: name,
      price: 10.99,
      description: 'Mock food item',
      imageUrl: '',
      isAvailable: true,
      categoryName: 'Mock Category',
      preparationTime: 15,
      ingredients: ['Ingredient 1', 'Ingredient 2']
    });
    
    // Restaurant info
    const restaurant = {
      id: '1',
      name: 'Mock Restaurant',
      phone: '0888123456'
    };
    
    // Generate 2 orders for the requested status
    for (let i = 1; i <= 2; i++) {
      // Create new order with appropriate status
      const order: Order = {
        id: `mock-${status}-${i}`,
        items: [
          {
            id: i,
            food: createFood(i, `Mock ${status} Food ${i}`),
            quantity: 1,
            price: 10.99,
            totalPrice: 10.99
          }
        ],
        totalAmount: 10.99,
        status: status,
        orderDate: new Date(Date.now() - i * 15 * 60000).toISOString(),
        deliveryAddress: 'Mock Address ' + i,
        estimatedDeliveryTime: new Date(Date.now() + 30 * 60000).toISOString(),
        restaurant: restaurant
      };
      
      mockOrders.push(order);
    }
    
    return mockOrders;
  }

  // Get a summary of the order (first item and total count)
  getOrderSummary(order: Order): string {
    if (!order.items || order.items.length === 0) {
      return 'No items';
    }
    
    const firstItem = order.items[0];
    const totalItems = order.items.length;
    
    if (totalItems === 1) {
      return `${firstItem.food.name}`;
    } else {
      return `${firstItem.food.name} + ${totalItems - 1} more item${totalItems > 2 ? 's' : ''}`;
    }
  }

  // Calculate bonus based on completed orders
  calculateBonus(orderCount: number): number {
    // Simple calculation - 2 BGN per order
    return orderCount * 2;
  }

  // Accept order (change status from PENDING to IN_PROGRESS)
  acceptOrder(orderId: string): void {
    this.orderService.updateOrderStatus(orderId, OrderStatus.CONFIRMED).subscribe({
      next: () => {
        // Move order from new to active
        const order = this.newOrders.find(o => o.id.toString() === orderId);
        if (order) {
          order.status = OrderStatus.CONFIRMED;
          this.newOrders = this.newOrders.filter(o => o.id.toString() !== orderId);
          this.activeOrders.push(order);
          this.notificationService.success('Order accepted successfully!');
        }
      },
      error: (err) => {
        console.error('Error accepting order:', err);
        this.notificationService.error('Failed to accept order. Please try again.');
      }
    });
  }

  // Mark order as ready for delivery
  markOrderAsReady(orderId: string): void {
    this.updateOrderStatus(orderId, OrderStatus.READY);
  }

  // Update order status
  updateOrderStatus(orderId: string, status: OrderStatus): void {
    this.orderService.updateOrderStatus(orderId, status).subscribe({
      next: () => {
        // Update lists based on new status
        if (status === OrderStatus.DELIVERED) {
          const order = this.activeOrders.find(o => o.id.toString() === orderId);
          if (order) {
            order.status = OrderStatus.DELIVERED;
            this.activeOrders = this.activeOrders.filter(o => o.id.toString() !== orderId);
            this.completedOrders.push(order);
            this.notificationService.success('Order marked as delivered!');
          }
        } else {
          // Just update the status
          const orderIndex = this.activeOrders.findIndex(o => o.id.toString() === orderId);
          if (orderIndex !== -1) {
            this.activeOrders[orderIndex].status = status;
            this.notificationService.success(`Order status updated to ${status}!`);
          }
        }
      },
      error: (err) => {
        console.error('Error updating order status:', err);
        this.notificationService.error('Failed to update order status. Please try again.');
      }
    });
  }
  
  // Debug authentication - useful for troubleshooting
  debugAuth(): void {
    this.authService.debugAuthToken().subscribe({
      next: (info) => {
        this.authDebugInfo = info;
        console.log('Auth debug info:', info);
        
        // Show debug info in an alert for easy viewing
        const debugInfo = JSON.stringify(info, null, 2);
        alert(`Auth Debug Info:\n${debugInfo}`);
      },
      error: (err) => {
        console.error('Error debugging auth token:', err);
      }
    });
  }
}