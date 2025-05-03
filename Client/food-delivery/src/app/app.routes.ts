// src/app/app.routes.ts
import { Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { ErrorComponent } from './error/error.component';
import { LoginComponent } from './user/login/login.component';
import { RegisterComponent } from './user/register/register.component';
import { CatalogComponent } from './catalog/catalog.component';
import { ProfileComponent } from './user/profile/profile.component';
import { SearchComponent } from './search/search.component';
import { MenuComponent } from './menu/menu.component';
import { EditMenuComponent } from './edit/edit-menu/edit-menu.component';
import { EditRestaurantComponent } from './edit/edit-restaurant/edit-resturant.component';
import { ShopCartComponent } from './shop-cart/shop-cart.component';
import { PaymentComponent } from './payment/payment.component';
import { SupplierComponent } from './supplier/supplier.component';
import { OrdersComponent } from './supplier/orders/orders.component';
import { RestaurantsComponent } from './restaurants/restaurants.component';
import { OrderTrackingPageComponent } from './order-tracking/order-tracking-page.component';
import{OrderTrackingComponent} from './components/order-tracking/order-tracking.component';
import { CreateRestaurantComponent } from './admin/create-restaurant/create-restaurant.component';
import { CreateMenuItemComponent } from './admin/create-menu-item.component/create-menu-item.component';
import { authGuard, roleGuard, customerOnlyGuard } from './guards/auth.guard';
import { USER_ROLE } from './types/user-role.enum';

export const routes: Routes = [
    {path: '', redirectTo: '/home', pathMatch: 'full'},
    {path: 'home', component: HomeComponent},
    {path: 'login', component: LoginComponent},
    {path: 'register', component: RegisterComponent},
    {
        path: 'catalog', 
        children: [
            {path: '', component: CatalogComponent},
            {path: ':restaurantId', component: MenuComponent}
        ]
    },
    {
        path: 'edit-restaurant/:id', 
        component: EditRestaurantComponent, 
        canActivate: [roleGuard([USER_ROLE.ROLE_ADMIN])]
    },
    {
        path: 'edit-menu/:id', 
        component: EditMenuComponent, 
        canActivate: [roleGuard([USER_ROLE.ROLE_RESTAURANT, USER_ROLE.ROLE_ADMIN])]
    },
    {
        path: 'create-menu', 
        component: CreateMenuItemComponent, 
        canActivate: [roleGuard([USER_ROLE.ROLE_RESTAURANT, USER_ROLE.ROLE_ADMIN])]
    },
    {
        path: 'profile', 
        component: ProfileComponent, 
        canActivate: [authGuard]
    },
    {
        path: 'shopCart', 
        component: ShopCartComponent, 
        canActivate: [customerOnlyGuard] // Only customers can access cart
    },
    {
        path: 'payment', 
        component: PaymentComponent, 
        canActivate: [customerOnlyGuard] // Only customers can access payment
    },
    {
        path: 'track-order', 
        component: OrderTrackingPageComponent
    },
    {
        path: 'track-order/:id', 
        component: OrderTrackingPageComponent
    },
    {
        path: 'search', 
        component: SearchComponent, 
        canActivate: [customerOnlyGuard] // Only customers can access search
    },
    {
        path: 'supplier', 
        canActivate: [roleGuard([USER_ROLE.ROLE_DRIVER])],
        children: [
            {path: '', component: SupplierComponent},
            {path: 'orders', component: OrdersComponent},
            {path: 'orders/:id', component: OrdersComponent}
        ]
    },
    {
        path: 'restaurants', 
        component: RestaurantsComponent, 
        canActivate: [roleGuard([USER_ROLE.ROLE_RESTAURANT])] // Only restaurant owners can access this route
    },
    {
        path: 'admin/create-restaurant',
        component: CreateRestaurantComponent,
        canActivate: [roleGuard([USER_ROLE.ROLE_ADMIN])] // Only admin can create restaurants
    },
    {
        path: 'admin/create-menu-item/:restaurantId',
        component: CreateMenuItemComponent,
        canActivate: [roleGuard([USER_ROLE.ROLE_ADMIN, USER_ROLE.ROLE_RESTAURANT])] // Admin or restaurant owner can create menu items
    },
    {path: 'tracking', component: OrderTrackingComponent},
    {path: '404', component: ErrorComponent},
    {path: '**', redirectTo: '/404'}
];