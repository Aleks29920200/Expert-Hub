import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import {User} from '../models/user.model';
import {UserService} from '../services/user.service';
import {SearchService} from '../services/search.service';
import {UserDTO} from '../models/dtos.model';


@Component({
  selector: 'app-search',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="container mx-auto p-4">
      <h2 class="text-2xl font-bold mb-4">Search Results</h2>

      <div *ngIf="isLoading" class="text-center">Loading...</div>

      <div *ngIf="!isLoading && users.length === 0" class="text-gray-500">
        No users found matching your criteria.
      </div>

      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        <div *ngFor="let user of users" class="bg-white p-6 rounded-lg shadow-lg border border-gray-200">
          <h2 class="text-xl font-bold text-gray-800">{{ user.firstName }} {{ user.lastName }}</h2>
          <p class="text-gray-600">Username: {{ user.username }}</p>
          <p class="text-gray-600 text-sm">{{ user.email }}</p>
        </div>
      </div>
    </div>
  `
})
export class SearchComponent implements OnInit {
  users: UserDTO[] = [];
  isLoading = true;

  constructor(private route: ActivatedRoute, private searchService: SearchService) {}

  ngOnInit() {
    // Assuming the route is something like /search/:query
    this.route.paramMap.subscribe(params => {
      const query = params.get('query');
      if (query) {
        this.performSearch(query);
      } else {
        this.isLoading = false;
      }
    });
  }

  performSearch(query: string) {
    this.searchService.searchByKeyword(query).subscribe({
      next: (data) => {
        this.users = data;
        this.isLoading = false;
      },
      error: () => this.isLoading = false
    });
  }
}
