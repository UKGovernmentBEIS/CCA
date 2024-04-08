import { Injectable } from '@angular/core';
import { Router } from '@angular/router';

import { BehaviorSubject, from, ignoreElements, Observable } from 'rxjs';

import { BusinessError } from './business-error';

@Injectable({ providedIn: 'root' })
export class BusinessErrorService {
  private readonly errorSubject = new BehaviorSubject<BusinessError>(null);
  readonly error$ = this.errorSubject.asObservable();

  constructor(private readonly router: Router) {}

  showError(error: BusinessError): Observable<boolean> {
    this.errorSubject.next(error);

    return from(this.router.navigate(['/error/business'], { skipLocationChange: true })).pipe(ignoreElements());
  }

  // this method is used to bypass the pendingRequestGuard when the error has to be caught before the
  // PendingRequestService tracks the request
  showErrorForceNavigation(error: BusinessError): Observable<boolean> {
    this.errorSubject.next(error);

    return from(
      this.router.navigate(['/error/business'], { state: { forceNavigation: true }, skipLocationChange: true }),
    ).pipe(ignoreElements());
  }

  clear(): void {
    this.errorSubject.next(null);
  }
}
