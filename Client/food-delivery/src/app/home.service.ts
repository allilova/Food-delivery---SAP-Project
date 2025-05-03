import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../environments/environment.development';
import { Observable, of, catchError } from 'rxjs';

export interface HomeData {
  title: string;
  mainText: string;
  description: string;
  callToAction: string;
}

@Injectable({
  providedIn: 'root'
})
export class HomeService {

  constructor(private http: HttpClient) { }

  getHomeData(): Observable<HomeData> {
    const { apiUrl } = environment;
    
    // Create fallback mock data
    const mockHomeData: HomeData = {
      title: "Welcome to Food Delivery",
      mainText: "Order delicious food online",
      description: "Discover restaurants, order food, and get it delivered right to your doorstep.",
      callToAction: "Browse Restaurants"
    };
    
    // Try to get data from API with fallback to mock data
    return this.http.get<HomeData>(`${apiUrl}/api/home`).pipe(
      catchError(error => {
        console.log('Using mock home data due to API error:', error);
        return of(mockHomeData);
      })
    );
  }
}