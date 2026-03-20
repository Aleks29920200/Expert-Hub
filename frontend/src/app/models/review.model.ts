// src/app/models/review.model.ts
import { User } from './user.model';
import { Comment } from './comment.model';

export interface Review {
  id: number;
  reviewingUser: User; // The author of the review
  reviewedUser: User;  // The receiver of the review
  reviewText: string;
  rating: number;
  dateOfReview: string; // LocalDate -> ISO String
  comments: Comment[];
  replies: any[];
}
