// src/app/services/restaurant.service.ts
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, of, from } from 'rxjs';
import { map, catchError, delay, switchMap, tap, take } from 'rxjs/operators';
import { environment } from '../../environments/environment.development';
import { Restaurant } from '../types/restaurants';
import { Food } from '../types/food';

@Injectable({
  providedIn: 'root'
})
export class RestaurantService {
  private apiUrl = environment.apiUrl;
  // Flag to indicate if the backend is available
  private useOfflineMode: boolean = false;

  constructor(private http: HttpClient) { 
    // Check if we're running in a test or SSR context
    if (typeof window !== 'undefined') {
      // Test connectivity to the backend
      this.http.get(`${this.apiUrl}/api/restaurants`).pipe(
        take(1),
        catchError(err => {
          console.warn('Backend seems to be unavailable, enabling offline mode');
          this.useOfflineMode = true;
          return of(null);
        })
      ).subscribe();
    } else {
      // In SSR context, use offline mode by default
      this.useOfflineMode = true;
    }
  }

  // Helper method to get auth token
  private getAuthToken(): string | null {
    return localStorage.getItem('jwt_token');
  }

  // Add headers for authenticated requests
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

  // Get all restaurants
  getAllRestaurants(): Observable<Restaurant[]> {
    console.log('Getting all restaurants');
    
    // If offline mode is enabled, go straight to mock data
    if (this.useOfflineMode) {
      console.log('Using mock data in offline mode');
      return this.getOfflineRestaurants();
    }
    
    return this.http.get<any[]>(`${this.apiUrl}/api/restaurants`).pipe(
      // Format response to ensure it matches Restaurant interface
      map(restaurantsResponse => {
        console.log('Raw API response for restaurants:', restaurantsResponse);
        
        // Check if the response is an array
        if (!Array.isArray(restaurantsResponse)) {
          console.warn('API response is not an array:', restaurantsResponse);
          return [];
        }
        
        // Format each restaurant to match the Restaurant interface
        const formattedRestaurants = restaurantsResponse.map(r => this.formatRestaurantResponse(r));
        console.log('Formatted API restaurants:', formattedRestaurants);
        
        return formattedRestaurants;
      }),
      // Add created restaurants from session storage
      map(restaurants => {
        // Get created restaurants from session storage
        const createdRestaurants = this.safelyGetFromStorage('createdRestaurants');
        
        if (createdRestaurants && createdRestaurants.length > 0) {
          console.log(`Adding ${createdRestaurants.length} created restaurants from session storage`);
          
          // Combine API restaurants with created ones
          // Make sure we don't have duplicates based on ID
          const existingIds = new Set(restaurants.map((r: Restaurant) => r.id));
          const uniqueCreatedRestaurants = createdRestaurants.filter((r: Restaurant) => !existingIds.has(r.id));
          
          return [...restaurants, ...uniqueCreatedRestaurants];
        }
        
        return restaurants;
      }),
      catchError(error => {
        console.error('Error fetching all restaurants:', error);
        
        // Try to get restaurants from session storage
        const createdRestaurants = this.safelyGetFromStorage('createdRestaurants');
        
        if (createdRestaurants && createdRestaurants.length > 0) {
          console.log(`Using ${createdRestaurants.length} created restaurants from session storage as fallback`);
          return of(createdRestaurants);
        }
        
        // If API fails and no session storage, fall back to mock data
        console.log('Using mock restaurants as fallback');
        return from(import('../mock-models/sample-models')).pipe(
          map(models => {
            console.log('Using mock restaurants:', models.mockRestaurants);
            return models.mockRestaurants;
          }),
          catchError(() => {
            console.error('Failed to load mock data');
            return of([]);
          })
        );
      })
    );
  }

  // Get restaurant by ID
  getRestaurantById(id: string): Observable<Restaurant> {
    // First try to find the restaurant in session storage
    const createdRestaurants = this.safelyGetFromStorage('createdRestaurants');
    
    if (createdRestaurants) {
      const foundRestaurant = createdRestaurants.find((r: Restaurant) => r.id === id);
      
      if (foundRestaurant) {
        console.log(`Found restaurant ${id} in session storage`);
        return of(foundRestaurant);
      }
    }
    
    // If not found in session storage, try the API
    return this.http.get<Restaurant>(`${this.apiUrl}/api/restaurants/${id}`).pipe(
      catchError(error => {
        console.error(`Error fetching restaurant ${id} from API:`, error);
        
        // If API fails, try to get from mock data as a last resort
        return from(import('../mock-models/sample-models')).pipe(
          map(models => {
            const mockRestaurant = models.mockRestaurants.find(r => r.id === id);
            if (mockRestaurant) {
              console.log(`Found mock restaurant for ${id}`);
              return mockRestaurant;
            }
            throw new Error(`Restaurant with ID ${id} not found`);
          }),
          catchError(err => {
            console.error(`Error finding restaurant ${id} in mock data:`, err);
            throw new Error(`Restaurant with ID ${id} not found`);
          })
        );
      })
    );
  }

  // Search restaurants
  searchRestaurants(keyword: string): Observable<Restaurant[]> {
    console.log(`Searching for restaurants with keyword: "${keyword}"`);
    
    // First try the backend search API
    return this.http.get<any[]>(`${this.apiUrl}/api/restaurants/search?keyword=${keyword}`).pipe(
      switchMap(restaurantsResponse => {
        console.log('Raw search API response:', restaurantsResponse);
        
        // Check if the response is an array
        if (!Array.isArray(restaurantsResponse)) {
          console.warn('Search API response is not an array:', restaurantsResponse);
          // If this happens, fall back to client-side filtering
          return this.performClientSideSearch(keyword);
        }
        
        // Format each restaurant to match the Restaurant interface
        const formattedRestaurants = restaurantsResponse.map(r => this.formatRestaurantResponse(r));
        console.log('Formatted search results:', formattedRestaurants);
        
        // Include created restaurants from session storage in search results
        return of(this.addCreatedRestaurantsToSearchResults(formattedRestaurants, keyword));
      }),
      catchError(error => {
        console.error('Error from search API:', error);
        
        // Fall back to client-side search
        console.log('Falling back to client-side search');
        return this.performClientSideSearch(keyword);
      })
    );
  }
  
  // Helper to add created restaurants to search results
  private addCreatedRestaurantsToSearchResults(restaurants: Restaurant[], keyword: string): Restaurant[] {
    const lowerKeyword = keyword.toLowerCase();
    try {
      const storedRestaurants = sessionStorage.getItem('createdRestaurants');
      if (storedRestaurants) {
        const createdRestaurants = JSON.parse(storedRestaurants);
        // Filter created restaurants by keyword
        const matchingCreatedRestaurants = createdRestaurants.filter((r: Restaurant) => 
          r.name.toLowerCase().includes(lowerKeyword) || 
          r.foodType?.toLowerCase().includes(lowerKeyword) ||
          r.address.toLowerCase().includes(lowerKeyword)
        );
        
        if (matchingCreatedRestaurants.length > 0) {
          console.log(`Found ${matchingCreatedRestaurants.length} matching created restaurants for keyword "${keyword}"`);
          
          // Combine API results with matching created restaurants
          const existingIds = new Set(restaurants.map((r: Restaurant) => r.id.toString()));
          const uniqueCreatedRestaurants = matchingCreatedRestaurants.filter((r: Restaurant) => 
            !existingIds.has(r.id.toString())
          );
          
          return [...restaurants, ...uniqueCreatedRestaurants];
        }
      }
    } catch (e) {
      console.error('Error processing created restaurants for search:', e);
    }
    
    return restaurants;
  }
  
  // Perform search on all restaurants client-side
  private performClientSideSearch(keyword: string): Observable<Restaurant[]> {
    console.log('Performing client-side search');
    const lowerKeyword = keyword.toLowerCase();
    
    // Get all restaurants first
    return this.getAllRestaurants().pipe(
      map(allRestaurants => {
        console.log(`Filtering ${allRestaurants.length} restaurants by keyword "${keyword}"`);
        
        // Filter by keyword
        return allRestaurants.filter(r => 
          r.name.toLowerCase().includes(lowerKeyword) || 
          r.foodType?.toLowerCase().includes(lowerKeyword) ||
          r.address.toLowerCase().includes(lowerKeyword)
        );
      }),
      catchError(error => {
        console.error('Error during client-side search:', error);
        
        // Last resort: search in session storage and mock data
        try {
          const results: Restaurant[] = [];
          
          // Check session storage first
          const createdRestaurants = this.safelyGetFromStorage('createdRestaurants');
          if (createdRestaurants) {
            const matchingCreated = createdRestaurants.filter((r: Restaurant) => 
              r.name.toLowerCase().includes(lowerKeyword) || 
              r.foodType?.toLowerCase().includes(lowerKeyword) ||
              r.address.toLowerCase().includes(lowerKeyword)
            );
            results.push(...matchingCreated);
          }
          
          if (results.length > 0) {
            console.log(`Found ${results.length} results in session storage`);
            return of(results);
          }
        } catch (e) {
          console.error('Error searching in session storage:', e);
        }
        
        // Last resort: search in mock data
        console.log('Falling back to mock data for search');
        return from(import('../mock-models/sample-models')).pipe(
          map(models => {
            const mockResults = models.mockRestaurants.filter((r: Restaurant) => 
              r.name.toLowerCase().includes(lowerKeyword) || 
              r.foodType?.toLowerCase().includes(lowerKeyword) ||
              r.address.toLowerCase().includes(lowerKeyword)
            );
            console.log(`Found ${mockResults.length} results in mock data`);
            return mockResults;
          }),
          catchError(() => {
            console.error('Failed to load mock data for search');
            return of([]);
          })
        );
      })
    );
  }

  // Add/remove restaurant from favorites
  toggleFavorite(restaurantId: string): Observable<any> {
    console.log(`Toggling favorite status for restaurant ${restaurantId}`);
    
    return this.http.put(`${this.apiUrl}/api/restaurants/${restaurantId}/add-favourites`, {}, {
      headers: this.getHeaders()
    }).pipe(
      tap(response => {
        console.log('Toggle favorite API response:', response);
        // Update local session storage favorites for immediate UI feedback
        this.updateLocalFavorites(restaurantId);
      }),
      catchError(error => {
        console.error('Error toggling favorite status:', error);
        
        // For demo/offline use, still update local session storage
        this.updateLocalFavorites(restaurantId);
        
        return of({ success: true, local: true });
      })
    );
  }
  
  // Helper method to update local favorites in session storage
  private updateLocalFavorites(restaurantId: string): void {
    console.log(`Updating local favorites for restaurant ${restaurantId}`);
    
    // Get the restaurant details
    this.getRestaurantById(restaurantId).pipe(
      // We only need one emission and want to complete the observable
      take(1)
    ).subscribe({
      next: restaurant => {
        if (!restaurant) {
          console.error(`Restaurant ${restaurantId} not found when updating favorites`);
          return;
        }
        
        // Get current favorites from session storage
        const favorites: Restaurant[] = this.safelyGetFromStorage('userFavorites') || [];
        
        console.log('Current favorites before update:', favorites);
        
        // Check if restaurant is already in favorites
        const existingIndex = favorites.findIndex(r => r.id.toString() === restaurantId.toString());
        
        if (existingIndex >= 0) {
          // Remove from favorites (toggle off)
          favorites.splice(existingIndex, 1);
          console.log(`Removed restaurant ${restaurantId} from local favorites`);
        } else {
          // Add to favorites (toggle on)
          favorites.push(restaurant);
          console.log(`Added restaurant ${restaurantId} to local favorites`);
        }
        
        // Update session storage
        this.safelySetInStorage('userFavorites', favorites);
        console.log('Updated favorites in session storage:', favorites);
      },
      error: err => {
        console.error(`Error fetching restaurant ${restaurantId} for favorites update:`, err);
      }
    });
  }

  // Get restaurant menu
  getRestaurantMenu(restaurantId: string): Observable<Food[]> {
    console.log('Fetching menu for restaurant:', restaurantId);
    
    // Always use mock data first to avoid dependency on backend for demo purposes
    return from(import('../mock-models/sample-models')).pipe(
      switchMap(models => {
        console.log('Using mock menu data for demo purposes');
        
        // Clone the mock menu items to avoid reference issues
        let menuItems: Food[] = JSON.parse(JSON.stringify(models.mockMenuItems));
        
        // Add a custom property to indicate this is mock data
        menuItems.forEach((item: Food) => {
          item.restaurantId = parseInt(restaurantId);
        });
        
        // Process items from session storage (created, updated, and deleted)
        try {
          // 1. Add newly created items
          const createdItems = sessionStorage.getItem('createdMenuItems');
          if (createdItems) {
            const parsedCreatedItems = JSON.parse(createdItems);
            // Filter to only include items for this restaurant
            const restaurantCreatedItems = parsedCreatedItems.filter(
              (item: Food) => item.restaurantId === parseInt(restaurantId) || 
                             !item.restaurantId // Include items without a restaurantId as well
            );
            
            if (restaurantCreatedItems.length > 0) {
              console.log(`Found ${restaurantCreatedItems.length} created menu items for restaurant ${restaurantId}`);
              // Add the created items to our menu items
              menuItems.push(...restaurantCreatedItems);
            }
          }
          
          // 2. Apply any updates to existing items
          const updatedItems = sessionStorage.getItem('updatedMenuItems');
          if (updatedItems) {
            const parsedUpdatedItems = JSON.parse(updatedItems);
            
            // Replace existing items with updated versions
            parsedUpdatedItems.forEach((updatedItem: Food) => {
              const index = menuItems.findIndex((item: Food) => item.id === updatedItem.id);
              if (index >= 0) {
                console.log(`Replacing menu item ${updatedItem.id} with updated version`);
                menuItems[index] = updatedItem;
              }
            });
          }
          
          // 3. Remove any deleted items
          const deletedItems = sessionStorage.getItem('deletedMenuItems');
          if (deletedItems) {
            const deletedIds = JSON.parse(deletedItems);
            if (deletedIds.length > 0) {
              console.log(`Removing ${deletedIds.length} deleted menu items`);
              // Create a new array instead of modifying the existing one
              menuItems = menuItems.filter((item: Food) => !deletedIds.includes(item.id));
            }
          }
        } catch (e) {
          console.error('Error processing menu items from session storage:', e);
        }
        
        return of(menuItems);
      }),
      catchError(error => {
        console.error('Error loading mock data:', error);
        return of([]);
      })
    );
  }

  // For restaurant owners: update restaurant info
  updateRestaurant(restaurantId: string, data: any): Observable<Restaurant> {
    return this.http.put<Restaurant>(`${this.apiUrl}/api/admin/restaurants/${restaurantId}`, data, {
      headers: this.getHeaders()
    }).pipe(
      catchError(error => {
        console.error(`Error updating restaurant ${restaurantId}:`, error);
        throw error;
      })
    );
  }

  // For restaurant owners: create menu item
  createMenuItem(data: any): Observable<Food> {
    console.log('Creating menu item with data:', data);
    
    // For demo purposes, always simulate successful creation
    console.log('Using mock response for menu item creation');
    
    // Get the localStorage to store the newly created menu item
    return from(import('../mock-models/sample-models')).pipe(
      switchMap(models => {
        // Create a new menu item with a unique ID
        const newItem: Food = {
          id: Math.floor(Math.random() * 1000) + 10,
          name: data.name,
          description: data.description,
          price: data.price,
          imageUrl: data.imageUrl || 'https://example.com/default-food.jpg',
          isAvailable: data.isAvailable || true,
          categoryName: data.categoryName,
          preparationTime: data.preparationTime || 20,
          ingredients: data.ingredients || [],
          restaurantId: data.restaurantId
        };
        
        // Store locally in session storage for the current session
        // This allows us to "remember" created items during the current browser session
        try {
          const storedItems = sessionStorage.getItem('createdMenuItems');
          const createdMenuItems = storedItems ? JSON.parse(storedItems) : [];
          createdMenuItems.push(newItem);
          sessionStorage.setItem('createdMenuItems', JSON.stringify(createdMenuItems));
          console.log('Stored new menu item in session storage');
        } catch (e) {
          console.error('Error storing menu item in session storage:', e);
        }
        
        // Simulate API delay
        return of(newItem).pipe(delay(800));
      })
    );
  }

  // For restaurant owners: update menu item
  updateMenuItem(foodId: string, data: any): Observable<Food> {
    return this.http.put<Food>(`${this.apiUrl}/api/admin/food/${foodId}`, data, {
      headers: this.getHeaders()
    }).pipe(
      catchError(error => {
        console.error(`Error updating menu item ${foodId}:`, error);
        throw error;
      })
    );
  }

  // For restaurant owners: delete menu item
  deleteMenuItem(foodId: string): Observable<any> {
    return this.http.delete(`${this.apiUrl}/api/admin/food/${foodId}`, {
      headers: this.getHeaders()
    }).pipe(
      catchError(error => {
        console.error(`Error deleting menu item ${foodId}:`, error);
        throw error;
      })
    );
  }

  // For restaurant owners: toggle food item availability
  toggleFoodAvailability(foodId: string): Observable<Food> {
    return this.http.put<Food>(`${this.apiUrl}/api/admin/food/${foodId}`, {}, {
      headers: this.getHeaders()
    }).pipe(
      catchError(error => {
        console.error(`Error toggling food availability ${foodId}:`, error);
        throw error;
      })
    );
  }
  
  // Method to delete a restaurant (for admins)
  deleteRestaurant(restaurantId: string): Observable<any> {
    return this.http.delete(`${this.apiUrl}/api/admin/restaurants/${restaurantId}`, {
      headers: this.getHeaders()
    }).pipe(
      catchError(error => {
        console.error(`Error deleting restaurant ${restaurantId}:`, error);
        throw error;
      })
    );
  }
  
  // Method to create a restaurant (for admins)
  createRestaurant(restaurantData: any): Observable<Restaurant> {
    console.log('Creating restaurant with data:', restaurantData);
    
    // First try to create via API
    return this.http.post<Restaurant>(`${this.apiUrl}/api/admin/restaurants`, restaurantData, {
      headers: this.getHeaders()
    }).pipe(
      map(response => {
        console.log('Restaurant created successfully via API:', response);
        
        // Format response to match Restaurant interface if needed
        const formattedRestaurant: Restaurant = this.formatRestaurantResponse(response);
        
        // Store the created restaurant in session storage
        this.storeCreatedRestaurant(formattedRestaurant);
        
        return formattedRestaurant;
      }),
      catchError(error => {
        console.error('Error creating restaurant via API:', error);
        
        // Fallback to mock implementation for demo purposes
        console.log('Using mock implementation for restaurant creation');
        
        // Create a new restaurant with mock data
        const newRestaurant: Restaurant = {
          id: Math.floor(Math.random() * 1000 + 100).toString(),
          name: restaurantData.restaurantName,
          imgUrl: restaurantData.imageUrl || 'Gourmet Restaurant.jpg',
          address: restaurantData.restaurantAddress?.street || 'Default Address',
          rating: 5.0,
          foodType: restaurantData.type || 'Various',
          timeDelivery: restaurantData.timeDelivery || '30-45 min',
          menu: []
        };
        
        // Store locally
        this.storeCreatedRestaurant(newRestaurant);
        
        // Return success
        return of(newRestaurant).pipe(delay(800));
      })
    );
  }
  
  // Helper to format restaurant data from API to match our interface
  private formatRestaurantResponse(response: any): Restaurant {
    // Check if response is already in the right format
    if (response.id && response.name && typeof response.id === 'string') {
      return response as Restaurant;
    }
    
    // Create a properly formatted restaurant object from the response
    // Handle potential property name differences
    const restaurant: Restaurant = {
      id: response.id ? response.id.toString() : '',  // Convert number to string if needed
      name: response.name || response.restaurantName || '',
      imgUrl: response.imgUrl || response.imageUrl || response.images || 'Gourmet Restaurant.jpg',
      address: response.address || (response.restaurantAddress?.street || 'Unknown address'),
      rating: response.rating || 5.0,
      foodType: response.foodType || response.type || 'Various',
      timeDelivery: response.timeDelivery || response.deliveryTime || '30-45 min',
      menu: response.menu || []
    };
    
    console.log('Formatted restaurant:', restaurant);
    return restaurant;
  }
  
  // Helper function to safely access session storage
  private safelyGetFromStorage(key: string): any {
    try {
      // Check if we're in a browser environment
      if (typeof window !== 'undefined' && window.sessionStorage) {
        const storedData = window.sessionStorage.getItem(key);
        return storedData ? JSON.parse(storedData) : null;
      }
      return null;
    } catch (e) {
      console.error(`Error accessing session storage for key ${key}:`, e);
      return null;
    }
  }
  
  // Helper function to safely set session storage
  private safelySetInStorage(key: string, data: any): boolean {
    try {
      // Check if we're in a browser environment
      if (typeof window !== 'undefined' && window.sessionStorage) {
        window.sessionStorage.setItem(key, JSON.stringify(data));
        return true;
      }
      return false;
    } catch (e) {
      console.error(`Error setting session storage for key ${key}:`, e);
      return false;
    }
  }
  
  // Get restaurants in offline mode
  private getOfflineRestaurants(): Observable<Restaurant[]> {
    console.log('Fetching offline restaurants');
    
    // First check session storage
    const storedRestaurants = this.safelyGetFromStorage('createdRestaurants');
    if (storedRestaurants && storedRestaurants.length > 0) {
      console.log(`Found ${storedRestaurants.length} restaurants in session storage`);
      return of(storedRestaurants);
    }
    
    // If no stored restaurants, use mock data
    return from(import('../mock-models/sample-models')).pipe(
      map(models => {
        const mockData = models.mockRestaurants;
        console.log('Using mock restaurants data:', mockData);
        return mockData;
      }),
      catchError(err => {
        console.error('Error loading mock data:', err);
        return of([]);
      })
    );
  }
  
  // Helper method to store created restaurant in session storage
  private storeCreatedRestaurant(restaurant: Restaurant): void {
    // Get any previously stored restaurants
    const createdRestaurants = this.safelyGetFromStorage('createdRestaurants') || [];
    
    // Add the new restaurant
    createdRestaurants.push(restaurant);
    
    // Store back in session storage
    if (this.safelySetInStorage('createdRestaurants', createdRestaurants)) {
      console.log('Stored new restaurant in session storage:', restaurant);
    }
  }
  
  // Get user's favorite restaurants
  getUserFavorites(): Observable<Restaurant[]> {
    // Use the profile endpoint and extract favorites
    console.log('Getting user favorites from profile');
    return this.http.get<any>(`${this.apiUrl}/api/users/profile`, {
      headers: this.getHeaders()
    }).pipe(
      map(profile => {
        console.log('Raw profile response:', profile);
        
        // Extract favorites from profile using various possible property names
        let favorites = [];
        
        // Try different possible property names
        if (profile?.favorites && Array.isArray(profile.favorites)) {
          favorites = profile.favorites;
        } else if (profile?.favourites && Array.isArray(profile.favourites)) {
          favorites = profile.favourites;
        } else if (profile?.user?.favorites && Array.isArray(profile.user.favorites)) {
          favorites = profile.user.favorites;
        } else if (profile?.user?.favourites && Array.isArray(profile.user.favourites)) {
          favorites = profile.user.favourites;
        } else {
          console.warn('No favorites array found in profile response');
          favorites = [];
        }
        
        console.log('Extracted favorites from profile:', favorites);
        
        // If we have favorite IDs but not full restaurant objects, fetch the full details
        if (favorites.length > 0) {
          if (typeof favorites[0] !== 'object') {
            // We have an array of IDs, fetch full details for each
            console.log('Favorites are IDs, fetching full restaurant details');
            return this.fetchFavoriteRestaurantsDetails(favorites);
          } else {
            // We have objects but need to ensure they match our Restaurant interface
            console.log('Favorites are objects, formatting to match Restaurant interface');
            const formattedFavorites = favorites.map((fav: any) => this.formatRestaurantResponse(fav));
            
            // Store in session storage
            this.safelySetInStorage('userFavorites', formattedFavorites);
            
            return of(formattedFavorites);
          }
        }
        
        return of([]);
      }),
      switchMap(favorites => {
        // If favorites is already an Observable of Restaurant objects, return it
        return favorites;
      }),
      catchError(error => {
        console.error('Error fetching user favorites:', error);
        // Try to get favorites from session storage as fallback
        const storedFavorites = this.safelyGetFromStorage('userFavorites');
        if (storedFavorites) {
          console.log('Using favorites from session storage:', storedFavorites);
          return of(storedFavorites);
        }
        return of([]);
      })
    );
  }
  
  // Helper method to fetch full restaurant details for favorites
  private fetchFavoriteRestaurantsDetails(favoriteIds: any[]): Observable<Restaurant[]> {
    console.log('Fetching details for favorite IDs:', favoriteIds);
    
    if (!favoriteIds || favoriteIds.length === 0) {
      return of([]);
    }
    
    // Convert any non-string IDs to strings for consistent comparison
    const stringIds = favoriteIds.map(id => id.toString());
    
    // First try to get all restaurants
    return this.getAllRestaurants().pipe(
      map(allRestaurants => {
        console.log('Filtering all restaurants to find favorites');
        // Filter to only favorited restaurants
        const favorites = allRestaurants.filter(restaurant => 
          stringIds.includes(restaurant.id.toString())
        );
        
        console.log(`Found ${favorites.length} favorite restaurants from all restaurants`);
        
        // Store in session storage for offline access
        this.safelySetInStorage('userFavorites', favorites);
        
        return favorites;
      }),
      catchError(error => {
        console.error('Error fetching favorite restaurant details:', error);
        return of([]);
      })
    );
  }
}