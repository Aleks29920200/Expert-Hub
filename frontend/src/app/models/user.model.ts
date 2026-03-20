import { Review } from './review.model';
import { Appointment } from './appointment.model';
import { Role } from './role.model';
import { Skill } from './skill.model';
import { UserStatus } from './enums.model';

export interface User {
  id: number;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  address: string;
  picture: string;
  bio?: string;

  review: Review[];
  appointments: Appointment[];
  activity: UserStatus;
  role: Role[];

  // ПРОМЕНЕНО: Вече очакваме стринг (Base64 или URL), а не обект FileEntity
  photoUrl: string;

  // ИЗТРИТИ са blockedUsers и createdBy
}
