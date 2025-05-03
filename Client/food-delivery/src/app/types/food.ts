export interface Food {
    id: number;
    name: string;
    description: string;
    price: number;
    imageUrl: string;
    isAvailable: boolean;
    categoryName: string;
    preparationTime: number;
    ingredients: string[];
    restaurantId?: number; // Optional as it may not be present in all responses
}