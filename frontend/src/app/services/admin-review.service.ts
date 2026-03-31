import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AdminReviewService {
  private apiUrl = 'http://localhost:8080/api/admin/reviews';

  constructor(private http: HttpClient) {}

  private getHeaders() {
    const token = localStorage.getItem('token');
    return { headers: new HttpHeaders().set('Authorization', `Bearer ${token}`) };
  }
  getUsers(): Observable<any[]> {
    return this.http.get<any[]>(`http://localhost:8080/api/admin/all`, this.getHeaders());
  }
  getReviews(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/all`, this.getHeaders());
  }

  createReview(review: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/create`, review, this.getHeaders());
  }

  updateReview(id: number, review: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/update/${id}`, review, this.getHeaders());
  }

  deleteReview(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/delete/${id}`, this.getHeaders());
  }
}
