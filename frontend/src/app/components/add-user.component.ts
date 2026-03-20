import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import {UserService} from '../services/user.service';


@Component({
  selector: 'app-add-user',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  template: `<div class="flex items-center justify-center min-h-screen bg-gray-100 p-4">
  <div class="bg-white rounded-2xl shadow-xl p-8 w-full max-w-lg">
    <div class="flex items-center justify-between mb-6 pb-4 border-b border-gray-200">
      <h1 class="text-3xl font-bold text-gray-800">Add New User/Skill</h1>
      <a routerLink="/admin" class="text-gray-500 hover:text-gray-700 text-2xl font-bold">&times;</a>
    </div>

    <form [formGroup]="addUserForm" (ngSubmit)="onSubmit()">
      <div class="mb-6">
        <label class="block text-gray-700 font-semibold mb-2">Title / Name</label>
        <input type="text" formControlName="name" class="w-full p-3 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-indigo-500">
      </div>

      <div class="mb-6">
        <label class="block text-gray-700 font-semibold mb-2">Category</label>
        <select formControlName="category" class="w-full p-3 rounded-lg border border-gray-300">
          <option value="IT">IT</option>
          <option value="Medicine">Medicine</option>
        </select>
      </div>

      <div class="mb-6">
        <label class="block text-gray-700 font-semibold mb-2">Image</label>
        <input type="file" (change)="onFileSelected($event)" class="w-full">
      </div>

      <div class="mb-6">
        <label class="block text-gray-700 font-semibold mb-2">Bio</label>
        <textarea formControlName="bio" class="w-full p-3 rounded-lg border border-gray-300 h-32"></textarea>
      </div>

      <div class="flex justify-center">
        <button type="submit" class="bg-indigo-600 hover:bg-indigo-700 text-white font-semibold py-3 px-8 rounded-lg shadow-md transition hover:scale-105">
          Add Entry
        </button>
      </div>
    </form>
  </div>
</div>
`
})
export class AddUserComponent {
  addUserForm: FormGroup;
  selectedFile: File | null = null;
  isSubmitting = false;

  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    private router: Router
  ) {
    // Съвпада с полетата в addUser.html
    this.addUserForm = this.fb.group({
      name: ['', Validators.required], // Title/Name
      category: ['IT', Validators.required],
      bio: ['', [Validators.required, Validators.minLength(10)]],
      // Добави други полета ако HTML-а ги изисква (email, username и т.н.)
      // Ако това е за добавяне на потребител:
      username: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]]
    });
  }

  onFileSelected(event: any) {
    if (event.target.files && event.target.files.length > 0) {
      this.selectedFile = event.target.files[0];
    }
  }

  onSubmit() {
    if (this.addUserForm.invalid || !this.selectedFile) {
      this.addUserForm.markAllAsTouched();
      if (!this.selectedFile) alert('Please upload an image.');
      return;
    }

    this.isSubmitting = true;
    const formData = new FormData();

    // Добавяме всички полета от формата
    Object.keys(this.addUserForm.value).forEach(key => {
      formData.append(key, this.addUserForm.get(key)?.value);
    });

    // Добавяме файла
    // Промени ключа, за да съвпада с Java DTO-то:
    formData.append('photoUrl', this.selectedFile);

    // Изпращаме към сървиса (увери се, че имаш метод за AddUser с FormData в UserService,
    // или използвай Auth метода ако е регистрация)
    this.userService.addUserAdmin(this.addUserForm.value).subscribe({
      next: () => {
        alert('Successfully added!');
        this.router.navigate(['/admin/users']);
      },
      error: (err) => {
        console.error(err);
        alert('Error adding entry.');
        this.isSubmitting = false;
      }
    });
  }
}
