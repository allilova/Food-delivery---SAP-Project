import { USER_ROLE } from './user-role.enum';

export interface User {
    id: number;
    name: string;
    email: string;
    phoneNumber: string;
    address: string;
    role: USER_ROLE;
}