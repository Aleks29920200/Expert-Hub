import { inject, PLATFORM_ID } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { isPlatformBrowser } from '@angular/common';
import {AuthService} from '../services/auth.service';


export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const platformId = inject(PLATFORM_ID);

  if (!isPlatformBrowser(platformId)) return true;

  const token = localStorage.getItem('token');
  const userRole = localStorage.getItem('role');
  const requiredRole = route.data['role']; // Вземаме ролята от конфигурацията на пътя

  // 1. Проверка за логнат потребител
  if (!token) {
    return router.createUrlTree(['/login']);
  }

  // 2. Проверка за роля (ако пътят изисква конкретна роля)
  if (requiredRole && userRole !== requiredRole) {
    console.warn('Достъп отказан! Изисква се роля:', requiredRole);
    return router.createUrlTree(['/home']); // Пращаме го вкъщи, ако не е админ
  }

  return true;
};


export const adminGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const platformId = inject(PLATFORM_ID);

  // 1. SSR Check
  if (!isPlatformBrowser(platformId)) {
    return router.createUrlTree(['/login']);
  }

  // 2. Browser Check
  if (authService.isLoggedIn() && authService.isAdmin()) {
    return true;
  } else {
    // Logged in but not Admin? Redirect to home
    return router.createUrlTree(['/home']);
  }
};
