import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {AdminService} from '../services/admin.service';


@Component({
  selector: 'app-admin-panel',
  standalone: true,
  imports: [CommonModule, FormsModule], // ВАЖНО: Тук добавяме нужните модули
  template: `
    <div class="container mt-4">
      <h2 class="mb-4">🛡️ Административен панел - Потребители</h2>

      <div class="card shadow-sm mb-5">
        <div class="card-header" [ngClass]="isEditMode ? 'bg-primary text-white' : 'bg-success text-white'">
          <h4 class="mb-0">{{ isEditMode ? '📝 Редактиране на потребител' : '➕ Добавяне на нов потребител' }}</h4>
        </div>
        <div class="card-body">
          <div class="row g-3">
            <div class="col-md-4">
              <label class="form-label">Потребителско име</label>
              <input [(ngModel)]="selectedUser.username" class="form-control" placeholder="Въведете име...">
            </div>
            <div class="col-md-4">
              <label class="form-label">Имейл адрес</label>
              <input [(ngModel)]="selectedUser.email" type="email" class="form-control" placeholder="example@mail.com">
            </div>
            <div class="col-md-4 d-flex align-items-end">
              <button (click)="saveUser()" class="btn" [ngClass]="isEditMode ? 'btn-primary' : 'btn-success'">
                {{ isEditMode ? 'Обнови' : 'Запази' }}
              </button>
              <button *ngIf="isEditMode" (click)="resetForm()" class="btn btn-outline-secondary ms-2">Отказ</button>
            </div>
          </div>
        </div>
      </div>

      <div class="table-responsive">
        <table class="table table-hover align-middle border">
          <thead class="table-light">
            <tr>
              <th>ID</th>
              <th>Потребителско име</th>
              <th>Имейл</th>
              <th class="text-center">Действия</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let user of users">
              <td><strong>#{{ user.id }}</strong></td>
              <td>{{ user.username }}</td>
              <td>{{ user.email }}</td>
              <td class="text-center">
                <button (click)="editUser(user)" class="btn btn-sm btn-outline-primary me-2" title="Редактирай">
                  ✏️ Редактирай
                </button>
                <button (click)="deleteUser(user.id)" class="btn btn-sm btn-outline-danger" title="Изтрий">
                  🗑️ Изтрий
                </button>
              </td>
            </tr>
            <tr *ngIf="users.length === 0">
              <td colspan="4" class="text-center text-muted p-4">Няма намерени потребители.</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  `,
  styles: [`
    .container { max-width: 1000px; }
    .card { border: none; border-radius: 10px; }
    .table th { text-transform: uppercase; font-size: 0.85rem; letter-spacing: 1px; }
    .btn-sm { padding: 0.4rem 0.8rem; }
  `]
})
export class AdminPanelComponent implements OnInit {
  users: any[] = [];
  selectedUser: any = { username: '', email: '' };
  isEditMode = false;

  constructor(private adminService: AdminService) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  // Зареждане на списъка
  loadUsers(): void {
    this.adminService.getUsers().subscribe({
      next: (data) => this.users = data,
      error: (err) => console.error('Грешка при зареждане:', err)
    });
  }

  // Създаване или Обновяване
  saveUser(): void {
    if (!this.selectedUser.username || !this.selectedUser.email) {
      alert('Моля, попълнете всички полета!');
      return;
    }

    if (this.isEditMode) {
      // UPDATE
      this.adminService.updateUser(this.selectedUser.id, this.selectedUser).subscribe({
        next: () => {
          this.loadUsers();
          this.resetForm();
        },
        error: (err) => alert('Грешка при обновяване!')
      });
    } else {
      // CREATE
      this.adminService.createUser(this.selectedUser).subscribe({
        next: () => {
          this.loadUsers();
          this.resetForm();
        },
        error: (err) => alert('Грешка при създаване!')
      });
    }
  }

  // Подготовка за редакция
  editUser(user: any): void {
    this.selectedUser = { ...user }; // Копираме обекта, за да не се променя в таблицата веднага
    this.isEditMode = true;
    window.scrollTo({ top: 0, behavior: 'smooth' }); // Скролваме нагоре до формата
  }

  // Изтриване
  deleteUser(id: number): void {
    if (confirm('Сигурни ли сте, че искате да изтриете този потребител?')) {
      this.adminService.deleteUser(id).subscribe({
        next: () => this.loadUsers(),
        error: (err) => alert('Грешка при изтриване!')
      });
    }
  }

  // Нулиране на формата
  resetForm(): void {
    this.selectedUser = { username: '', email: '' };
    this.isEditMode = false;
  }
}
