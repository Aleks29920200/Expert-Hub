
import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { UserProfile } from '../models/user-profile.dto';
import { UserService } from '../services/user.service';
import { AuthService } from '../services/auth.service';
import {ReviewService} from '../services/review.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  styles: [
    `.last-child-border-0:last-child { border-bottom: 0 !important; }
    .card { transition: transform 0.2s ease-in-out; }
    .review-item p { font-size: 0.95rem; line-height: 1.6; }
    .bg-primary { background: linear-gradient(135deg, #4e73df 0%, #224abe 100%) !important; }
    .badge { font-weight: 500; letter-spacing: 0.3px; }`
  ],
  imports: [CommonModule, RouterLink, FormsModule],
  // ТВОЯТ HTML ТЕМПЛЕЙТ ОСТАВА ТУК (не го трия, за да не става огромен кодът)
  template: `
    <div class="profile-page bg-light min-vh-100" *ngIf="userProfile">
      <div class="bg-primary text-white py-5 mb-4 shadow">
        <div class="container">
          <div class="row align-items-center">
            <div class="col-md-3 text-center text-md-start">
              <div class="position-relative d-inline-block">
                <img [src]="profileImageSrc"
                     alt="Profile Photo"
                     class="w-full rounded-lg object-cover h-48 border border-gray-300 shadow-sm">
                <span
                  class="position-absolute bottom-0 end-0 p-2 bg-success border border-2 border-white rounded-circle"
                  title="Online Status"></span>
              </div>
            </div>
            <div class="col-md-6 text-center text-md-start mt-3 mt-md-0">
              <h1 class="fw-bold mb-1">{{ userProfile?.firstName }} {{ userProfile?.lastName }}</h1>
              <p class="fs-5 opacity-75">{{ userProfile?.username }}</p>
              <div class="d-flex flex-wrap justify-content-center justify-content-md-start gap-2 mt-2">
            <span class="badge rounded-pill bg-warning text-dark px-3 py-2">
              <i class="fas fa-certificate me-1"></i> {{ userProfile?.skill?.category || 'Community Member' }}
            </span>
                <span class="badge rounded-pill bg-info text-dark px-3 py-2" *ngIf="userProfile?.reviews">
              <i class="fas fa-star me-1"></i> {{ userProfile.reviews?.length || 0 }} Reviews
            </span>
              </div>
            </div>
            <div class="col-md-3 text-center text-md-end mt-4 mt-md-0">
              <button *ngIf="userProfile?.username !== currentUser"
                      [routerLink]="['/chat', userProfile?.username]"
                      class="btn btn-light btn-lg px-4 shadow-sm fw-bold">
                <i class="fas fa-paper-plane me-2"></i> Message
              </button>
            </div>
          </div>
        </div>
      </div>

      <div class="container pb-5">
        <div class="row">
          <div class="col-lg-4">
            <div class="card border-0 shadow-sm mb-4">
              <div class="card-body">
                <h5 class="card-title fw-bold mb-3">Quick Info</h5>
                <ul class="list-unstyled mb-0">
                  <li class="mb-3 d-flex align-items-center">
                    <i class="fas fa-envelope text-primary me-3"></i>
                    <div>
                      <small class="text-muted d-block">Email Address</small>
                      <span>{{ userProfile.email }}</span>
                    </div>
                  </li>
                </ul>
              </div>
            </div>
          </div>

          <div class="col-lg-8">
            <div class="card border-0 shadow-sm mb-4">
              <div class="card-body p-4">
                <h4 class="fw-bold text-primary mb-3">About Expert</h4>
              </div>
            </div>

            <div class="card border-0 shadow-sm">
              <div class="card-body p-4">
                <div class="d-flex justify-content-between align-items-center mb-4">
                  <h4 class="fw-bold mb-0">Reviews</h4>
                  <span class="text-muted">{{ userProfile?.reviews?.length || 0 }} total</span>
                </div>

                <div *ngFor="let review of userProfile.reviews || []" class="review-item mb-4 pb-4 border-bottom last-child-border-0">

                  <div class="d-flex justify-content-between mb-2">
                    <div class="d-flex align-items-center">
                      <div class="avatar-sm bg-secondary text-white rounded-circle d-flex align-items-center justify-content-center me-2"
                           style="width: 35px; height: 35px;">
                        {{ review?.authorUsername?.charAt(0) | uppercase }}
                      </div>
                      <strong class="text-dark">{{ review.authorUsername || 'Anonymous' }}</strong>
                    </div>

                    <div class="d-flex align-items-center gap-3">
                      <small class="text-muted">{{ review.created | date:'mediumDate' }}</small>

                      <ng-container *ngIf="review.authorUsername === currentUser">
                        <i class="fas fa-edit text-primary" style="cursor: pointer;" (click)="startEdit(review)" title="Edit Review"></i>
                        <i class="fas fa-trash text-danger" style="cursor: pointer;" (click)="deleteReview(review.id)" title="Delete Review"></i>
                      </ng-container>

                      <ng-container *ngIf="currentUser && replyingReviewId !== review.id">
                        <i class="fas fa-reply text-success" style="cursor: pointer;" (click)="startReply(review)" title="Reply to Review"></i>
                      </ng-container>
                    </div>
                  </div>

                  <p *ngIf="editingReviewId !== review.id" class="text-secondary mb-0 ps-md-5">{{ review.content }}</p>

                  <div *ngIf="editingReviewId === review.id" class="ps-md-5 mt-2">
                    <textarea [(ngModel)]="editReviewContent" class="form-control mb-2" rows="2"></textarea>
                    <button (click)="saveEdit(review.id)" class="btn btn-sm btn-primary me-2">Save</button>
                    <button (click)="cancelEdit()" class="btn btn-sm btn-outline-secondary">Cancel</button>
                  </div>

                  <div *ngIf="review.replies?.length" class="ms-md-5 mt-3 p-3 bg-light border-start border-4 border-success rounded">

                    <div *ngFor="let comment of review.replies; let last = last" [class.border-bottom]="!last" [class.pb-3]="!last" [class.mb-3]="!last">
                      <div class="d-flex justify-content-between align-items-center mb-1">
                        <div class="d-flex align-items-center gap-2">
                          <strong class="text-dark small">{{ comment.authorUsername }}</strong>
                          <span *ngIf="comment.authorUsername === userProfile.username" class="badge bg-primary rounded-pill" style="font-size: 0.65rem;">Expert</span>
                        </div>
                        <small class="text-muted" style="font-size: 0.75rem;">{{ comment.created | date:'short' }}</small>
                      </div>

                      <p class="text-secondary mb-1 small">{{ comment.content }}</p>

                      <button *ngIf="currentUser && currentUser !== comment.authorUsername"
                              class="btn btn-link text-muted p-0 text-decoration-none mt-1"
                              style="font-size: 0.75rem;"
                              (click)="startReplyToComment(review, comment.authorUsername)">
                        <i class="fas fa-reply fa-sm me-1"></i>Reply
                      </button>
                    </div>

                  </div>
                  <div *ngIf="replyingReviewId === review.id" class="ms-md-5 mt-3">
                    <textarea [(ngModel)]="replyReviewContent" class="form-control mb-2" rows="2" placeholder="Write your reply..."></textarea>
                    <button (click)="saveReply(review.id)" class="btn btn-sm btn-success me-2">Submit Reply</button>
                    <button (click)="cancelReply()" class="btn btn-sm btn-outline-secondary">Cancel</button>
                  </div>

                </div>

                <div *ngIf="!userProfile.reviews?.length" class="text-center py-5">
                  <i class="far fa-comment-dots fa-3x text-light mb-3"></i>
                  <p class="text-muted">No reviews yet. Be the first to share your experience!</p>
                </div>

                <div *ngIf="currentUser && userProfile.username !== currentUser" class="mt-4 pt-3 border-top">
                  <h6 class="fw-bold mb-3">Leave a Review:</h6>
                  <textarea
                    [(ngModel)]="newReviewContent"
                    class="form-control mb-3 shadow-sm"
                    rows="3"
                    placeholder="How was your experience with this expert?">
              </textarea>

                  <button
                    (click)="submitReview()"
                    [disabled]="!newReviewContent?.trim() || isLoading"
                    class="btn btn-success w-100 fw-bold py-2 shadow-sm">
                    <span *ngIf="!isLoading"><i class="fas fa-paper-plane me-2"></i> Submit Review</span>
                    <span *ngIf="isLoading" class="spinner-border spinner-border-sm" role="status"></span>
                  </button>
                </div>

              </div>
            </div>
          </div>
        </div>
      </div>
    </div>



    `
})
export class ProfileComponent implements OnInit {
  userProfile: UserProfile | null = null;
  newReviewContent: string = '';
  currentUser: string | null = null;
  isLoading = true;
  editingReviewId: number | null = null;
  editReviewContent: string = '';

  replyingReviewId: number | null = null;
  replyReviewContent: string = '';
  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private userService: UserService,
    private authService: AuthService,
    private reviewService: ReviewService,
    private cdr: ChangeDetectorRef
  ) {
    this.currentUser = this.authService.getCurrentUsername();
  }

  ngOnInit(): void {
    // Взимаме параметъра - може да е 'username' или 'id'
    const param = this.route.snapshot.paramMap.get('username') || this.route.snapshot.paramMap.get('id');

    if (param && param !== 'undefined') {
      this.loadUser(param);
    } else {
      console.warn('Invalid User Param in URL:', param);
      this.isLoading = false;
    }
  }

  // Добави тази функция в класа си
  convertBytesToBase64(byteArray: number[]): string {
    // 1. Преобразуваме Java байтовете (които могат да са отрицателни) в правилни 8-битови байтове
    const uint8Array = new Uint8Array(byteArray);

    // 2. Превръщаме ги в двоичен текст
    let binary = '';
    for (let i = 0; i < uint8Array.length; i++) {
      binary += String.fromCharCode(uint8Array[i]);
    }

    // 3. Кодираме текста в Base64 формат (това, което браузърът иска)
    return btoa(binary);
  }
  startEdit(review: any): void {
    this.editingReviewId = review.id;
    this.editReviewContent = review.content; // Зареждаме стария текст
  }
  saveEdit(reviewId: number): void {
    // 1. Взимаме потребителското име и го запазваме в сигурна локална променлива
    const username = this.userProfile?.username;

    // 2. Проверяваме дали имаме текст и валидно име
    if (!this.editReviewContent?.trim() || !username) return;

    this.reviewService.updateReview(reviewId, this.editReviewContent).subscribe({
      next: () => {
        this.cancelEdit();
        // 3. Използваме сигурната променлива тук
        this.loadReviews(username);
      },
      error: (err) => console.error('Грешка при редакция', err)
    });
  }
  cancelEdit(): void {
    this.editingReviewId = null;
    this.editReviewContent = '';
  }
  // --- НОВ МЕТОД ЗА ИЗТРИВАНЕ ---
  deleteReview(reviewId: number): void {
    const username = this.userProfile?.username;
    if (!username) return;

    if (confirm('Сигурни ли сте, че искате да изтриете това ревю?')) {
      this.reviewService.deleteReview(reviewId).subscribe({
        next: () => this.loadReviews(username),
        error: (err) => console.error('Грешка при изтриване', err)
      });
    }
  }

  // --- НОВИ МЕТОДИ ЗА ОТГОВОР ---
  startReply(review: any): void {
    this.replyingReviewId = review.id;
    this.replyReviewContent = '';
  }

  cancelReply(): void {
    this.replyingReviewId = null;
    this.replyReviewContent = '';
  }

  saveReply(reviewId: number): void {
    const profileUsername = this.userProfile?.username;
    const loggedInUser = this.currentUser; // Взимаме името на логнатия потребител

    // Проверяваме дали имаме текст, дали сме в профил и дали потребителят е логнат
    if (!this.replyReviewContent?.trim() || !profileUsername || !loggedInUser) return;

    // Подаваме loggedInUser като трети параметър
    this.reviewService.replyToReview(reviewId, this.replyReviewContent, loggedInUser).subscribe({
      next: () => {
        this.cancelReply();
        this.loadReviews(profileUsername); // Презареждаме, за да видим новия коментар
      },
      error: (err) => console.error('Грешка при отговор', err)
    });
  }

  // --- НОВ МЕТОД: Когато цъкнеш Reply върху вече съществуващ коментар ---
  startReplyToComment(review: any, commentAuthor: string): void {
    this.replyingReviewId = review.id;
    // Зареждаме полето с @името на човека, на когото отговаряме
    this.replyReviewContent = `@${commentAuthor} `;
  }
  loadUser(username: string) {
    this.authService.getUserProfile(username).subscribe({
      next: (data: any) => {
        this.userProfile = data.userProfile ? data.userProfile : data;
        this.isLoading = false;
        this.cdr.detectChanges(); // Задължително го остави!
      },
      error: (err) => console.error('Error loading user', err)
    });
  }

// Този метод автоматично ще разбере какво е пратил бекендът и ще го направи на снимка
  get profileImageSrc(): string {
    const photo = this.userProfile?.photoUrl;

    // Ако потребителят няма снимка, показваме дефолтна
    if (!photo) {
      return 'https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_1280.png';
    }

    // Случай 1: Ако вече е готов и правилен URL
    if (typeof photo === 'string' && photo.startsWith('data:image')) {
      return photo;
    }

    // Случай 2: Ако бекендът го е пратил като ТЕКСТ, който прилича на масив: "[-119, 80...]"
    let parsedData = photo;
    if (typeof photo === 'string' && photo.startsWith('[')) {
      try {
        parsedData = JSON.parse(photo);
      } catch (e) {
        console.error('Error parsing image array', e);
      }
    }

    // Случай 3: Ако е истински JS масив от числа
    if (Array.isArray(parsedData)) {
      const base64String = this.convertBytesToBase64(parsedData);
      return 'data:image/png;base64,' + base64String;
    }

    // Случай 4: Ако бекендът го е пратил директно като чист Base64 текст
    if (typeof parsedData === 'string') {
      return 'data:image/png;base64,' + parsedData;
    }

    return 'https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_1280.png';
  }


  // Не забравяй да добавиш ReviewService в конструктора!
  submitReview(): void {
    const profile = this.userProfile;

    if (!profile || !profile.username || !this.newReviewContent?.trim()) {
      return;
    }

    const currentUsername = this.authService.getCurrentUsername();
    if (!currentUsername) {
      alert("You must be logged in to leave a review!");
      return;
    }

    this.isLoading = true;

    // Пращаме ревюто (подаваме username)
    this.reviewService.addReview(currentUsername, profile.username, this.newReviewContent).subscribe({
      next: (response: any) => {
        alert('Review added successfully!');
        this.newReviewContent = '';

        // Презареждаме САМО ревютата, за да се появи новото веднага!
        this.loadReviews(profile.username);
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Failed to add review', err);
        this.isLoading = false;
        alert('Could not add review. Check console for details.');
      }
    });
  }

  // В profile.component.ts добави този метод:
  loadReviews(username: string) {
    this.reviewService.getReviewsForUser(username).subscribe({
      next: (reviews: any[]) => {
        if (this.userProfile) {
          this.userProfile.reviews = reviews; // Слагаме изтеглените ревюта в профила
        }
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Error loading reviews', err)
    });
  }
}


