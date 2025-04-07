import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../environments/environment.development';
import { Item } from './types/items';

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  constructor(private http: HttpClient) { }

  getItems(){
    const {apiUrl} = environment;
     return this.http.get<Item[]>(`${apiUrl}/Resturant`)
  }
}
