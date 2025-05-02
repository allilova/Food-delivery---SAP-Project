import { Food } from './food';
export interface OrderItem {
    id: number;
    food: Food;
    quantity: number;
    price: number;
    totalPrice?: number;  // Added this field to match template usage
}

export enum OrderStatus {
    PENDING = 'PENDING',
    CONFIRMED = 'CONFIRMED',
    PREPARING = 'PREPARING',
    READY = 'READY',
    OUT_FOR_DELIVERY = 'OUT_FOR_DELIVERY',
    DELIVERED = 'DELIVERED',
    CANCELLED = 'CANCELLED',
    // For backward compatibility with existing components
    IN_PROGRESS = 'IN_PROGRESS'
}

export interface StatusUpdate {
    status: OrderStatus;
    timestamp: string;
    note?: string;
}

export interface Order {
    id: string;
    items: OrderItem[];
    totalAmount: number;
    status: OrderStatus;
    orderDate: string;
    deliveryAddress: string;
    deliveryTime?: string;
    estimatedDeliveryTime?: string;
    trackingCode?: string;
    statusHistory?: StatusUpdate[];
    restaurant?: {
        id: string;
        name: string;
        phone?: string;
    };
    driver?: {
        id?: string;
        name?: string;
        phone?: string;
        location?: {
            lat: number;
            lng: number;
        };
    };
}