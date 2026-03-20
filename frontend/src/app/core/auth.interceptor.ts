import { HttpInterceptorFn } from '@angular/common/http';
import { inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  // 1. Инжектираме Platform ID, за да знаем дали сме в браузъра
  const platformId = inject(PLATFORM_ID);

  // 2. Достъпваме localStorage само ако сме в браузъра
  if (isPlatformBrowser(platformId)) {
    const token = localStorage.getItem('token');

    if (token) {
      // Клонираме заявката и добавяме Токена и Credentials
      const cloned = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        },
        withCredentials: true // <--- ВАЖНО ЗА CORS И СЕСИИТЕ В SPRING SECURITY
      });
      return next(cloned);
    }
  }

  // 3. Ако няма токен или сме на сървъра, поне подаваме withCredentials
  const clonedWithoutToken = req.clone({
    withCredentials: true // <--- ДОБАВЕНО ТУК СЪЩО
  });

  return next(clonedWithoutToken);
};
