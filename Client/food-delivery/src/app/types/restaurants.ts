export interface Restaurant {
    restaurantID: number;
    restaurantName: string;
    type: string;
    images?: string[];
    open: boolean;
    restaurant?: {
        userID: number;
        name: string;
        email: string;
        phone_number: string;
    };
    restaurantAddress?: {
        id: number;
    };
    contactInfo?: {
        email: string;
        phone: string;
        socialMedia: string;
    };
    openingHours: string;
    closingHours: string;
    // Add missing properties used in the template
    rating?: string;
    address?: string;
}