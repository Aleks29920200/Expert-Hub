// src/app/components/user-profile/user-profile.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import {UserService} from '../services/user.service';
import {AuthService} from '../services/auth.service';
import {BlockService} from '../services/block.service';
import {ReviewService} from '../services/review.service';


@Component({
  selector: 'app-user-profile',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule
  ],
  styles: [`
    .profile-container { padding: 20px; max-width: 800px; margin: 0 auto; }
    .header { text-align: center; margin-bottom: 20px; }
    .header img { width: 120px; height: 120px; border-radius: 50%; object-fit: cover; }
    .actions { display: flex; justify-content: center; gap: 10px; margin-bottom: 20px; }
    .btn-danger { background-color: #dc3545; color: white; border: none; padding: 8px 16px; border-radius: 4px; cursor: pointer; }
    .review-card { border-bottom: 1px solid #eee; padding: 10px 0; }
    .add-review { margin-top: 30px; }
    textarea { width: 100%; height: 100px; margin-bottom: 10px; }
  `],
  template: `
    <div *ngIf="user" class="profile-container">
      <div class="header">
        <div class="card">
          <img *ngIf="user.photoId"
               [src]="'http://localhost:8080/download/' + user.photoId"
               class="profile-img"
               alt="User Photo">

          <img *ngIf="!user.photoId"
               src="assets/default-avatar.png"
               class="profile-img"
               alt="Default Avatar">
        </div>
        <h2>{{ user.firstName }} {{ user.lastName }}</h2>
        <p class="text-muted">{{ user.username }}</p>
        <span class="badge" [ngClass]="user.activityStatus === 'ONLINE' ? 'bg-success' : 'bg-secondary'">
          {{ user.activityStatus || 'OFFLINE' }}
        </span>
      </div>

      <div class="actions">
        <button (click)="startChat()">Message</button>
        <button *ngIf="user.username !== currentUsername" (click)="blockUser()" class="btn-danger">
          Block User
        </button>
      </div>

      <div class="reviews">
        <h3>Reviews</h3>
        <div *ngFor="let review of user.reviews" class="review-card">
          <strong>{{ review.reviewerName || 'Anonymous' }}</strong> said:
          <p>{{ review.content }}</p>

          <div class="comments" *ngIf="review.comments && review.comments.length > 0">
            <div *ngFor="let comment of review.comments" style="margin-left: 20px; color: #666;">
              <small>{{ comment.authorName }}: {{ comment.text }}</small>
            </div>
          </div>
        </div>

        <div *ngIf="!user.reviews || user.reviews.length === 0">
          <p>No reviews yet.</p>
        </div>
      </div>

      <div class="add-review" *ngIf="user.username !== currentUsername">
        <h4>Leave a Review</h4>
        <textarea [(ngModel)]="newReviewContent" placeholder="Write a review..."></textarea>
        <button (click)="submitReview()" [disabled]="!newReviewContent">Submit</button>
      </div>
    </div>
  `
})
export class UserProfileComponent implements OnInit {
  user: any | null = null;
  newReviewContent = '';
  currentUsername: string | null = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private userService: UserService,
    private authService: AuthService,
    private blockService: BlockService,
    private reviewService:ReviewService
  ) {
    this.currentUsername = this.authService.getCurrentUsername();
  }

  ngOnInit() {
    const userIdParam = this.route.snapshot.paramMap.get('id');

    // SAFETY CHECK: Only load if it's a real number and not the word "undefined"
    if (userIdParam && userIdParam !== 'undefined') {
      this.loadUser(userIdParam);
    } else {
      console.warn('Invalid User ID in URL:', userIdParam);
      this.router.navigate(['/']); // Redirect to home so they aren't stuck on a broken page
    }
  }

  loadUser(username: string) {
    this.authService.getUserProfile(username).subscribe({
      next: (data) => {
        // UNWRAP BACKEND DATA: Grab 'userProfile' out of the Map your backend sends
        this.user = data.userProfile ? data.userProfile : data;
      },
      error: (err) => console.error('Error loading user', err)
    });
  }
  startChat() {
    if (!this.user) return;
    this.router.navigate(['/chat', { targetUser: this.user.username }]);
  }

  blockUser() {
    if (!this.user || !this.currentUsername) return;
    if (!confirm(`Block ${this.user.username}?`)) return;

    // ИЗПОЛЗВАМЕ blockService, а не userService!
    this.blockService.blockUser(this.currentUsername, this.user.username).subscribe({
      next: () => alert('User blocked'),
      error: (err) => alert('Error blocking user')
    });
  }

  submitReview() {
    const targetId = this.user.userId || this.user.id;
    if (!this.user || !this.newReviewContent || !targetId || !this.currentUsername) return;

    // ИЗПОЛЗВАМЕ reviewService с 3 параметъра!
    this.reviewService.addReview(this.currentUsername, targetId, this.newReviewContent).subscribe({
      next: () => {
        alert('Review added!');
        this.loadUser(this.user.username); // Презареждаме профила
      },
      error: (err) => alert('Error adding review')
    });
  }
  }
