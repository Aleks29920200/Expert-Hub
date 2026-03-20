import { Inject, Injectable, PLATFORM_ID } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, catchError, Observable, of, tap } from 'rxjs';
import { isPlatformBrowser } from '@angular/common'; // <--- IMPORT THIS

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/auth';

  private currentUserSubject = new BehaviorSubject<string | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(
    private http: HttpClient,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {if (isPlatformBrowser(this.platformId)) {
    const token = localStorage.getItem('token');
    const username = localStorage.getItem('username');

    if (token && username) {
      // Ако намерим токен, веднага казваме на всички, че сме логнати!
      this.currentUserSubject.next(username);
    }
  }
  }

  // --- LOGIN ---
  login(credentials: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/login`, credentials).pipe(
      tap((response: any) => {
        if (isPlatformBrowser(this.platformId)) {
          localStorage.setItem('token', response.token);
          localStorage.setItem('username', response.username);
          localStorage.setItem('role', response.role);
        }
        // ПОПРАВКА 1: Подаваме само името (текст), а не целия обект!
        this.currentUserSubject.next(response.username);
      })
    );
  }

// Помощен метод за проверка на ролята
  getRole(): string | null {
    if (isPlatformBrowser(this.platformId)) {
      return localStorage.getItem('role');
    }
    return null;
  }

  registerExpert(formData: FormData): Observable<any> {
    // ✅ Matches @PostMapping("/register/expert")
    return this.http.post(`${this.apiUrl}/register/expert`, formData);
  }

  // 2. Register Client
  registerClient(formData: FormData): Observable<any> {
    // ✅ Matches @PostMapping("/register/client")
    return this.http.post(`${this.apiUrl}/register/client`, formData);
  }

  // --- LOGOUT ---
  logout(): void {
    console.log("ВНИМАНИЕ: Методът logout() току-що се стартира!");

    if (isPlatformBrowser(this.platformId)) {
      localStorage.removeItem('token');
      localStorage.removeItem('username');
      localStorage.removeItem('role'); // ПОПРАВКА 3: Изтриваме и ролята!
    }
    this.currentUserSubject.next(null);
  }

  // --- HELPER METHODS (Fixes the Crashes) ---

  isLoggedIn(): boolean {
    // FIX: Check platform before touching localStorage
    if (isPlatformBrowser(this.platformId)) {
      const token = localStorage.getItem('token');
      return !!token;
    }
    return false; // Server is never "logged in"
  }

  getUserRole(): string {
    if (isPlatformBrowser(this.platformId)) {
      return localStorage.getItem('role') || 'USER';
    }
    return 'USER'; // Default for server
  }

  isAdmin(): boolean {
    if (isPlatformBrowser(this.platformId)) {

      const role = localStorage.getItem('role');
      return role === 'ADMIN';
    }
    return false;
  }

  getCurrentUsername(): string | null {
    return this.currentUserSubject.value;
  }
  getAllUsersForAdmin(): Observable<any> {
    // The "Double Lock" - Safety inside the service itself
    if (!isPlatformBrowser(this.platformId)) {
      return of([]);
    }
    return this.http.get(this.apiUrl);
  }
  getUserProfile(username: string): Observable<any> {
    // Make sure this matches your backend @GetMapping exactly!
    return this.http.get<any>(`${this.apiUrl}/profile/${username}`);
  }
}
