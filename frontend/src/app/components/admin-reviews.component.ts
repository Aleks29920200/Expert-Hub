import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {AdminReviewService} from '../services/admin-review.service';


@Component({
  selector: 'app-admin-reviews',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="container mt-4">
      <h2 class="mb-4">⭐ Управление на ревюта (Reviews)</h2>

      <div class="card shadow-sm mb-5">
        <div class="card-header" [ngClass]="isEditMode ? 'bg-warning text-dark' : 'bg-info text-white'">
          <h4 class="mb-0">{{ isEditMode ? '📝 Редактиране на ревю' : '✨ Добавяне на ново ревю' }}</h4>
        </div>
        <div class="card-body">
          <div class="row g-3">

            <div class="col-md-4">
              <label class="form-label">Оценяващ (Клиент) <span class="text-danger">*</span></label>
              <select [(ngModel)]="selectedReview.reviewerUsername" class="form-select">
                <option value="" disabled>-- Избери клиент --</option>
                <option *ngFor="let user of clients" [value]="user.username">
                  👤 {{ user.username }} ({{ user.firstName }} {{ user.lastName }})
                </option>
              </select>
              <small class="text-muted" *ngIf="clients.length === 0">Не са намерени клиенти</small>
            </div>

            <div class="col-md-4">
              <label class="form-label">Оценяван (Експерт) <span class="text-danger">*</span></label>
              <select [(ngModel)]="selectedReview.targetUsername" class="form-select">
                <option value="" disabled>-- Избери експерт --</option>
                <option *ngFor="let user of experts" [value]="user.username">
                  🎯 {{ user.username }} ({{ user.firstName }} {{ user.lastName }})
                </option>
              </select>
              <small class="text-muted" *ngIf="experts.length === 0">Не са намерени експерти</small>
            </div>

            <div class="col-md-4">
              <label class="form-label">Дата</label>
              <input type="date" [(ngModel)]="selectedReview.date" class="form-control">
            </div>

            <div class="col-md-2">
              <label class="form-label">Оценка (1-5)</label>
              <input type="number" min="1" max="5" [(ngModel)]="selectedReview.rating" class="form-control">
            </div>

            <div class="col-md-10">
              <label class="form-label">Съдържание на ревюто <span class="text-danger">*</span></label>
              <textarea [(ngModel)]="selectedReview.content" class="form-control" rows="2" placeholder="Напишете коментар..."></textarea>
            </div>

            <div class="col-12 mt-4 text-end">
              <button *ngIf="isEditMode" (click)="resetForm()" class="btn btn-outline-secondary me-2">Отказ</button>
              <button (click)="saveReview()" class="btn" [ngClass]="isEditMode ? 'btn-warning' : 'btn-info text-white'">
                {{ isEditMode ? 'Обнови ревюто' : 'Запази ревюто' }}
              </button>
            </div>
          </div>
        </div>
      </div>

      <div class="table-responsive">
        <table class="table table-hover align-middle border">
          <thead class="table-light">
            <tr>
              <th>ID</th>
              <th>Оценяващ</th>
              <th>Оценяван</th>
              <th>Оценка</th>
              <th>Съдържание</th>
              <th>Дата</th>
              <th class="text-center">Действия</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let review of reviews">
              <td><span class="badge bg-secondary">#{{ review.id }}</span></td>
              <td><strong>{{ review.reviewingUser || 'N/A' }}</strong></td>
              <td><strong>{{ review.reviewedUser || 'N/A' }}</strong></td>
              <td>
                <span class="text-warning fw-bold">{{ getStars(review.rating) }}</span>
              </td>
              <td><small class="text-muted">{{ review.reviewText | slice:0:50 }}{{ review.reviewText?.length > 50 ? '...' : '' }}</small></td>
              <td>{{ review.dateOfReview | date:'dd.MM.yyyy' }}</td>
              <td class="text-center">
                <button (click)="editReview(review)" class="btn btn-sm btn-outline-primary me-2" title="Редактирай">✏️</button>
                <button (click)="deleteReview(review.id)" class="btn btn-sm btn-outline-danger" title="Изтрий">🗑️</button>
              </td>
            </tr>
            <tr *ngIf="reviews.length === 0">
              <td colspan="7" class="text-center text-muted p-4">Няма намерени ревюта.</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  `,
  styles: [`.card { border: none; border-radius: 10px; }`]
})
export class AdminReviewsComponent implements OnInit {
  reviews: any[] = [];

  // Добавяме отделни масиви
  users: any[] = [];
  clients: any[] = [];
  experts: any[] = [];

  selectedReview: any = {
    id: null,
    reviewerUsername: '',
    targetUsername: '',
    content: '',
    date: '',
    rating: 5
  };

  isEditMode = false;

  constructor(private adminReviewService: AdminReviewService) {}

  ngOnInit(): void {
    this.loadReviews();
    this.loadUsers();
  }

  loadReviews(): void {
    this.adminReviewService.getReviews().subscribe({
      next: (data) => this.reviews = data,
      error: (err) => console.error('Грешка при зареждане на ревюта:', err)
    });
  }

  loadUsers(): void {
    this.adminReviewService.getUsers().subscribe({
      next: (data) => {
        this.users = data;
        // Филтрираме потребителите въз основа на техните роли
        this.clients = this.users.filter(u => this.hasRole(u, 'CLIENT'));
        this.experts = this.users.filter(u => this.hasRole(u, 'EXPERT'));
      },
      error: (err) => console.error('Грешка при зареждане на потребители:', err)
    });
  }

  // Помощен метод за проверка на ролите
  hasRole(user: any, roleName: string): boolean {
    if (!user || !user.roles) return false;

   return user.roles.some((r: any) => r.authority === 'ROLE_' + roleName || r.name === roleName);
  }

  saveReview(): void {
    if (!this.selectedReview.reviewerUsername || !this.selectedReview.targetUsername || !this.selectedReview.content) {
      alert('Оценяващият, оценяваният и съдържанието са задължителни!');
      return;
    }

    if (this.isEditMode) {
      this.adminReviewService.updateReview(this.selectedReview.id, this.selectedReview).subscribe({
        next: () => { this.loadReviews(); this.resetForm(); },
        error: (err) => alert('Грешка при обновяване!')
      });
    } else {
      this.adminReviewService.createReview(this.selectedReview).subscribe({
        next: () => { this.loadReviews(); this.resetForm(); },
        error: (err) => alert('Грешка при създаване!')
      });
    }
  }

  editReview(review: any): void {
    this.selectedReview = {
      id: review.id,
      reviewerUsername: review.reviewingUser?.username || '',
      targetUsername: review.reviewedUser?.username || '',
      content: review.reviewText || '',
      date: review.dateOfReview || '',
      rating: review.rating || 5
    };
    this.isEditMode = true;
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  deleteReview(id: number): void {
    if (confirm('Сигурни ли сте, че искате да изтриете това ревю?')) {
      this.adminReviewService.deleteReview(id).subscribe({
        next: () => this.loadReviews(),
        error: (err) => alert('Грешка при изтриване!')
      });
    }
  }

  resetForm(): void {
    this.selectedReview = { id: null, reviewerUsername: '', targetUsername: '', content: '', date: '', rating: 5 };
    this.isEditMode = false;
  }

  getStars(rating: number): string {
    if (!rating) return '⭐';
    return '⭐'.repeat(rating);
  }
}
