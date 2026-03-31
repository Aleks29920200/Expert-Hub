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
      <h2 class="mb-4">👥 Управление на потребители</h2>

      <div class="card shadow-sm mb-5">
        <div class="card-header" [ngClass]="isEditMode ? 'bg-warning text-dark' : 'bg-primary text-white'">
          <h4 class="mb-0">{{ isEditMode ? '📝 Редактиране на потребител' : '✨ Добавяне на нов потребител' }}</h4>
        </div>
        <div class="card-body">
          <div class="row g-3">

            <div class="col-md-4">
              <label class="form-label">Потребителско име <span class="text-danger">*</span></label>
              <input [(ngModel)]="selectedUser.username" class="form-control" placeholder="напр. ivan88">
            </div>
            <div class="col-md-4">
              <label class="form-label">Имейл <span class="text-danger">*</span></label>
              <input type="email" [(ngModel)]="selectedUser.email" class="form-control" placeholder="ivan@example.com">
            </div>
            <div class="col-md-4">
              <label class="form-label">Парола {{ isEditMode ? '(остави празно, ако не сменяш)' : '*' }}</label>
              <input type="password" [(ngModel)]="selectedUser.password" class="form-control" placeholder="********">
            </div>

            <div class="col-md-6">
              <label class="form-label">Име</label>
              <input [(ngModel)]="selectedUser.firstName" class="form-control" placeholder="Иван">
            </div>
            <div class="col-md-6">
              <label class="form-label">Фамилия</label>
              <input [(ngModel)]="selectedUser.lastName" class="form-control" placeholder="Иванов">
            </div>

            <div class="col-md-8">
              <label class="form-label">Адрес</label>
              <input [(ngModel)]="selectedUser.address" class="form-control" placeholder="гр. София, ул. Примерна 1">
            </div>
            <div class="col-md-4">
              <label class="form-label">Снимка (URL профил)</label>
              <img [src]="selectedUser.picture || 'https://ui-avatars.com/api/?name=' + selectedUser.username + '&background=random'"
                   alt="avatar"
                   class="rounded-circle"
                   style="width: 40px; height: 40px; object-fit: cover;">
            </div>

            <div class="col-12 mt-4 text-end">
              <button *ngIf="isEditMode" (click)="resetForm()" class="btn btn-outline-secondary me-2">Отказ</button>
              <button (click)="saveUser()" class="btn" [ngClass]="isEditMode ? 'btn-warning' : 'btn-primary'">
                {{ isEditMode ? 'Обнови потребителя' : 'Запази потребителя' }}
              </button>
            </div>
          </div>
        </div>
      </div>

      <div class="table-responsive">
        <table class="table table-hover align-middle border">
          <thead class="table-light">
          <tr>
            <th>Снимка</th>
            <th>ID</th>
            <th>Потребител</th>
            <th>Имена</th>
            <th>Имейл</th>
            <th>Адрес</th>
            <th class="text-center">Действия</th>
          </tr>
          </thead>
          <tbody>
          <tr *ngFor="let user of users">
            <td>
              <img [src]="user.picture || 'https://ui-avatars.com/api/?name=' + user.username + '&background=random'"
                   alt="avatar"
                   class="rounded-circle"
                   style="width: 40px; height: 40px; object-fit: cover;">
            </td>
            <td><span class="badge bg-secondary">#{{ user.userId }}</span></td>
            <td><strong>{{ user.username }}</strong></td>
            <td>{{ user.firstName }} {{ user.lastName }}</td>
            <td>{{ user.email }}</td>
            <td class="text-muted small">{{ user.address || 'Няма въведен' }}</td>
            <td class="text-center">
              <button (click)="editUser(user)" class="btn btn-sm btn-outline-primary me-2" title="Редактирай">✏️
              </button>
              <button (click)="deleteUser(user.userId)" class="btn btn-sm btn-outline-danger" title="Изтрий">🗑️</button>
            </td>
          </tr>
          <tr *ngIf="users.length === 0">
            <td colspan="7" class="text-center text-muted p-4">Няма намерени потребители.</td>
          </tr>
          </tbody>
        </table>
      </div>
    </div>
    \`
  `,
  styles: [`
    .container { max-width: 1200px; }
    .card { border: none; border-radius: 10px; }
    .table th { text-transform: uppercase; font-size: 0.85rem; letter-spacing: 1px; }
    .btn-sm { padding: 0.4rem 0.8rem; }
  `]
})
export class AdminPanelComponent implements OnInit {
  users: any[] = [];

  // Обновеният обект с всички полета от DTO-то
  selectedUser: any = {
    userId: null,
    username: '',
    email: '',
    password: '',
    firstName: '',
    lastName: '',
    address: '',
    picture: ''
  };

  isEditMode = false;

  constructor(private adminService: AdminService) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.adminService.getUsers().subscribe({
      next: (data) => this.users = data,
      error: (err) => console.error('Грешка при зареждане:', err)
    });
  }

  saveUser(): void {
    if (!this.selectedUser.username || !this.selectedUser.email) {
      alert('Потребителското име и имейлът са задължителни!');
      return;
    }

    if (this.isEditMode) {
      // Използваме userId!
      this.adminService.updateUser(this.selectedUser.userId, this.selectedUser).subscribe({
        next: () => {
          this.loadUsers();
          this.resetForm();
        },
        error: (err) => alert('Грешка при обновяване!')
      });
    } else {
      if (!this.selectedUser.password) {
        alert('Моля, въведете парола за новия потребител!');
        return;
      }
      this.adminService.createUser(this.selectedUser).subscribe({
        next: () => {
          this.loadUsers();
          this.resetForm();
        },
        error: (err) => alert('Грешка при създаване!')
      });
    }
  }

  editUser(user: any): void {
    // Копираме данните. НЕ показваме криптираната парола от базата!
    this.selectedUser = { ...user, password: '' };
    this.isEditMode = true;
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  deleteUser(userId: number): void { // Тук също очакваме userId
    if (confirm('Сигурни ли сте, че искате да изтриете този потребител?')) {
      this.adminService.deleteUser(userId).subscribe({
        next: () => this.loadUsers(),
        error: (err) => alert('Грешка при изтриване!')
      });
    }
  }

  resetForm(): void {
    this.selectedUser = {
      userId: null,
      username: '',
      email: '',
      password: '',
      firstName: '',
      lastName: '',
      address: '',
      picture: ''
    };
    this.isEditMode = false;
  }
}
