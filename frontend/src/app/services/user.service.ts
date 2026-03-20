import { Inject, Injectable, PLATFORM_ID } from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { isPlatformBrowser } from '@angular/common';
// Ensure this path is correct for your project
import { UserDTO } from '../models/dtos.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private baseUrl = 'http://localhost:8080/api';

  constructor(
    private http: HttpClient,
    @Inject(PLATFORM_ID) private platformId: Object // <--- FIXED SYNTAX HERE
  ) {}

  // ==========================================
  // BASIC USER METHODS
  // ==========================================

  getUserById(id: number | string): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/users/${id}`);
  }

  getUserDetailsAdmin(id: number): Observable<UserDTO> {
    return this.http.get<UserDTO>(`${this.baseUrl}/admin/user-details/${id}`);
  }

  addUserAdmin(userDto: UserDTO): Observable<any> {
    return this.http.post(`${this.baseUrl}/admin/add-user`, userDto);
  }

  // ==========================================
  // HOME & SEARCH METHODS
  // ==========================================

  getAllSkills(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/search/category/all`);
  }

  searchUsers(query: string): Observable<any[]> {
    return this.http.post<any[]>(`${this.baseUrl}/search`, { info: query });
  }

  // ==========================================
  // PROFILE & REVIEWS METHODS
  // ==========================================



  addReview(userId: number | string, content: string): Observable<any> {
    return this.http.post(`${this.baseUrl}/users/${userId}/reviews`, { content });
  }

  // ==========================================
  // BLOCKING & INTERACTION METHODS
  // ==========================================

  blockUser(sourceUsername: string, targetUsername: string): Observable<any> {
    return this.http.post(`${this.baseUrl}/users/block/${sourceUsername}/${targetUsername}`, {});
  }

  unblockUser(sourceUsername: string, targetUsername: string): Observable<any> {
    return this.http.post(`${this.baseUrl}/users/unblock/${sourceUsername}/${targetUsername}`, {});
  }

  // ==========================================
  // ADMIN METHODS (The Fix for 401 Error)
  // ==========================================

  getAllUsers() {
    // 1. Взимаме токена от LocalStorage

    return this.http.get<any[]>('http://localhost:8080/api/users/admin/all-users');
  }

  allUsers():Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/users/all-users`);
  }
  updateUser(id: number, userData: any): Observable<any> {
    return this.http.put(`${this.baseUrl}/users/admin/edit-user/${id}`, userData);
  }

  deleteUser(id: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/users/admin/remove-user/${id}`);
  }

  getUserByUsername(username: string): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/users/username/${username}`);
  }

}
