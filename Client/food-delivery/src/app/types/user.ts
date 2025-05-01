import { USER_ROLE } from './user-role.enum';
import { Restaurant } from './restaurants';

export interface User {
    id: number;
    name: string;
    email: string;
    phoneNumber?: string;
    address: string;
    role: USER_ROLE;
    // Make both naming conventions accessible
    favorites?: Restaurant[];
    favourites?: Restaurant[]; 
}