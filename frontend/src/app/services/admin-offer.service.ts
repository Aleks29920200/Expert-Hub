import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AdminOfferService {
  private apiUrl = 'http://localhost:8080/api/admin/offers';

  constructor(private http: HttpClient) {}

  private getHeaders() {
    const token = localStorage.getItem('token');
    return { headers: new HttpHeaders().set('Authorization', `Bearer ${token}`) };
  }
// Добави това в admin-offer.service.ts (или в съответния сървис)
  getUsers(): Observable<any[]> {
    return this.http.get<any[]>('http://localhost:8080/api/admin/all', this.getHeaders());
  }
  getOffers(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/all`, this.getHeaders());
  }

  createOffer(offer: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/create`, offer, this.getHeaders());
  }

  updateOffer(id: number, offer: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/update/${id}`, offer, this.getHeaders());
  }

  deleteOffer(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/delete/${id}`, this.getHeaders());
  }
}
