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
      <h2 class="mb-4">🎯 Управление на умения (Skills)</h2>

      <div class="card shadow-sm mb-5">
        <div class="card-header" [ngClass]="isEditMode ? 'bg-warning text-dark' : 'bg-success text-white'">
          <h4 class="mb-0">{{ isEditMode ? '📝 Редактиране на умение' : '✨ Добавяне на ново умение' }}</h4>
        </div>
        <div class="card-body">
          <div class="row g-3">

            <div class="col-md-4">
              <label class="form-label">Име на умение <span class="text-danger">*</span></label>
              <input [(ngModel)]="selectedSkill.name" class="form-control" placeholder="напр. Java Programming">
            </div>

            <div class="col-md-4">
              <label class="form-label">Категория</label>
              <select [(ngModel)]="selectedSkill.category" class="form-select">
                <option value="" disabled>-- Избери категория --</option>
                <option value="IT">IT и Програмиране</option>
                <option value="DESIGN">Дизайн</option>
                <option value="MARKETING">Маркетинг</option>
                <option value="MUSIC">Музика</option>
                <option value="OTHER">Друго</option>
              </select>
            </div>

            <div class="col-md-4">
              <label class="form-label">Таг (Tag)</label>
              <input [(ngModel)]="selectedSkill.tag" class="form-control" placeholder="#java, #coding">
            </div>

            <div class="col-md-12">
              <label class="form-label">Описание</label>
              <textarea [(ngModel)]="selectedSkill.description" class="form-control" rows="2" placeholder="Кратко описание на умението..."></textarea>
            </div>

            <div class="col-12 mt-4 text-end">
              <button *ngIf="isEditMode" (click)="resetForm()" class="btn btn-outline-secondary me-2">Отказ</button>
              <button (click)="saveSkill()" class="btn" [ngClass]="isEditMode ? 'btn-warning' : 'btn-success'">
                {{ isEditMode ? 'Обнови умението' : 'Запази умението' }}
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
              <th>Име</th>
              <th>Категория</th>
              <th class="text-center">Действия</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let skill of skills">
              <td><span class="badge bg-secondary">#{{ skill.id }}</span></td>
              <td><strong>{{ skill.name }}</strong></td>
              <td><span class="badge bg-info text-dark">{{ skill.category || 'Няма' }}</span></td>
              <td class="text-center">
                <button (click)="editSkill(skill)" class="btn btn-sm btn-outline-primary me-2">✏️</button>
                <button (click)="deleteSkill(skill.id)" class="btn btn-sm btn-outline-danger">🗑️</button>
              </td>
            </tr>
            <tr *ngIf="skills.length === 0">
              <td colspan="4" class="text-center text-muted p-4">Няма намерени умения.</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  `,
  styles: [`.card { border: none; border-radius: 10px; }`]
})
export class AdminSkillsComponent implements OnInit {
  skills: any[] = [];

  selectedSkill: any = {
    id: null,
    name: '',
    description: '',
    category: '',
    tag: ''
  };

  isEditMode = false;

  constructor(private adminSkillService: AdminSkillService) {}

  ngOnInit(): void {
    this.loadSkills();
  }

  loadSkills(): void {
    this.adminSkillService.getSkills().subscribe({
      next: (data) => this.skills = data,
      error: (err) => console.error('Грешка при зареждане:', err)
    });
  }

  saveSkill(): void {
    if (!this.selectedSkill.name) {
      alert('Името на умението е задължително!');
      return;
    }

    if (this.isEditMode) {
      this.adminSkillService.updateSkill(this.selectedSkill.id, this.selectedSkill).subscribe({
        next: () => { this.loadSkills(); this.resetForm(); },
        error: (err) => alert('Грешка при обновяване!')
      });
    } else {
      this.adminSkillService.createSkill(this.selectedSkill).subscribe({
        next: () => { this.loadSkills(); this.resetForm(); },
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
    this.selectedSkill = { id: null, name: '', description: '', category: '', tag: '' };
    this.isEditMode = false;
  }
}
