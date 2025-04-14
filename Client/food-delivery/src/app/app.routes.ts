import { Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { ErrorComponent } from './error/error.component';
import { LoginComponent } from './user/login/login.component';
import { RegisterComponent } from './user/register/register.component';
import { CatalogComponent } from './catalog/catalog.component';
import { ProfileComponent } from './user/profile/profile.component';
import { SearchComponent } from './search/search.component';
import { MenuComponent } from './menu/menu.component';

export const routes: Routes = [
    {path: '', redirectTo: '/home', pathMatch: 'full'},
    {path: 'home', component: HomeComponent},
    {path: 'login', component: LoginComponent},
    {path: 'register', component: RegisterComponent},
    {path: 'catalog', children: [
        {path: '', component: CatalogComponent},
        {path: ':restaurantId', component:MenuComponent}
    ]},
    {path: 'menu', component: MenuComponent},
    {path: 'profile', component: ProfileComponent},
    {path: 'search', component: SearchComponent},
    {path: '404', component: ErrorComponent},
    {path: '**', redirectTo: '/404'}
];
