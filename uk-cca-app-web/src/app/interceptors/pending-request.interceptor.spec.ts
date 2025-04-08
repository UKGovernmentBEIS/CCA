import { HttpHandlerFn, HttpRequest, HttpResponse } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';

import { map, timer } from 'rxjs';

import { PendingRequestService } from '@netz/common/services';

import { PendingRequestInterceptor } from './pending-request.interceptor';

describe('PendingRequestInterceptor', () => {
  let pendingRequestService: PendingRequestService;

  beforeEach(async () => {
    pendingRequestService = TestBed.inject(PendingRequestService);
  });
  function intercept(req: HttpRequest<unknown>, next: HttpHandlerFn) {
    return TestBed.runInInjectionContext(() => PendingRequestInterceptor(req, next));
  }

  it('should track non-GET pending requests', () => {
    jest.useFakeTimers();

    const next = () => timer(1000).pipe(map(() => new HttpResponse()));
    intercept(new HttpRequest<unknown>('POST', 'http://localhost', {}), next).subscribe();

    expect(pendingRequestService.hasPendingRequests()).toBeTruthy();

    jest.advanceTimersByTime(3000);

    expect(pendingRequestService.hasPendingRequests()).toBeFalsy();
  });

  it('should not track GET requests', () => {
    const next = () => timer(1000).pipe(map(() => new HttpResponse()));

    intercept(new HttpRequest<unknown>('GET', 'http://localhost'), next).subscribe();

    expect(pendingRequestService.hasPendingRequests()).toBeFalsy();
  });
});
