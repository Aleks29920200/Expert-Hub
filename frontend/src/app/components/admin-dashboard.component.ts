import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

// Импорти на всички CRUD компоненти
import { AdminPanelComponent } from './admin-home.component';
import { AdminSkillsComponent } from './admin-skill.component';
import { AdminAppointmentsComponent } from './admin-appointments.component';
import { AdminReviewsComponent } from './admin-reviews.component';
import {AdminOffersComponent} from './admin-offer.component';

type TabType = 'users' | 'skills' | 'appointments' | 'reviews' | 'offers';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    AdminPanelComponent,
    AdminSkillsComponent,
    AdminAppointmentsComponent,
    AdminReviewsComponent,
    AdminOffersComponent
  ],
  template: `
    <div class="container-fluid mt-4 px-4">
      <div class="d-flex justify-content-between align-items-center mb-4">
        <h2>⚙️ Главен Административен Панел</h2>
        <span class="badge bg-primary fs-6">Режим: {{ getTabName() }}</span>
      </div>

      <ul class="nav nav-pills nav-fill mb-4 p-2 bg-light rounded shadow-sm border">
        <li class="nav-item">
          <button
            class="nav-link py-3"
            [class.active]="activeTab === 'users'"
            (click)="switchTab('users')">
            👥 Потребители
          </button>
        </li>
        <li class="nav-item">
          <button
            class="nav-link py-3"
            [class.active]="activeTab === 'skills'"
            (click)="switchTab('skills')">
            🎯 Умения
          </button>
        </li>
        <li class="nav-item">
          <button
            class="nav-link py-3"
            [class.active]="activeTab === 'appointments'"
            (click)="switchTab('appointments')">
            📅 Срещи
          </button>
        </li>
        <li class="nav-item">
          <button
            class="nav-link py-3"
            [class.active]="activeTab === 'offers'"
            (click)="switchTab('offers')">
            💼 Оферти
          </button>
        </li>
        <li class="nav-item">
          <button
            class="nav-link py-3"
            [class.active]="activeTab === 'reviews'"
            (click)="switchTab('reviews')">
            ⭐ Ревюта
          </button>
        </li>
      </ul>

      <div class="admin-content-wrapper">
        <app-admin-panel *ngIf="activeTab === 'users'"></app-admin-panel>
        <app-admin-skills *ngIf="activeTab === 'skills'"></app-admin-skills>
        <app-admin-appointments *ngIf="activeTab === 'appointments'"></app-admin-appointments>
        <app-admin-offers *ngIf="activeTab === 'offers'"></app-admin-offers>
        <app-admin-reviews *ngIf="activeTab === 'reviews'"></app-admin-reviews>
      </div>
    </div>
  `,
  styles: [`
    .nav-pills { gap: 10px; }
    .nav-link {
      cursor: pointer;
      font-weight: 600;
      font-size: 1.05rem;
      border-radius: 8px;
      transition: all 0.3s ease;
      color: #495057;
      white-space: nowrap; /* Предпазва текста от счупване на 2 реда при по-малки екрани */
    }
    .nav-link:hover:not(.active) { background-color: #e9ecef; }
    .nav-link.active {
      background-color: #212529 !important; /* Тъмен модерен цвят за активния таб */
      color: white !important;
      box-shadow: 0 4px 6px rgba(0,0,0,0.1);
    }
    .admin-content-wrapper {
      animation: fadeIn 0.4s ease-in-out;
    }
    @keyframes fadeIn {
      from { opacity: 0; transform: translateY(10px); }
      to { opacity: 1; transform: translateY(0); }
    }
  `]
})
export class AdminDashboardComponent {

  // Променлива, която пази информация кой таб е отворен в момента
  activeTab: TabType = 'users';

  // Метод за смяна на таба
  switchTab(tab: TabType): void {
    this.activeTab = tab;
  }

  // Помощен метод за красиво показване на името на таба в баджа горе вдясно
  getTabName(): string {
    const names = {
      'users': 'Потребители',
      'skills': 'Умения',
      'appointments': 'Срещи',
      'offers': 'Оферти',
      'reviews': 'Ревюта'
    };
    return names[this.activeTab] || 'Неизвестен';
  }
}
