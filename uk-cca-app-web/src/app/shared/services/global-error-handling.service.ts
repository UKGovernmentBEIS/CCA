import { HttpErrorResponse, HttpStatusCode } from '@angular/common/http';
import { ErrorHandler, inject, Service } from '@angular/core';
import { Router } from '@angular/router';

import { first, from, Observable, switchMap, throwError } from 'rxjs';

import { logger } from '../utils/logger';
import { AuthService } from './auth.service';
import { StaleChunkRecoveryService } from './stale-chunk-recovery.service';

const EXCLUDED_URLS = ['.+/account/+\\w+/header-info$'];

const GLOBALLY_HANDLED_STATUSES: number[] = [
  HttpStatusCode.InternalServerError,
  HttpStatusCode.Unauthorized,
  HttpStatusCode.Forbidden,
];

/** Whether `handleHttpError` already navigates away or re-authenticates for this error. */
export function isGloballyHandledHttpError(error: unknown): error is HttpErrorResponse {
  return (
    error instanceof HttpErrorResponse &&
    GLOBALLY_HANDLED_STATUSES.includes(error.status) &&
    !EXCLUDED_URLS.some((url) => new RegExp(url).test(error.url))
  );
}

@Service()
export class GlobalErrorHandlingService implements ErrorHandler {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private readonly staleChunkRecoveryService = inject(StaleChunkRecoveryService);

  handleError(error: unknown): void {
    // A fresh copy of the page is loading, so an error page would only flash before it.
    if (this.staleChunkRecoveryService.recover(error)) return;

    error instanceof HttpErrorResponse && error.status === HttpStatusCode.NotFound
      ? this.router.navigate(['/error', '404'], { state: { forceNavigation: true } })
      : this.router.navigate(['/error', '500'], { state: { forceNavigation: true }, skipLocationChange: true });

    logger.error('ERROR', error);
  }

  handleHttpError(res: HttpErrorResponse): Observable<never> {
    if (!isGloballyHandledHttpError(res)) return throwError(() => res);

    switch (res.status) {
      case HttpStatusCode.InternalServerError:
        return from(
          this.router.navigate(['/error', '500'], { state: { forceNavigation: true }, skipLocationChange: true }),
        ).pipe(switchMap(() => throwError(() => res)));

      case HttpStatusCode.Unauthorized:
        return from(this.authService.reauthenticate()).pipe(switchMap(() => throwError(() => res)));

      case HttpStatusCode.Forbidden:
        return this.authService.loadUserState().pipe(
          first(),
          switchMap(() => from(this.router.navigate(['landing'], { state: { forceNavigation: true } }))),
          switchMap(() => throwError(() => res)),
        );

      default:
        return throwError(() => res);
    }
  }
}
