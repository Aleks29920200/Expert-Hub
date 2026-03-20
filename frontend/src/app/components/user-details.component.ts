import { Component, OnInit } from '@angular/core';
import {CommonModule, NgOptimizedImage} from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import {UserDTO} from '../models/dtos.model';
import {UserService} from '../services/user.service';

@Component({
  selector: 'app-user-details',
  standalone: true,
  imports: [CommonModule, RouterLink, NgOptimizedImage],
  template: `
    <div class="flex items-center justify-center min-h-screen bg-gray-100 p-4" *ngIf="user">
      <div class="bg-white rounded-2xl shadow-xl p-8 w-full max-w-lg">
        <div class="flex justify-between items-center mb-6 pb-4 border-b border-gray-200">
          <h1 class="text-3xl font-bold text-gray-800">Details for {{ user.username }}</h1>
          <a routerLink="/admin/users" class="text-gray-500 hover:text-gray-700 text-2xl">&times;</a>
        </div>

        <div class="space-y-4">
          <div class="p-4 bg-gray-50 rounded-lg">
            <p class="text-sm font-medium text-gray-500">First Name</p>
            <p class="text-gray-900 font-semibold text-lg">{{ user.firstName }}</p>
          </div>
          <div class="p-4 bg-gray-50 rounded-lg">
            <p class="text-sm font-medium text-gray-500">Last Name</p>
            <p class="text-gray-900 font-semibold text-lg">{{ user.lastName }}</p>
          </div>
          <div class="p-4 bg-gray-50 rounded-lg">
            <p class="text-sm font-medium text-gray-500">Bio / Skill Description</p>
            <p class="text-gray-900 font-semibold text-lg">{{ user.bio || 'No bio available' }}</p>
          </div>
          <div class="p-4 bg-gray-50 rounded-lg" *ngIf="user.picture">
            <p class="text-sm font-medium text-gray-500 mb-2">Photo</p>
            <img [src]="'data:image/jpeg;base64,' + user.photoUrl"
                 class="w-full rounded-lg object-cover h-48" alt="User Photo">
          </div>
        </div>
      </div>
    </div>
  `
})
export class UserDetailsComponent implements OnInit {
  user: UserDTO | null = null;
  isLoading = true;

  constructor(
    private route: ActivatedRoute,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.userService.getUserDetailsAdmin(+id).subscribe({
        next: (data) => {
          this.user = data;
          this.isLoading = false;
        },
        error: (err) => {
          console.error('Error fetching details', err);
          this.isLoading = false;
        }
      });
    }
  }
}
