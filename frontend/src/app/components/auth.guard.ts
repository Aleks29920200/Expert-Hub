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

  // 1. Проверка за Server-Side Rendering (SSR)
  if (!isPlatformBrowser(platformId)) {
    return router.createUrlTree(['/login']);
  }

  // 2. Ако потребителят ИЗОБЩО не е логнат -> пращаме го да се логне
  if (!authService.isLoggedIn()) {
    return router.createUrlTree(['/login']);
  }

  // 3. Ако е логнат, но НЕ Е админ -> пращаме го в началната страница (или страница "Нямате достъп")
  if (!authService.isAdmin()) {
    return router.createUrlTree(['/admin']);
  }

  // 4. Ако е логнат И е админ -> пускаме го!
  return true;
};
