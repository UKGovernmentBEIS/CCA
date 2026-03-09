import type { HttpEvent, HttpHandlerFn, HttpRequest } from '@angular/common/http';
import { inject } from '@angular/core';

import { from, switchMap } from 'rxjs';

import { KeycloakService } from '@shared/services';
import type { Observable } from 'rxjs';

import { environment } from 'src/environments/environment';

export function KeycloakBearerInterceptor(
  request: HttpRequest<unknown>,
  next: HttpHandlerFn,
): Observable<HttpEvent<unknown>> {
  const keycloakService = inject(KeycloakService);

  if (!shouldAddToken(request, keycloakService.isAuthenticated)) {
    return next(request);
  }

  const minValidity = environment.timeoutBanner.timeOffsetSeconds;

  return from(keycloakService.updateToken(minValidity)).pipe(
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
  return !request.url.includes('api.pwnedpasswords.com') && isAuthenticated;
}
