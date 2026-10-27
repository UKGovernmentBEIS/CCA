import type { HttpEvent, HttpHandlerFn, HttpRequest } from '@angular/common/http';
import { inject } from '@angular/core';

import { catchError, concatWith, EMPTY, from, ignoreElements, switchMap, throwError } from 'rxjs';

import { AuthService, KeycloakService } from '@shared/services';
import type { Observable } from 'rxjs';

import { environment } from 'src/environments/environment';

export function KeycloakBearerInterceptor(
  request: HttpRequest<unknown>,
  next: HttpHandlerFn,
): Observable<HttpEvent<unknown>> {
  const keycloakService = inject(KeycloakService);
  const authService = inject(AuthService);

  if (!shouldAddToken(request, keycloakService.isAuthenticated)) {
    return next(request);
  }

  const minValidity = environment.timeoutBanner.timeOffsetSeconds;

  return from(keycloakService.updateToken(minValidity)).pipe(
    catchError((error) =>
      from(authService.reauthenticate()).pipe(
        catchError(() => EMPTY),
        ignoreElements(),
        concatWith(throwError(() => error)),
      ),
    ),
    switchMap(() => {
      const token = keycloakService.token;
      if (token) {
        request = request.clone({
          setHeaders: {
            Authorization: `Bearer ${token}`,
          },
        });
      }
      return next(request);
    }),
  );
}

function shouldAddToken(request: HttpRequest<unknown>, isAuthenticated: boolean): boolean {
  return isAuthenticated && isSameOriginOrApiOrigin(request.url);
}

function isSameOriginOrApiOrigin(url: string): boolean {
  try {
    const requestOrigin = new URL(url, location.origin).origin;
    const apiOrigin = new URL(environment.apiOptions.baseUrl, location.origin).origin;
    return requestOrigin === location.origin || requestOrigin === apiOrigin;
  } catch {
    return false;
  }
}
