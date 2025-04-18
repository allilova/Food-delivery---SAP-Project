import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../environments/environment.development';
import { Restaurant } from './types/restaurants';


@Injectable({
  providedIn: 'root'
})
export class ApiService {

  constructor(private http: HttpClient) { }

  getRestaurants(){
    const {apiUrl} = environment;
     return this.http.get<Restaurant[]>(`${apiUrl}/Restaurant`);
  }

  getMenu(id:string){
    const {apiUrl} = environment;
    return this.http.get<Restaurant>(`${apiUrl}/Restaurant/${id}`);
  }
}
