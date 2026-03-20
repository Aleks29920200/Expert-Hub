import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-index',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="container-fluid bg-light min-vh-100 d-flex flex-column justify-content-center align-items-center" style="padding-top: 60px;">
      <div class="row w-100 justify-content-center text-center px-3">
        <div class="col-md-8 col-lg-6">
          <div class="mb-4">
            <i class="fas fa-handshake fa-5x text-primary shadow-sm rounded-circle p-4 bg-white"></i>
          </div>
          <h1 class="display-4 fw-bold text-dark mb-3">Welcome to Skill-Sharing Network</h1>
          <p class="lead text-secondary mb-5">
            The easiest way to exchange expertise, learn new skills, and connect with top professionals.
            Book your first appointment today!
          </p>
          <div class="d-flex flex-column flex-sm-row justify-content-center gap-3">
            <a routerLink="/login" class="btn btn-primary btn-lg px-5 rounded-pill shadow-sm">Login</a>
            <a routerLink="/register/client" class="btn btn-outline-dark btn-lg px-4 rounded-pill shadow-sm">Register as Client</a>
            <a routerLink="/register/expert" class="btn btn-dark btn-lg px-4 rounded-pill shadow-sm">Register as Expert</a>
          </div>
        </div>
      </div>

      <div class="row mt-5 pt-5 w-100 justify-content-center text-center px-3">
        <div class="col-md-3 mb-4"><div class="bg-white rounded-4 shadow-sm p-4 h-100 border border-light"><i class="fas fa-search fa-2x text-primary mb-3"></i><h5 class="fw-bold">1. Find a Skill</h5></div></div>
        <div class="col-md-3 mb-4"><div class="bg-white rounded-4 shadow-sm p-4 h-100 border border-light"><i class="far fa-calendar-check fa-2x text-primary mb-3"></i><h5 class="fw-bold">2. Book Session</h5></div></div>
        <div class="col-md-3 mb-4"><div class="bg-white rounded-4 shadow-sm p-4 h-100 border border-light"><i class="fas fa-graduation-cap fa-2x text-primary mb-3"></i><h5 class="fw-bold">3. Learn & Grow</h5></div></div>
      </div>
    </div>
  `
})
export class IndexComponent {}
