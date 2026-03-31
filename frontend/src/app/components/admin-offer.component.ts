import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {AdminOfferService} from '../services/admin-offer.service';


@Component({
  selector: 'app-admin-offers',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="container mt-4">
      <h2 class="mb-4">💼 Управление на Оферти</h2>

      <div class="card shadow-sm mb-5">
        <div class="card-header" [ngClass]="isEditMode ? 'bg-warning text-dark' : 'bg-primary text-white'">
          <h4 class="mb-0">{{ isEditMode ? '📝 Редактиране на оферта' : '✨ Създаване на нова оферта' }}</h4>
        </div>
        <div class="card-body">
          <div class="row g-3">

            <div class="col-md-6">
              <label class="form-label">Заглавие / Име на услугата <span class="text-danger">*</span></label>
              <input [(ngModel)]="selectedOffer.title" class="form-control" placeholder="напр. Изработка на уебсайт">
            </div>

            <div class="col-md-3">
              <label class="form-label">Цена <span class="text-danger">*</span></label>
              <input type="number" step="0.01" [(ngModel)]="selectedOffer.price" class="form-control" placeholder="0.00">
            </div>

            <div class="col-md-3">
              <label class="form-label">Продавач (Seller) <span class="text-danger">*</span></label>
              <select [(ngModel)]="selectedOffer.sellerId" class="form-select">
                <option [ngValue]="null" disabled>-- Избери потребител --</option>
                <option *ngFor="let user of users" [value]="user.userId">
                  👤 {{ user.username }}
                </option>
              </select>
            </div>
            <div class="col-md-4">
              <label class="form-label">Статус</label>
              <select [(ngModel)]="selectedOffer.status" class="form-select">
                <option value="ACTIVE">ACTIVE</option>
                <option value="INACTIVE">INACTIVE</option>
                <option value="PENDING">PENDING</option>
              </select>
            </div>

            <div class="col-md-8">
              <label class="form-label">Описание</label>
              <textarea [(ngModel)]="selectedOffer.description" class="form-control" rows="2" placeholder="Детайли за офертата..."></textarea>
            </div>

            <div class="col-12 mt-4 text-end">
              <button *ngIf="isEditMode" (click)="resetForm()" class="btn btn-outline-secondary me-2">Отказ</button>
              <button (click)="saveOffer()" class="btn" [ngClass]="isEditMode ? 'btn-warning' : 'btn-primary'">
                {{ isEditMode ? 'Обнови офертата' : 'Запази офертата' }}
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
              <th>Заглавие</th>
              <th>Цена</th>
              <th>Продавач</th>
              <th>Статус</th>
              <th class="text-center">Действия</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let offer of offers">
              <td><span class="badge bg-secondary">#{{ offer.id }}</span></td>
              <td><strong>{{ offer.title }}</strong></td>
              <td class="text-success fw-bold">{{ offer.price | number:'1.2-2' }}</td>
              <td>
                <span *ngIf="offer.seller">👤 {{ offer.seller.username }}</span>
                <span *ngIf="!offer.seller" class="text-muted">Няма</span>
              </td>
              <td>
                <span class="badge" [ngClass]="offer.status === 'ACTIVE' ? 'bg-success' : 'bg-danger'">
                  {{ offer.status || 'ACTIVE' }}
                </span>
              </td>
              <td class="text-center">
                <button (click)="editOffer(offer)" class="btn btn-sm btn-outline-primary me-2">✏️</button>
                <button (click)="deleteOffer(offer.id)" class="btn btn-sm btn-outline-danger">🗑️</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  `,
  styles: [`.card { border: none; border-radius: 10px; }`]
})
export class AdminOffersComponent implements OnInit {
  offers: any[] = [];
  users: any[] = []; // ДОБАВЕНО: Масив за потребителите

  selectedOffer: any = {
    id: null, title: '', price: null, sellerId: null, status: 'ACTIVE', description: ''
  };

  isEditMode = false;

  constructor(private adminOfferService: AdminOfferService) {}

  ngOnInit(): void {
    this.loadOffers();
    this.loadUsers(); // ДОБАВЕНО: Зареждаме потребителите при старт
  }

  loadOffers(): void {
    this.adminOfferService.getOffers().subscribe(data => this.offers = data);
  }

  // ДОБАВЕНО: Метод за изтегляне на потребителите
  loadUsers(): void {
    this.adminOfferService.getUsers().subscribe({
      next: (data) => this.users = data,
      error: (err) => console.error('Грешка при зареждане на потребители:', err)
    });
  }

  saveOffer(): void {
    if (!this.selectedOffer.title || this.selectedOffer.price === null || !this.selectedOffer.sellerId) {
      alert('Заглавието, цената и Продавачът са задължителни!');
      return;
    }
    // ... логиката за запазване остава същата
    if (this.isEditMode) {
      this.adminOfferService.updateOffer(this.selectedOffer.id, this.selectedOffer).subscribe(() => { this.loadOffers(); this.resetForm(); });
    } else {
      this.adminOfferService.createOffer(this.selectedOffer).subscribe(() => { this.loadOffers(); this.resetForm(); });
    }
  }

  editOffer(offer: any): void {
    this.selectedOffer = {
      id: offer.id,
      title: offer.title,
      price: offer.price,
      sellerId: offer.seller?.userId || null, // Взимаме ID-то на продавача
      status: offer.status || 'ACTIVE',
      description: offer.description || ''
    };
    this.isEditMode = true;
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  deleteOffer(id: number): void {
    if (confirm('Сигурни ли сте?')) {
      this.adminOfferService.deleteOffer(id).subscribe(() => this.loadOffers());
    }
  }

  resetForm(): void {
    this.selectedOffer = { id: null, title: '', price: null, sellerId: null, status: 'ACTIVE', description: '' };
    this.isEditMode = false;
  }
}
