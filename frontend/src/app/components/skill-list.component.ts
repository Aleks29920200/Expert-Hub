import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {Skill} from '../models/skill.model';
import {SkillService} from '../services/skill.service';


@Component({
  imports: [CommonModule],
  selector: 'app-skill-list',
  template: `
    <h2>Manage Skills</h2>

    <div *ngFor="let skill of skills" class="skill-card">
      <h3>{{ skill.name }}</h3>
      <p>Category: {{ skill.category }}</p>
      <button (click)="deleteSkill(skill.id)">Delete</button>
      <button (click)="editSkill(skill)">Edit</button>
    </div>

    `
})
export class SkillListComponent implements OnInit {
  skills: Skill[] = [];

  constructor(private skillService: SkillService) {}

  ngOnInit() {

    this.skillService.getAllSkills().subscribe({
      next: (data) => this.skills = data,
      error: (err) => console.error('Failed to load skills', err)
    });
  }

  deleteSkill(id: number | undefined) {
    if(!id) return;
    if(confirm('Are you sure?')) {
      this.skillService.deleteSkill(id).subscribe(() => {
        // Remove from UI array
        this.skills = this.skills.filter(s => s.id !== id);
      });
    }
  }

  editSkill(skill: Skill) {
    // Logic to open a modal or navigate to edit page
    console.log('Editing', skill);
  }
}
