import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';

export interface AppointmentDTO {
  dateOfAppointment: Date;
  name: string;

  requesterUsername: string;
  providerUsername: string;
  skillId: number;
}

@Injectable({
  providedIn: 'root'
})
export class AppointmentService {
  // ПРОМЕНЕНО: Бекендът слуша на /api/appointments
  private apiUrl = `${environment.apiUrl}/appointments`;

  constructor(private http: HttpClient) {}

  addAppointment(appointment: AppointmentDTO): Observable<any> {
    const token = localStorage.getItem('token'); // Взимаш токена
    const headers = { 'Authorization': `Bearer ${token}` };

    // ВАЖНО: 'text' е в единични кавички!
    return this.http.post(`${this.apiUrl}/add`, appointment, {
      headers: headers,
      responseType: 'text'
    });
  }

  removeAppointment(appointmentId: number): Observable<any> {
    const token = localStorage.getItem('token');
    const headers = { 'Authorization': `Bearer ${token}` };

    // ДОБАВЯМЕ responseType: 'text' (в единични кавички!)
    return this.http.delete(`${this.apiUrl}/remove/${appointmentId}`, {
      headers: headers,
      responseType: 'text'
    });
  }

  getAppointments(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/all`);
  }

  getCalendarEvents(): Observable<AppointmentDTO[]> {
    return this.http.get<AppointmentDTO[]>(`${this.apiUrl}/calendar-events`);
  }
}
