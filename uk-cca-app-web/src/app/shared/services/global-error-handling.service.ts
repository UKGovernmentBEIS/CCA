import { HttpErrorResponse, HttpStatusCode } from '@angular/common/http';
import { ErrorHandler, Injectable, NgZone } from '@angular/core';
import { Router } from '@angular/router';

import { first, from, Observable, switchMap, throwError } from 'rxjs';

import { AuthService } from './auth.service';

@Injectable({ providedIn: 'root' })
export class GlobalErrorHandlingService implements ErrorHandler {
  excludedUrls = ['.+/account/+\\w+/header-info$'];

  constructor(
    private readonly authService: AuthService,
    private readonly router: Router,
    private readonly ngZone: NgZone,
  ) {}

  handleError(error: unknown): void {
    this.ngZone.run(() =>
      error instanceof HttpErrorResponse && error.status === HttpStatusCode.NotFound
        ? this.router.navigate(['/error', '404'], { state: { forceNavigation: true } })
        : this.router.navigate(['/error', '500'], { state: { forceNavigation: true }, skipLocationChange: true }),
    );

    console.error('ERROR', error);
  }

  handleHttpError(res: HttpErrorResponse): Observable<never> {
    console.log(res);
    const urlContained = this.excludedUrls.some((url) => new RegExp(url).test(res.url));

    if (!urlContained) {
      switch (res.status) {
        case HttpStatusCode.InternalServerError:
          return from(
            this.router.navigate(['/error', '500'], { state: { forceNavigation: true }, skipLocationChange: true }),
          ).pipe(switchMap(() => throwError(() => res)));

        case HttpStatusCode.Unauthorized:
          return from(this.authService.login()).pipe(switchMap(() => throwError(() => res)));

        case HttpStatusCode.Forbidden:
          return this.authService.loadUserState().pipe(
            first(),
            switchMap(() => from(this.router.navigate(['landing'], { state: { forceNavigation: true } }))),
            switchMap(() => throwError(() => res)),
          );

        default:
          return throwError(() => res);
      }
    } else {
      return throwError(() => res);
    }
  }
}
