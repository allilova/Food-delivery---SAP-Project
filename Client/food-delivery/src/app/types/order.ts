import { Food } from './food';
export interface OrderItem {
    id: number;
    food: Food;
    quantity: number;
    price: number;
}

export enum OrderStatus {
    PENDING = 'PENDING',
    IN_PROGRESS = 'IN_PROGRESS',
    DELIVERED = 'DELIVERED',
    CANCELLED = 'CANCELLED'
}

export interface Order {
    id: number;
    items: OrderItem[];
    totalAmount: number;
    orderStatus: OrderStatus;
    orderDate: string;
    deliveryAddress: string;
}