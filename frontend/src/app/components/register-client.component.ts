import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-register-client',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `<div class="bg-light min-vh-100 d-flex justify-content-center align-items-center">
    <div class="card shadow-lg p-4" style="width: 100%; max-width: 500px;">

      <h3 class="text-center mb-4">Create Account (User)</h3>

      <form [formGroup]="registerForm" (ngSubmit)="onSubmit()">
        <div class="mb-3">
          <label class="form-label">Username</label>
          <input type="text" formControlName="username" class="form-control" placeholder="Enter username">
        </div>
        <div class="mb-3">
          <label class="form-label">First Name</label>
          <input type="text" formControlName="firstName" class="form-control" placeholder="Enter first name">
        </div>
        <div class="mb-3">
          <label class="form-label">Last Name</label>
          <input type="text" formControlName="lastName" class="form-control" placeholder="Enter last name">
        </div>
        <div class="mb-3">
          <label class="form-label">Email</label>
          <input type="email" formControlName="email" class="form-control" placeholder="name@example.com">
        </div>
        <div class="mb-3">
          <label class="form-label">Password</label>
          <input type="password" formControlName="password" class="form-control" placeholder="********">
        </div>

        <div class="mb-3">
          <label class="form-label">Profile Photo</label>
          <input type="file" (change)="onFileSelected($event)" class="form-control" required>
        </div>

        <div class="d-grid">
          <button type="submit" [disabled]="registerForm.invalid || !selectedFile" class="btn btn-primary">Register</button>
        </div>
      </form>
    </div>
  </div>
  `
})
export class RegisterClientComponent {
  registerForm: FormGroup;
  selectedFile: File | null = null;

  constructor(private fb: FormBuilder, private authService: AuthService, private router: Router) {
    this.registerForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(5)]],
      firstName: ['', [Validators.required, Validators.minLength(2)]],
      lastName: ['', [Validators.required, Validators.minLength(2)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(5)]]
    });
  }

  onFileSelected(event: any) {
    this.selectedFile = event.target.files[0];
  }

  onSubmit() {
    if (this.registerForm.valid && this.selectedFile) {
      const formData = new FormData();
      Object.keys(this.registerForm.value).forEach(key => {
        formData.append(key, this.registerForm.get(key)?.value);
      });

      // ✅ FIX: Changed 'photoUrl' to 'img' to match backend expectation
      // Трябва да е 'photoUrl', за да съвпадне с Java класа RegisterAsClientDto!
      formData.append('photoUrl', this.selectedFile);

      this.authService.registerClient(formData).subscribe({
        next: () => {
          alert('Registration successful! Please login.');
          this.router.navigate(['/login']);
        },
        error: (err) => {
          console.error('Registration failed', err);
          alert('Registration failed: ' + (err.error?.message || 'Server error'));
        }
      });
    }
  }
}
