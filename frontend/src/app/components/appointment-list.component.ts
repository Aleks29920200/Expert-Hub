import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http'; // Заявки към бекенда за списъците
import { AppointmentDTO, AppointmentService } from '../services/appointment.service';

@Component({
  selector: 'app-appointment-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="p-6 bg-white rounded-lg shadow-md">
      <h2 class="text-2xl font-bold mb-4">Manage Appointments</h2>

      <div class="mb-6 flex flex-wrap gap-2">
        <input [(ngModel)]="newTitle" placeholder="Title (e.g. Consult)" class="border p-2 rounded flex-1 min-w-[150px]"/>

        <select [(ngModel)]="clientUsername" class="border p-2 rounded flex-1 min-w-[150px]">
          <option value="" disabled selected>Select Client...</option>
          <option *ngFor="let client of clients" [value]="client.username">
            {{ client.username }} ({{ client.firstName }} {{ client.lastName }})
          </option>
        </select>

        <select [(ngModel)]="skillId" class="border p-2 rounded flex-1 min-w-[150px]">
          <option [ngValue]="null" disabled selected>Select Category...</option>
          <option *ngFor="let skill of skills" [ngValue]="skill.id">
            {{ skill.category }} - {{ skill.name }}
          </option>
        </select>

        <input type="datetime-local" [(ngModel)]="newDate" class="border p-2 rounded"/>
        <button (click)="addEvent()" class="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700">Add</button>
      </div>

      <ul class="space-y-2">
        <li *ngFor="let apt of appointments" class="flex justify-between items-center p-3 bg-gray-50 border rounded">
          <div>
            <strong class="text-lg">{{ apt.name }}</strong>
            <span class="text-sm text-gray-500 ml-2">({{ apt.dateOfAppointment | date:'medium' }})</span>
            <div class="text-sm text-gray-600">Client: {{ apt.requesterUsername }}</div>
          </div>
          <button (click)="removeEvent(apt.id)" class="text-red-500 hover:text-red-700">
            <i class="fas fa-trash"></i> Remove
          </button>
        </li>
      </ul>

      <div *ngIf="appointments.length === 0" class="text-gray-500 mt-4">
        No appointments scheduled yet.
      </div>
    </div>
  `
})
export class AppointmentListComponent implements OnInit {
  appointments: any[] = [];

  newTitle: string = '';
  clientUsername: string = '';
  skillId: number | null = null;
  newDate: string = '';

  // Списъци за падащите менюта
  clients: any[] = [];
  skills: any[] = [];

  constructor(
    private appointmentService: AppointmentService,
    private http: HttpClient // Инжектираме HttpClient
  ) {}

  ngOnInit() {
    this.loadEvents();
    this.loadClients(); // Зареждаме клиентите при отваряне
    this.loadSkills();  // Зареждаме уменията при отваряне
  }

  // --- Зареждане на данни за падащите менюта ---

  loadClients() {
    // ВАЖНО: Увери се, че този URL съвпада с твоя бекенд ендпойнт за клиенти!
    this.http.get<any[]>('http://localhost:8080/api/users/clients').subscribe({
      next: (data) => this.clients = data,
      error: (err) => console.error('Error loading clients:', err)
    });
  }

  loadSkills() {
    this.http.get<any[]>('http://localhost:8080/api/admin/skills/all').subscribe({
      next: (data) => {
        this.skills = data;
        // ДОБАВЯМЕ ТОВА ЗА ДА ВИДИМ ДАННИТЕ:
        console.log("УМЕНИЯ ОТ БЕКЕНДА:", data);
      },
      error: (err) => console.error('Error loading skills:', err)
    });
  }

  // --- Съществуваща логика ---

  loadEvents() {
    this.appointmentService.getAppointments().subscribe({
      next: (data) => this.appointments = data,
      error: (err) => console.error('Error loading appointments', err)
    });
  }

  addEvent() {
    console.log("1. Title:", this.newTitle);
    console.log("2. Date:", this.newDate);
    console.log("3. Client:", this.clientUsername);
    console.log("4. Skill ID:", this.skillId);
    if (!this.newTitle || !this.newDate || !this.clientUsername || !this.skillId) {
      alert("Please fill all fields!");
      return;
    }

    const appointment: AppointmentDTO = {
      dateOfAppointment: new Date(this.newDate),
      name: this.newTitle,
      requesterUsername: this.clientUsername,
      // Взимаме името на логнатия експерт. Ако го пазиш под друго име в localStorage (напр. 'username'), смени го тук:
      providerUsername: localStorage.getItem('currentUser') || 'ivan4o', // <-- ДОБАВИ ТОВА
      skillId: Number(this.skillId)
    };

    console.log("ПРАЩАМЕ ТОВА КЪМ БЕКЕНДА:", appointment);
    this.appointmentService.addAppointment(appointment).subscribe({
      next: () => {
        this.loadEvents();
        this.newTitle = '';
        this.newDate = '';
        this.clientUsername = '';
        this.skillId = null;
      },
      error: (err) => {
        console.error('Error adding appointment', err);
        alert('Failed to add appointment.');
      }
    });
  }

  removeEvent(appointmentId: number) {
    if (!appointmentId) return;
    this.appointmentService.removeAppointment(appointmentId).subscribe({
      next: () => this.loadEvents(),
      error: (err) => console.error('Error removing appointment', err)
    });
  }
}
