import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {AdminAppointmentService} from '../services/admin-appointment.service';


@Component({
  selector: 'app-admin-appointments',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="container mt-4">
      <h2 class="mb-4">📅 Управление на срещи (Appointments)</h2>

      <div class="card shadow-sm mb-5">
        <div class="card-header" [ngClass]="isEditMode ? 'bg-warning text-dark' : 'bg-primary text-white'">
          <h4 class="mb-0">{{ isEditMode ? '📝 Редактиране на среща' : '✨ Създаване на нова среща' }}</h4>
        </div>
        <div class="card-body">
          <div class="row g-3">

            <div class="col-md-4">
              <label class="form-label">Тема / Име на срещата <span class="text-danger">*</span></label>
              <input [(ngModel)]="selectedAppt.name" class="form-control" placeholder="напр. Урок по Java">
            </div>

            <div class="col-md-4">
              <label class="form-label">Дата и час <span class="text-danger">*</span></label>
              <input type="datetime-local" [(ngModel)]="selectedAppt.dateOfAppointment" class="form-control">
            </div>

            <div class="col-md-4">
              <label class="form-label">ID на умението (Skill ID)</label>
              <input type="number" [(ngModel)]="selectedAppt.skillId" class="form-control" placeholder="напр. 1">
            </div>

            <div class="col-md-6">
              <label class="form-label">Потребителско име (Клиент)</label>
              <input [(ngModel)]="selectedAppt.requesterUsername" class="form-control" placeholder="username123">
            </div>

            <div class="col-md-6">
              <label class="form-label">Потребителско име (Експерт)</label>
              <input [(ngModel)]="selectedAppt.providerUsername" class="form-control" placeholder="expert_user">
            </div>

            <div class="col-12 mt-4 text-end">
              <button *ngIf="isEditMode" (click)="resetForm()" class="btn btn-outline-secondary me-2">Отказ</button>
              <button (click)="saveAppointment()" class="btn" [ngClass]="isEditMode ? 'btn-warning' : 'btn-primary'">
                {{ isEditMode ? 'Обнови срещата' : 'Запази срещата' }}
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
              <th>Тема</th>
              <th>Дата</th>
              <th>Клиент</th>
              <th>Експерт</th>
              <th>Статус</th>
              <th class="text-center">Действия</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let appt of appointments">
              <td><span class="badge bg-secondary">#{{ appt.id }}</span></td>
              <td><strong>{{ appt.name }}</strong></td>
              <td>{{ appt.dateOfAppointment | date:'dd.MM.yyyy HH:mm' }}</td>
              <td>{{ appt.requester?.username || 'N/A' }}</td>
              <td>{{ appt.provider?.username || 'N/A' }}</td>
              <td>
                <span class="badge"
                      [ngClass]="{'bg-warning text-dark': appt.status === 'PENDING', 'bg-success': appt.status === 'APPROVED', 'bg-danger': appt.status === 'REJECTED'}">
                  {{ appt.status || 'PENDING' }}
                </span>
              </td>
              <td class="text-center">
                <button (click)="editAppointment(appt)" class="btn btn-sm btn-outline-primary me-2">✏️</button>
                <button (click)="deleteAppointment(appt.id)" class="btn btn-sm btn-outline-danger">🗑️</button>
              </td>
            </tr>
            <tr *ngIf="appointments.length === 0">
              <td colspan="7" class="text-center text-muted p-4">Няма намерени срещи.</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  `,
  styles: [`.card { border: none; border-radius: 10px; }`]
})
export class AdminAppointmentsComponent implements OnInit {
  appointments: any[] = [];

  selectedAppt: any = {
    id: null,
    name: '',
    dateOfAppointment: '',
    requesterUsername: '',
    providerUsername: '',
    skillId: null
  };

  isEditMode = false;

  constructor(private adminAppointmentService: AdminAppointmentService) {}

  ngOnInit(): void {
    this.loadAppointments();
  }

  loadAppointments(): void {
    this.adminAppointmentService.getAppointments().subscribe({
      next: (data) => this.appointments = data,
      error: (err) => console.error('Грешка при зареждане:', err)
    });
  }

  saveAppointment(): void {
    if (!this.selectedAppt.name || !this.selectedAppt.dateOfAppointment) {
      alert('Темата и датата са задължителни!');
      return;
    }

    if (this.isEditMode) {
      this.adminAppointmentService.updateAppointment(this.selectedAppt.id, this.selectedAppt).subscribe({
        next: () => { this.loadAppointments(); this.resetForm(); },
        error: (err) => alert('Грешка при обновяване!')
      });
    } else {
      this.adminAppointmentService.createAppointment(this.selectedAppt).subscribe({
        next: () => { this.loadAppointments(); this.resetForm(); },
        error: (err) => alert('Грешка при създаване!')
      });
    }
  }

  editAppointment(appt: any): void {
    // Подготвяме данните за формата (мапваме от обекта към DTO структурата)
    this.selectedAppt = {
      id: appt.id,
      name: appt.name,
      dateOfAppointment: appt.dateOfAppointment,
      requesterUsername: appt.requester?.username || '',
      providerUsername: appt.provider?.username || '',
      skillId: appt.skill?.id || null
    };
    this.isEditMode = true;
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  deleteAppointment(id: number): void {
    if (confirm('Сигурни ли сте, че искате да изтриете тази среща?')) {
      this.adminAppointmentService.deleteAppointment(id).subscribe({
        next: () => this.loadAppointments(),
        error: (err) => alert('Грешка при изтриване!')
      });
    }
  }

  resetForm(): void {
    this.selectedAppt = { id: null, name: '', dateOfAppointment: '', requesterUsername: '', providerUsername: '', skillId: null };
    this.isEditMode = false;
  }
}
