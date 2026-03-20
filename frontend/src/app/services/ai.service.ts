import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AiService {
  // Промени порта, ако твоят Spring Boot работи на друг
  private apiUrl = 'http://localhost:8080/api/ai';

  constructor(private http: HttpClient) {}

  askAssistant(message: string): Observable<{reply: string}> {
    const isLoggedIn = !!localStorage.getItem('token'); // Проверяваме дали има токен
    const contextualMessage = `[User is logged in: ${isLoggedIn}] ${message}`; // Добавяме го скрито в съобщението

    return this.http.post<{reply: string}>(`${this.apiUrl}/ask`, { message: contextualMessage });
  }
}
