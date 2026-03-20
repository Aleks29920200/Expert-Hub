import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { SearchService } from '../services/search.service';
import { UserDTO } from '../models/dtos.model';
import {PaymentService} from '../services/payment.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  template: `
    
    <div class="min-h-screen bg-gray-50 pb-10">

      <div class="bg-indigo-600 text-white py-16 px-4 text-center shadow-md">
        <h1 class="text-4xl font-bold mb-4">Find the Perfect Expert</h1>
        <div class="max-w-xl mx-auto flex gap-2">
          <input type="text" [(ngModel)]="searchQuery" (keyup.enter)="performSearch(searchQuery)"
                 placeholder="Search by name or skill..." class="flex-grow p-3 rounded-lg text-gray-800 focus:outline-none focus:ring-2 focus:ring-yellow-400">
          <button (click)="performSearch(searchQuery)" class="bg-yellow-400 text-indigo-900 font-bold py-3 px-6 rounded-lg hover:bg-yellow-500 transition">
            Search
          </button>
        </div>

        <div class="mt-8 max-w-5xl mx-auto">
          <p class="text-xs font-semibold opacity-80 mb-4 text-indigo-200 uppercase tracking-widest">Browse by Category</p>
          <div class="flex flex-wrap justify-center gap-2">
            <button (click)="performCategorySearch('it')" class="px-3 py-1 bg-white/10 hover:bg-white/30 text-white rounded-full text-sm backdrop-blur-sm transition-colors border border-white/20">IT</button>
            <button (click)="performCategorySearch('medicine')" class="px-3 py-1 bg-white/10 hover:bg-white/30 text-white rounded-full text-sm backdrop-blur-sm transition-colors border border-white/20">Medicine</button>
            <button (click)="performCategorySearch('archaeology')" class="px-3 py-1 bg-white/10 hover:bg-white/30 text-white rounded-full text-sm backdrop-blur-sm transition-colors border border-white/20">Archaeology</button>
            <button (click)="performCategorySearch('engineering')" class="px-3 py-1 bg-white/10 hover:bg-white/30 text-white rounded-full text-sm backdrop-blur-sm transition-colors border border-white/20">Engineering</button>
            <button (click)="performCategorySearch('education')" class="px-3 py-1 bg-white/10 hover:bg-white/30 text-white rounded-full text-sm backdrop-blur-sm transition-colors border border-white/20">Education</button>
            <button (click)="performCategorySearch('law')" class="px-3 py-1 bg-white/10 hover:bg-white/30 text-white rounded-full text-sm backdrop-blur-sm transition-colors border border-white/20">Law</button>
            <button (click)="performCategorySearch('art')" class="px-3 py-1 bg-white/10 hover:bg-white/30 text-white rounded-full text-sm backdrop-blur-sm transition-colors border border-white/20">Art</button>
            <button (click)="performCategorySearch('science')" class="px-3 py-1 bg-white/10 hover:bg-white/30 text-white rounded-full text-sm backdrop-blur-sm transition-colors border border-white/20">Science</button>
            <button (click)="performCategorySearch('business')" class="px-3 py-1 bg-white/10 hover:bg-white/30 text-white rounded-full text-sm backdrop-blur-sm transition-colors border border-white/20">Business</button>
            <button (click)="performCategorySearch('finance')" class="px-3 py-1 bg-white/10 hover:bg-white/30 text-white rounded-full text-sm backdrop-blur-sm transition-colors border border-white/20">Finance</button>
            <button (click)="performCategorySearch('mathematics')" class="px-3 py-1 bg-white/10 hover:bg-white/30 text-white rounded-full text-sm backdrop-blur-sm transition-colors border border-white/20">Mathematics</button>
            <button (click)="performCategorySearch('psychology')" class="px-3 py-1 bg-white/10 hover:bg-white/30 text-white rounded-full text-sm backdrop-blur-sm transition-colors border border-white/20">Psychology</button>
            <button (click)="performCategorySearch('architecture')" class="px-3 py-1 bg-white/10 hover:bg-white/30 text-white rounded-full text-sm backdrop-blur-sm transition-colors border border-white/20">Architecture</button>
            <button (click)="performCategorySearch('agriculture')" class="px-3 py-1 bg-white/10 hover:bg-white/30 text-white rounded-full text-sm backdrop-blur-sm transition-colors border border-white/20">Agriculture</button>
            <button (click)="performCategorySearch('sports')" class="px-3 py-1 bg-white/10 hover:bg-white/30 text-white rounded-full text-sm backdrop-blur-sm transition-colors border border-white/20">Sports</button>
            <button (click)="performCategorySearch('media')" class="px-3 py-1 bg-white/10 hover:bg-white/30 text-white rounded-full text-sm backdrop-blur-sm transition-colors border border-white/20">Media</button>
            <button (click)="performCategorySearch('music')" class="px-3 py-1 bg-white/10 hover:bg-white/30 text-white rounded-full text-sm backdrop-blur-sm transition-colors border border-white/20">Music</button>
            <button (click)="performCategorySearch('philosophy')" class="px-3 py-1 bg-white/10 hover:bg-white/30 text-white rounded-full text-sm backdrop-blur-sm transition-colors border border-white/20">Philosophy</button>
            <button (click)="performCategorySearch('history')" class="px-3 py-1 bg-white/10 hover:bg-white/30 text-white rounded-full text-sm backdrop-blur-sm transition-colors border border-white/20">History</button>
            <button (click)="performCategorySearch('linguistics')" class="px-3 py-1 bg-white/10 hover:bg-white/30 text-white rounded-full text-sm backdrop-blur-sm transition-colors border border-white/20">Linguistics</button>
            <button (click)="performCategorySearch('environment')" class="px-3 py-1 bg-white/10 hover:bg-white/30 text-white rounded-full text-sm backdrop-blur-sm transition-colors border border-white/20">Environment</button>
            <button (click)="performCategorySearch('astronomy')" class="px-3 py-1 bg-white/10 hover:bg-white/30 text-white rounded-full text-sm backdrop-blur-sm transition-colors border border-white/20">Astronomy</button>
            <button (click)="performCategorySearch('geology')" class="px-3 py-1 bg-white/10 hover:bg-white/30 text-white rounded-full text-sm backdrop-blur-sm transition-colors border border-white/20">Geology</button>
            <button (click)="performCategorySearch('biology')" class="px-3 py-1 bg-white/10 hover:bg-white/30 text-white rounded-full text-sm backdrop-blur-sm transition-colors border border-white/20">Biology</button>
            <button (click)="performCategorySearch('chemistry')" class="px-3 py-1 bg-white/10 hover:bg-white/30 text-white rounded-full text-sm backdrop-blur-sm transition-colors border border-white/20">Chemistry</button>
            <button (click)="performCategorySearch('physics')" class="px-3 py-1 bg-white/10 hover:bg-white/30 text-white rounded-full text-sm backdrop-blur-sm transition-colors border border-white/20">Physics</button>
            <button (click)="performCategorySearch('economics')" class="px-3 py-1 bg-white/10 hover:bg-white/30 text-white rounded-full text-sm backdrop-blur-sm transition-colors border border-white/20">Economics</button>
            <button (click)="performCategorySearch('sociology')" class="px-3 py-1 bg-white/10 hover:bg-white/30 text-white rounded-full text-sm backdrop-blur-sm transition-colors border border-white/20">Sociology</button>
            <button (click)="performCategorySearch('politics')" class="px-3 py-1 bg-white/10 hover:bg-white/30 text-white rounded-full text-sm backdrop-blur-sm transition-colors border border-white/20">Politics</button>
            <button (click)="performCategorySearch('transportation')" class="px-3 py-1 bg-white/10 hover:bg-white/30 text-white rounded-full text-sm backdrop-blur-sm transition-colors border border-white/20">Transportation</button>
          </div>
        </div>
      </div>

      <div class="container mx-auto px-4 mt-8">

        <div *ngIf="isLoading" class="text-center py-12">
          <div class="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-600"></div>
          <p class="mt-2 text-gray-500">Finding experts...</p>
        </div>

        <div *ngIf="!isLoading && users.length === 0" class="text-center py-12">
          <p class="text-gray-500">No experts found.</p>
        </div>

        <div *ngIf="!isLoading && users.length > 0" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <div *ngFor="let user of users" class="bg-white rounded-xl shadow-lg overflow-hidden border border-gray-100 flex flex-col hover:shadow-2xl transition duration-300">

            <div class="h-48 bg-gray-200 relative">
              <img [src]="getImageUrl(user.photoUrl)"
                   class="w-full h-full object-cover cursor-pointer"
                   [routerLink]="['/profile', user.username]"
                   alt="User Photo">

              <div *ngIf="user.role && user.role.length > 0"
                   class="absolute top-0 right-0 bg-indigo-600/90 backdrop-blur-sm text-white text-xs font-bold px-3 py-1 m-2 rounded-full shadow">
                {{ getRoleName(user) }}
              </div>
            </div>

            <div class="p-6 flex-grow">
              <h3 class="text-xl font-bold text-gray-800 cursor-pointer hover:text-indigo-600"
                  [routerLink]="['/profile', user.id]">
                {{ user.firstName }} {{ user.lastName }}
              </h3>
              <p class="text-indigo-500 font-medium text-sm mb-2">{{ user.username }}</p>

              <div class="mt-3 flex flex-wrap gap-2">
                <ng-container *ngIf="user.createdBy && user.createdBy.length > 0">
               <span *ngFor="let skill of user.createdBy" class="text-[10px] uppercase font-bold text-white px-2 py-1 rounded bg-indigo-400">
                 {{ skill.category }}
               </span>
                </ng-container>
              </div>
            </div>

            <ng-container *ngIf="!user.hasPaid">
              <button (click)="payForAccess(user)"
                      class="bg-green-600 hover:bg-green-700 text-white font-bold py-2 px-4 rounded w-full transition">
                <i class="fas fa-lock me-2"></i> Pay to Unlock Contact
              </button>
            </ng-container>

            <ng-container *ngIf="user.hasPaid">
              <div class="flex justify-center gap-2">
                <a [routerLink]="['/profile', user.username]"
                   class="bg-indigo-600 hover:bg-indigo-700 text-white font-semibold py-2 px-4 rounded transition">
                  <i class="fas fa-user"></i> Profile
                </a>

                <button (click)="goToChat(user.username)"
                        class="bg-yellow-400 hover:bg-yellow-500 text-indigo-900 font-bold py-2 px-4 rounded transition">
                  <i class="fas fa-comment"></i> Message
                </button>
              </div>
            </ng-container>

          </div>
        </div>
      </div>
    </div>
  `
})
export class HomeComponent implements OnInit {
  searchQuery: string = '';
  users: UserDTO[] = [];
  isLoading: boolean = false;

  constructor(
    private searchService: SearchService,
    private cdr: ChangeDetectorRef,
    private sanitizer: DomSanitizer,
    private router: Router,
  private paymentService: PaymentService
  ) {}

  ngOnInit() {
    this.performSearch(''); // Търсим с празен стринг, за да върне всички!
  }
  payForAccess(expert: any) {
    // 1. Взимаме ID-то на текущо логнатия потребител (купувача)
    let myUserId = 1; // Хардкодната стойност за тест (ако няма никой логнат)

    const currentUserStr = localStorage.getItem('currentUser');
    if (currentUserStr) {
      try {
        const currentUserObj = JSON.parse(currentUserStr);
        // Предполагаме, че обектът в localStorage има поле id
        if (currentUserObj && currentUserObj.id) {
          myUserId = currentUserObj.id;
        }
      } catch (e) {
        console.warn('Не можах да прочета логнатия потребител от localStorage', e);
      }
    }

    // 2. Взимаме ID-то на офертата, която ще се плаща
    // Ако в DTO-то няма defaultOfferId, използваме 1 за тест
    const offerIdToPay = expert.defaultOfferId || 1;

    console.log(`Иницииране на плащане: Купувач ID=${myUserId}, Оферта ID=${offerIdToPay}`);

    // 3. Извикваме PaymentService
    // ВНИМАНИЕ: Увери се, че методът в payment.service.ts приема (userId, offerId)
    this.paymentService.createHostedCheckoutSession(myUserId, offerIdToPay).subscribe({
      next: (response: any) => {
        // Очакваме бекендът да върне JSON: { "url": "https://checkout.stripe.com/..." }
        if (response && response.url) {
          console.log('Пренасочване към Stripe...');
          window.location.href = response.url; // Това отваря страницата на Stripe!
        } else {
          console.error('Бекендът не върна URL за плащане:', response);
          alert('Възникна грешка при създаването на сесия за плащане.');
        }
      },
      error: (err) => {
        console.error('Грешка при връзката със сървъра за плащане:', err);
        alert('Неуспешна връзка със сървъра. Моля, опитайте по-късно.');
      }
    });
  }




  // ✅ ROBUST NAVIGATION METHOD
  goToChat(userObj: any) {
    // 1. Prefer Username (since Chat uses usernames anyway)
    const target = userObj.username;

    if (!target) {
      console.error("User object:", userObj);
      alert("Error: Missing username!");
      return;
    }

    // 2. Navigate to /chat/alex
    this.router.navigate(['/chat', target]);
  }

  getImageUrl(photoData: any): SafeUrl {
    if (!photoData) {
      return 'assets/default-user.png';
    }

    if (typeof photoData === 'string') {
      const finalUrl = photoData.startsWith('data:') ? photoData : `data:image/jpeg;base64,${photoData}`;
      return this.sanitizer.bypassSecurityTrustUrl(finalUrl);
    }
    // Fallback за стария формат
    return 'assets/default-user.png';
  }

  // Съществуващият ти метод за търсене по текст
  performSearch(query: string) {
    this.isLoading = true;
    this.searchService.searchByKeyword(query).subscribe({
      next: (data) => {
        this.users = data || [];
        this.isLoading = false;
        if (this.cdr) this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Грешка при търсене:', err);
        this.isLoading = false;
      }
    });
  }

  // НОВ метод за търсене по категория
  performCategorySearch(category: string) {
    this.isLoading = true;
    // Можеш също да обновиш полето за търсене визуално, ако желаеш:
    this.searchQuery = category;

    this.searchService.searchByCategory(category).subscribe({
      next: (data) => {
        this.users = data || [];
        this.isLoading = false;
        if (this.cdr) this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Грешка при търсене по категория:', err);
        this.isLoading = false;
      }
    });
  }

  getRoleName(user: any): string {
    if (user && user.roles && user.roles.length > 0) {
      const role = user.roles[0].name || user.roles[0];
      return role.replace('ROLE_', '').toLowerCase();
    }
    return 'expert';
  }
}
