// src/app/models/dtos.model.ts

import {Review} from './review.model';
import {Appointment} from './appointment.model';
import {UserStatus} from './enums.model';
import {Role} from './role.model';
import {FileEntity} from './file-entity.model';
import {Skill} from './skill.model';

export interface AddAppointmentRequest {
  name: string;
  dateOfAppointment: Date;
  requesterUsername: string;
  skillId: number;// ISO Date string
  // Add other fields from your Java AddAppointment DTO here
}

export interface CreateCommentRequest {
  userId: number;
  reviewId: number;
  text: string;
}


export interface SkillDto {
  id?: number;
  name: string;
  category: string;
  tag?: string;
  description?: string;
}

export interface SearchDto {
  info: string; // The category or keyword to search
}

export interface ReviewDto {
  id?: number;
  reviewerUsername: string;
  revieweeId: number; // or revieweeUsername depending on controller
  content: string;
  rating: number;
  replies: any[];
}

export interface MessageDto {
  id?: number;
  sender: string; // username
  receiver: string; // username
  content: string;
  timestamp?: string;
  messageType?: string;// ISO string
}

export interface UserRegistrationDto {
  username: string;
  email: string;
  password: string;
  // Add specific fields for Expert/Client if they differ
}
export interface UserDTO {
  id: number;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  address: string;
  picture: string; // Simple URL string if used
  bio?: string;
  review: Review[];
  appointments: Appointment[];
  activity: UserStatus;
  role: Role[];

  // In Java this was named 'photoUrl' but typed as 'FileEntity'
   photoUrl?: {
    filedata: string;
    contentType: "image/png";
    fileName: string;

  };
  hasPaid?: boolean; // <-- ДОБАВЯМЕ ТОВА ПОЛЕ
  defaultOfferId?: number;


  createdBy: Skill[];

}
