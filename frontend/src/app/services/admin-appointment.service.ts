import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AdminAppointmentService {
  private apiUrl = 'http://localhost:8080/api/admin/appointments';

  constructor(private http: HttpClient) {}

  private getHeaders() {
    const token = localStorage.getItem('token');
    return { headers: new HttpHeaders().set('Authorization', `Bearer ${token}`) };
  }

  getAppointments(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/all`, this.getHeaders());
  }

  createAppointment(appointment: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/create`, appointment, this.getHeaders());
  }

  updateAppointment(id: number, appointment: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/update/${id}`, appointment, this.getHeaders());
  }

  deleteAppointment(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/delete/${id}`, this.getHeaders());
  }
}
