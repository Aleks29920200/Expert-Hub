import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import {UserService} from '../services/user.service';
import {UserDTO} from '../models/dtos.model';


@Component({
  selector: 'app-edit-user',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `<div class="flex items-center justify-center min-h-screen bg-gray-100 p-4">
    <div class="bg-white rounded-2xl shadow-xl p-8 w-full max-w-lg">
      <div class="flex justify-between items-center mb-6 pb-4 border-b border-gray-200">
        <h1 class="text-3xl font-bold text-gray-800">Edit User</h1>
      </div>

      <form [formGroup]="editForm" (ngSubmit)="onSubmit()">
        <div class="mb-6">
          <label class="block text-gray-700 font-semibold mb-2">Email</label>
          <input type="email" formControlName="email" class="w-full p-3 rounded-lg border border-gray-300">
        </div>

        <div class="flex justify-end space-x-2">
          <button type="submit" class="bg-indigo-600 hover:bg-indigo-700 text-white font-semibold py-2 px-6 rounded-lg">Save Changes</button>
        </div>
      </form>
    </div>
  </div>
  `
})
export class EditUserComponent implements OnInit {
  editForm: FormGroup;
  userId: number | null = null;
  selectedFile: File | null = null;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private userService: UserService
  ) {
    this.editForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      bio: [''],
      // Добави други полета, които могат да се редактират
    });
  }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.userId = +id;
      this.loadUserData(this.userId);
    }
  }

  loadUserData(id: number) {
    this.userService.getUserDetailsAdmin(id).subscribe({
      next: (user: UserDTO) => {
        // Попълваме формата с текущите данни
        this.editForm.patchValue({
          email: user.email,
          // bio: user.bio  <-- увери се, че DTO-то има bio
        });
      },
      error: (err) => console.error('Error loading user', err)
    });
  }

  onFileSelected(event: any) {
    if (event.target.files.length > 0) {
      this.selectedFile = event.target.files[0];
    }
  }

  onSubmit() {
    if (this.editForm.valid && this.userId) {
      const formData = new FormData();
      // Добавяме текстовите полета
      formData.append('email', this.editForm.get('email')?.value);
      formData.append('id', this.userId.toString());

      // Ако е избрана нова снимка, я добавяме (В Java полето е photoUrl)
      if (this.selectedFile) {
        formData.append('photoUrl', this.selectedFile);
      }

      this.userService.updateUser(this.userId, formData).subscribe({
        next: () => alert('User updated successfully'),
        error: (err) => console.error(err)
      });
    }
  }
}
