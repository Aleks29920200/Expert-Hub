import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import { Observable } from 'rxjs';
import { UserDTO } from '../models/dtos.model'; // Ensure this path is correct

@Injectable({
  providedIn: 'root'
})
export class SearchService {
  // Make sure this matches your Controller's @RequestMapping ("api/search" or similar)
  private baseUrl = 'http://localhost:8080/api/search';

  constructor(private http: HttpClient) {}




  searchByKeyword(query: string): Observable<UserDTO[]> {
    // Създава заявка от типа: GET http://localhost:8080/api/search?query=Думата
    let params = new HttpParams().set('query', query);
    return this.http.get<UserDTO[]>(this.baseUrl, { params });
  }
  searchByCategory(category: string): Observable<UserDTO[]> {
    return this.http.get<UserDTO[]>(`${this.baseUrl}/category/${category}`);
  }

}
