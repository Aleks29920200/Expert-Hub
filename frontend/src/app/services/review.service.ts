// src/app/services/review.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import { ReviewDto } from '../models/dtos.model';
import {environment} from '../environments/environment';

@Injectable({ providedIn: 'root' })
export class ReviewService {
  private apiUrl = `${environment.apiUrl}/reviews`;

  constructor(private http: HttpClient) {}

  // Matches: addReview(reviewer, revieweeId, content)
  // targetUsername вече е string!
  addReview(reviewerUsername: string, targetUsername: string, content: string) {
    const payload = {
      reviewerUsername: reviewerUsername,
      targetUsername: targetUsername, // Сменихме targetId с targetUsername
      content: content
    };
    return this.http.post(`${this.apiUrl}/add`, payload);
  }
// В review.service.ts
  getReviewsForUser(username: string): Observable<any[]> {
    // Увери се, че имаш такъв GET ендпойнт в Java бекенда си!
    // Напр: @GetMapping("/user/{username}")
    return this.http.get<any[]>(`${this.apiUrl}/user/${username}`);
  }
// Редактиране на ревю
  // Редактиране
  updateReview(reviewId: number, content: string): Observable<any> {
    return this.http.put(`${this.apiUrl}/${reviewId}`, { content: content });
  }

  // Изтриване
  deleteReview(reviewId: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${reviewId}`);
  }

  // Отговор
  replyToReview(reviewId: number, replyContent: string, authorUsername: string): Observable<any> {
    // Пращаме JSON обект, който Spring Boot ще конвертира в Map<String, String>
    return this.http.post(`${this.apiUrl}/${reviewId}/reply`, {
      content: replyContent,
      authorUsername: authorUsername
    });
  }
}
