// src/app/mock-models/sample-food.ts
import { Food } from '../types/food';

export const mockFoodItems: Food[] = [
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
    imageUrl: 'assets/pasta-carbanara.jpg',
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
  },
  {
    id: 4,
    name: 'Tiramisu',
    description: 'Classic Italian dessert with layers of coffee-soaked ladyfingers and mascarpone cream',
    price: 7.99,
    imageUrl: 'https://example.com/tiramisu.jpg',
    isAvailable: true,
    categoryName: 'Dessert',
    preparationTime: 5,
    ingredients: ['Ladyfingers', 'Mascarpone', 'Coffee', 'Cocoa']
  }
];