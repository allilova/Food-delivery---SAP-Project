
import { Restaurant } from '../types/restaurants';
import { Food } from '../types/food';
import { Order, OrderStatus } from '../types/order';

export const mockRestaurants: Restaurant[] = [
  {
    id: '1',
    name: 'Test Restaurant',
    address: '123 Main St, Test City',
    imgUrl: 'https://example.com/test-restaurant.jpg',
    foodType: 'Italian',
    timeDelivery: '30-45 min',
    rating: 4.5,
    menu: []
  },
  {
    id: '2',
    name: 'Asian Fusion',
    address: '456 Market St, Tokyo',
    imgUrl: 'https://example.com/asian-restaurant.jpg',
    foodType: 'Asian',
    timeDelivery: '25-40 min',
    rating: 4.8,
    menu: []
  }
];

// Convert the format to match the Food interface
export const mockMenuItems: Food[] = [
  {
    id: 1,
    name: 'Margherita Pizza',
    description: 'Classic pizza with tomato sauce, mozzarella, and basil',
    price: 12.99,
    imageUrl: 'https://example.com/margherita-pizza.jpg',
    isAvailable: true,
    categoryName: 'Pizza',
    preparationTime: 20,
    ingredients: ['Tomato Sauce', 'Mozzarella', 'Basil', 'Olive Oil']
  },
  {
    id: 2,
    name: 'Pasta Carbonara',
    description: 'Creamy pasta with eggs, cheese, pancetta, and black pepper',
    price: 14.99,
    imageUrl: 'https://example.com/pasta-carbonara.jpg',
    isAvailable: true,
    categoryName: 'Pasta',
    preparationTime: 15,
    ingredients: ['Spaghetti', 'Eggs', 'Parmesan', 'Pancetta', 'Black Pepper']
  },
  {
    id: 3,
    name: 'Caesar Salad',
    description: 'Fresh romaine lettuce with creamy Caesar dressing, croutons, and parmesan',
    price: 9.99,
    imageUrl: 'https://example.com/caesar-salad.jpg',
    isAvailable: true,
    categoryName: 'Salad',
    preparationTime: 10,
    ingredients: ['Romaine Lettuce', 'Caesar Dressing', 'Croutons', 'Parmesan']
  }
];

// Sample orders using proper types from the Order interface
export const mockOrders: Order[] = [
  {
    id: '1',
    items: [
      {
        id: 1,
        food: mockMenuItems[0], // Reference to Margherita Pizza
        quantity: 2,
        price: 12.99,
        totalPrice: 25.98
      },
      {
        id: 2,
        food: mockMenuItems[1], // Reference to Pasta Carbonara
        quantity: 1,
        price: 14.99,
        totalPrice: 14.99
      }
    ],
    status: OrderStatus.DELIVERED,
    totalAmount: 40.97,
    orderDate: new Date().toISOString(),
    deliveryAddress: '123 Main St, Test City',
    deliveryTime: '40 minutes',
    estimatedDeliveryTime: new Date(Date.now() + 40*60000).toISOString(),
    restaurant: {
      id: '1',
      name: 'Test Restaurant'
    }
  }
];