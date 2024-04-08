import { HttpEvent, HttpHandlerFn, HttpRequest } from '@angular/common/http';
import { inject } from '@angular/core';

import { Observable } from 'rxjs';

import { PendingRequestService } from '../guards/pending-request.service';

export function PendingRequestInterceptor(request: HttpRequest<unknown>, next: HttpHandlerFn): Observable<HttpEvent<unknown>> {
  const pendingRequest = inject(PendingRequestService);
  if (request.method !== 'GET') {
    return next(request).pipe(pendingRequest.trackRequest());
  } else {
    return next(request);
  }
}
