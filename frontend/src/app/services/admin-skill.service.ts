import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AdminSkillService {
  private apiUrl = 'http://localhost:8080/api/skills/admin';

  constructor(private http: HttpClient) {}

  private getHeaders() {
    const token = localStorage.getItem('token');
    return { headers: new HttpHeaders().set('Authorization', `Bearer ${token}`) };
  }

  getSkills(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/all`, this.getHeaders());
  }

  createSkill(skill: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/create`, skill, this.getHeaders());
  }

  updateSkill(id: number, skill: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/update/${id}`, skill, this.getHeaders());
  }

  deleteSkill(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/delete/${id}`, this.getHeaders());
  }
}
