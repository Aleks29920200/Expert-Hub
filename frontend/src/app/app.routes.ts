import { Routes } from '@angular/router';
import { HomeComponent } from './components/home.component';
import { LoginComponent } from './components/login.component';
import { RegisterClientComponent } from './components/register-client.component';
import { RegisterExpertComponent } from './components/register-expert.component';
import { UserListComponent } from './components/user-list.component';
import { AddUserComponent } from './components/add-user.component';
import { EditUserComponent } from './components/edit-user.component';
import { UserDetailsComponent } from './components/user-details.component';
import { ManageSkillsComponent } from './components/manage-skills.component';
import { AddSkillComponent } from './components/add-skill.component';
import { EditSkillComponent } from './components/edit-skill.component';
import { SkillDetailsComponent } from './components/skill-details.component';
import { ProfileComponent } from './components/profile.component';
import { ChatComponent } from './components/chat.component';
import { adminGuard, authGuard } from './components/auth.guard';
import { AppointmentListComponent } from './components/appointment-list.component';
import { IndexComponent } from './components/index.component';
import {AdminPanelComponent} from './components/admin-home.component';
import {AdminDashboardComponent} from './components/admin-dashboard.component';

export const routes: Routes = [
  // --- Public Routes (Достъпни за всички) ---
  { path: '', component: IndexComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register/client', component: RegisterClientComponent },
  { path: 'register/expert', component: RegisterExpertComponent },

  // --- Protected Routes (Само за логнати потребители) ---
  {
    path: 'home',
    component: HomeComponent,
    canActivate: [authGuard] // <--- ДОБАВЕНО ТУК
  },

  // --- Admin Routes ---
  {
    path: 'admin',
    component: AdminDashboardComponent,
    canActivate: [adminGuard],
    children: [
      { path: 'users', component: UserListComponent },
      { path: 'add-user', component: AddUserComponent },
      { path: 'edit-user/:id', component: EditUserComponent },
      { path: 'user-details/:id', component: UserDetailsComponent },
      { path: 'manage-skills', component: ManageSkillsComponent },
      { path: 'add-skill', component: AddSkillComponent },
      { path: 'edit-skill/:id', component: EditSkillComponent },
      { path: 'skill-details/:id', component: SkillDetailsComponent },
    ]
  },

  // --- User Features (Protected) ---
  {
    path: 'profile/:username',
    component: ProfileComponent,
    canActivate: [authGuard] // <--- Защитаваме и профила
  },
  {
    path: 'chat/:username',
    component: ChatComponent,
    canActivate: [authGuard] // <--- Защитаваме и чата
  },
  {
    path: 'chat',
    component: ChatComponent,
    canActivate: [authGuard]
  },
  {
    path: 'calendar',
    component: AppointmentListComponent,
    canActivate: [authGuard] // <--- Защитаваме и календара
  },

  // Fallback (Ако някой въведе грешен URL, го пращаме на началната Index страница)
  { path: '**', redirectTo: '' }
];
