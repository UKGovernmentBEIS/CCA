import { HttpHandlerFn, HttpRequest, HttpResponse } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';

import { map, timer } from 'rxjs';

import { mockClass } from '../../../testing';
import { GlobalErrorHandlingService } from '../services/global-error-handling.service';
import { HttpErrorInterceptor } from './http-error.interceptor';

describe(`HttpErrorInterceptor`, () => {
  const globalErrorHandlingService = mockClass(GlobalErrorHandlingService);

  function intercept(req: HttpRequest<unknown>, next: HttpHandlerFn) {
    return TestBed.runInInjectionContext(() => HttpErrorInterceptor(req, next));
  }
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        { provide: GlobalErrorHandlingService, useValue: globalErrorHandlingService },
      ],
    });
  });

  it('should be created', () => {
    const next = () => timer(1000).pipe(map(() => new HttpResponse()));
    const req = new HttpRequest<unknown>('POST', 'http://localhost', {});
    expect(intercept(req, next)).toBeDefined();
  });
});
