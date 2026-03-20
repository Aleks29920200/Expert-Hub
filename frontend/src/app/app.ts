import { Component, OnInit, Inject, PLATFORM_ID } from '@angular/core'; // <--- 1. Import Inject & PLATFORM_ID
import { isPlatformBrowser } from '@angular/common'; // <--- 2. Import isPlatformBrowser
import { RouterOutlet, RouterLink, RouterLinkActive, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { AuthService } from './services/auth.service';
import { UserService } from './services/user.service';
import {environment} from './environments/environment';
import {AiChatComponent} from './components/ai-chat.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, CommonModule, FormsModule,AiChatComponent],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
  currentUser: string | null = null;
  currentUserId: number | null = null;
  isAdmin: boolean = false;
  isUserMenuOpen: boolean = false;

  isRegisterMenuOpen: boolean = false;
  searchQuery: string = '';

  constructor(
    private authService: AuthService,
    private userService: UserService,
    private router: Router,
    private http: HttpClient,
    @Inject(PLATFORM_ID) private platformId: Object // <--- 3. Inject Platform ID
  ) {}

  ngOnInit() {
    // 4. CHECK IF BROWSER BEFORE TOUCHING LOCALSTORAGE
    if (isPlatformBrowser(this.platformId)) {

      const token = localStorage.getItem('token');
      if (token) {
        this.fetchUserDetails();
      }

    }

    // Subscribe to user changes (Safe to run anywhere)
    this.authService.currentUser$.subscribe(username => {
      this.currentUser = username;
      if (username) {
        // If we have a username, we are likely already in the browser,
        // but it doesn't hurt to be safe inside fetchUserDetails too.
        if (!this.currentUserId) {
          this.fetchUserDetails();
        }
      } else {
        this.isAdmin = false;
        this.currentUserId = null;
      }
    });
  }



  fetchUserDetails() {
    if (!isPlatformBrowser(this.platformId)) return;

    const token = localStorage.getItem('token');
    if (!token) return;

    // Увери се, че адресът е към 8080 (бекенда)
    this.http.get<any>('http://localhost:8080/api/auth/me').subscribe({
      next: (user) => {
        if (user) {
          this.currentUserId = user.id;

          // ---> ДОБАВИ ТОЗИ РЕД <---
          this.currentUser = user.username;

          if (Array.isArray(user.role)) {
            this.isAdmin = user.role.some((r: any) => r.name === 'ADMIN');
          } else {
            this.isAdmin = false;
          }
        }
      },
      error: (err) => console.warn('Auth check failed', err)
    });
  }
  performSearch() {
    if (this.searchQuery && this.searchQuery.trim() !== '') {
      // Пренасочваме към компонента за търсене с въведената дума
      this.router.navigate(['/search', this.searchQuery]);
      this.searchQuery = ''; // Изчистваме полето
    }
  }
  logout(): void {
    console.log("ВНИМАНИЕ: Методът logout() току-що се стартира!"); // <-- ДОБАВИ ТОВА

    if (isPlatformBrowser(this.platformId)) {
      localStorage.removeItem('token');
      localStorage.removeItem('username');
    }

    this.router.navigate(['/']).then(() => {
      window.location.reload(); // Пълно презареждане, за да изчистим състоянието
    });
  }


}
