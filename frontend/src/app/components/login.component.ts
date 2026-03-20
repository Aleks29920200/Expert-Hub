import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink
  ],
  template: `<div class="min-h-screen flex items-center justify-center p-4" style="background: linear-gradient(to right, #3b82f6, #8b5cf6);">
    <div class="bg-white rounded-xl shadow-2xl p-10 w-full max-w-md">
      <h2 class="text-3xl font-bold text-gray-800 mb-6 text-center">Login</h2>

      <form [formGroup]="loginForm" (ngSubmit)="onSubmit()">
        <div class="mb-4">
          <label for="username" class="block text-gray-700 text-sm font-bold mb-2">Username</label>
          <input type="text" id="username" formControlName="username" placeholder="Enter your username"
                 class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline">
        </div>

        <div class="mb-6">
          <label for="password" class="block text-gray-700 text-sm font-bold mb-2">Password</label>
          <input type="password" id="password" formControlName="password" placeholder="Enter your password"
                 class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline">
        </div>

        <div *ngIf="errorMessage" class="mb-4 text-red-500 text-sm text-center">
          {{ errorMessage }}
        </div>

        <div class="flex items-center justify-between mb-6">
          <button type="submit" [disabled]="loginForm.invalid"
                  class="bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline transition duration-300 w-full">
            Sign In
          </button>
        </div>
      </form>

      <div class="mt-4 grid grid-cols-2 gap-4">
        <a routerLink="/register/client" class="block w-full bg-gray-600 hover:bg-gray-700 text-white font-semibold py-2 px-4 rounded shadow transition transform hover:scale-105 text-center decoration-none">
          Register As User
        </a>
        <a routerLink="/register/expert" class="block w-full bg-indigo-600 hover:bg-indigo-700 text-white font-semibold py-2 px-4 rounded shadow transition transform hover:scale-105 text-center decoration-none">
          Register As Expert
        </a>
      </div>

      <div class="mt-6 text-center">
        <a href="http://localhost:8080/oauth2/authorization/facebook" class="inline-flex items-center justify-center w-full bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded transition duration-300 decoration-none">
          <i class="fab fa-facebook-f mr-2"></i> Login with Facebook
        </a>
      </div>
    </div>
  </div>
  `,
})
export class LoginComponent {
  loginForm: FormGroup;
  credentials = { username: '', password: '' };
  errorMessage: string = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(4)]],
      password: ['', [Validators.required, Validators.minLength(5)]]
    });
  }


  onSubmit(): void {
    console.log('--- СТАРТ НА ОПИТ ЗА ВХОД ---');
    console.log('1. Въведени данни до момента:', this.loginForm.value);

    // 1. Проверка за валидност
    if (this.loginForm.invalid) {
      console.warn('2. СТОП: Формата е НЕВАЛИДНА! Провери дължината на символите.');
      this.errorMessage = 'Моля, въведете поне 4 символа за име и 5 за парола.';
      return;
    }

    const loginData = this.loginForm.value;
    console.log('3. Формата е валидна! Пращаме към бекенда:', loginData);

    // 2. Изпращане на заявка
    this.authService.login(this.loginForm.value).subscribe({
      next: (response) => {
        console.log('Успешен вход с роля:', response.role);

        if (response.role === 'ADMIN') {
          this.router.navigate(['/admin']); // Или твоя път за админ
        } else {
          this.router.navigate(['/home']); // Път за обикновен потребител
        }
      },
      error: (err) => console.error('Грешка при вход', err)
    });
  }

}
