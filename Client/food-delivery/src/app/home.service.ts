import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../environments/environment.development';
import { Observable } from 'rxjs';

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
    return this.http.get<HomeData>(`${apiUrl}/api/home`);
  }
}