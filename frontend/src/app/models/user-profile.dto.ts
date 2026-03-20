import { User } from './user.model';
import { Skill } from './skill.model';

export interface Review {
  id: number;
  // ПРОМЕНЕНО: Трябва да съвпада с ReviewDTO.java в Spring Boot
  authorUsername: string;
  content: string;
  created?: string;
  replies?: any[];
}

export interface UserProfile extends User {
  skill?: Skill;
  reviews?: Review[];
}
