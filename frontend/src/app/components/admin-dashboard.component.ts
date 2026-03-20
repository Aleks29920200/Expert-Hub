import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import {AdminPanelComponent} from './admin-home.component';
import {AdminSkillsComponent} from './admin-skill.component';

// ВАЖНО: Увери се, че пътищата до твоите компоненти са правилни!


@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  // Тук казваме на Angular, че ще ползваме тези два компонента вътре в HTML-а
  imports: [CommonModule, AdminPanelComponent, AdminSkillsComponent],
  template: `
    <div class="container mt-5">
      <div class="d-flex justify-content-between align-items-center mb-4">
        <h2>⚙️ Главен Административен Панел</h2>
        <span class="badge bg-primary fs-6">Режим: {{ activeTab === 'users' ? 'Потребители' : 'Умения' }}</span>
      </div>

      <ul class="nav nav-pills nav-fill mb-4 p-2 bg-light rounded shadow-sm border">
        <li class="nav-item">
          <button
            class="nav-link py-3"
            [class.active]="activeTab === 'users'"
            (click)="switchTab('users')">
            👥 Управление на Потребители
          </button>
        </li>
        <li class="nav-item">
          <button
            class="nav-link py-3"
            [class.active]="activeTab === 'skills'"
            (click)="switchTab('skills')">
            🎯 Управление на Умения
          </button>
        </li>
      </ul>

      <div class="admin-content-wrapper">

        <app-admin-panel *ngIf="activeTab === 'users'"></app-admin-panel>

        <app-admin-skills *ngIf="activeTab === 'skills'"></app-admin-skills>

      </div>
    </div>
  `,
  styles: [`
    .nav-pills { gap: 10px; }
    .nav-link {
      cursor: pointer;
      font-weight: 600;
      font-size: 1.1rem;
      border-radius: 8px;
      transition: all 0.3s ease;
      color: #495057;
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
  activeTab: 'users' | 'skills' = 'users';

  // Метод за смяна на таба
  switchTab(tab: 'users' | 'skills'): void {
    this.activeTab = tab;
  }
}
