import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {AdminSkillService} from '../services/admin-skill.service';

@Component({
  selector: 'app-admin-skills',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="container mt-4">
      <h2 class="mb-4">🎯 Административен панел - Умения</h2>

      <div class="card shadow-sm mb-5">
        <div class="card-header" [ngClass]="isEditMode ? 'bg-warning text-dark' : 'bg-info text-white'">
          <h4 class="mb-0">{{ isEditMode ? '📝 Редактиране на умение' : '✨ Добавяне на ново умение' }}</h4>
        </div>
        <div class="card-body">
          <div class="row g-3">
            <div class="col-md-4">
              <label class="form-label">Име на умението</label>
              <input [(ngModel)]="selectedSkill.name" class="form-control" placeholder="напр. Java, Angular, Photoshop...">
            </div>
            <div class="col-md-6">
              <label class="form-label">Описание</label>
              <input [(ngModel)]="selectedSkill.description" class="form-control" placeholder="Кратко описание на умението...">
            </div>
            <div class="col-md-2 d-flex align-items-end">
              <button (click)="saveSkill()" class="btn w-100" [ngClass]="isEditMode ? 'btn-warning' : 'btn-info text-white'">
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
              <th width="10%">ID</th>
              <th width="25%">Умение</th>
              <th width="45%">Описание</th>
              <th width="20%" class="text-center">Действия</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let skill of skills">
              <td><span class="badge bg-secondary">#{{ skill.id }}</span></td>
              <td><strong>{{ skill.name }}</strong></td>
              <td class="text-muted">{{ skill.description }}</td>
              <td class="text-center">
                <button (click)="editSkill(skill)" class="btn btn-sm btn-outline-primary me-2" title="Редактирай">
                  ✏️
                </button>
                <button (click)="deleteSkill(skill.id)" class="btn btn-sm btn-outline-danger" title="Изтрий">
                  🗑️
                </button>
              </td>
            </tr>
            <tr *ngIf="skills.length === 0">
              <td colspan="4" class="text-center text-muted p-4">Няма добавени умения в базата.</td>
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
export class AdminSkillsComponent implements OnInit {
  skills: any[] = [];
  selectedSkill: any = { name: '', description: '' };
  isEditMode = false;

  constructor(private adminSkillService: AdminSkillService) {}

  ngOnInit(): void {
    this.loadSkills();
  }

  loadSkills(): void {
    this.adminSkillService.getSkills().subscribe({
      next: (data) => this.skills = data,
      error: (err) => console.error('Грешка при зареждане на умения:', err)
    });
  }

  saveSkill(): void {
    if (!this.selectedSkill.name) {
      alert('Моля, въведете име на умението!');
      return;
    }

    if (this.isEditMode) {
      this.adminSkillService.updateSkill(this.selectedSkill.id, this.selectedSkill).subscribe({
        next: () => {
          this.loadSkills();
          this.resetForm();
        },
        error: (err) => alert('Грешка при обновяване!')
      });
    } else {
      this.adminSkillService.createSkill(this.selectedSkill).subscribe({
        next: () => {
          this.loadSkills();
          this.resetForm();
        },
        error: (err) => alert('Грешка при създаване!')
      });
    }
  }

  editSkill(skill: any): void {
    this.selectedSkill = { ...skill };
    this.isEditMode = true;
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  deleteSkill(id: number): void {
    if (confirm('Сигурни ли сте, че искате да изтриете това умение?')) {
      this.adminSkillService.deleteSkill(id).subscribe({
        next: () => this.loadSkills(),
        error: (err) => alert('Грешка при изтриване!')
      });
    }
  }

  resetForm(): void {
    this.selectedSkill = { name: '', description: '' };
    this.isEditMode = false;
  }
}
