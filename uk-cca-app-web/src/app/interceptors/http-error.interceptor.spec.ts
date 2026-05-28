import { HttpEvent, HttpHandlerFn, HttpRequest, HttpResponse } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';

import { of } from 'rxjs';

import { mockClass } from '@netz/common/testing';
import { GlobalErrorHandlingService } from '@shared/services';

import { HttpErrorInterceptor } from './http-error.interceptor';

describe(`HttpErrorInterceptor`, () => {
  const globalErrorHandlingService = mockClass(GlobalErrorHandlingService);

  function intercept(req: HttpRequest<unknown>, next: HttpHandlerFn) {
    return TestBed.runInInjectionContext(() => HttpErrorInterceptor(req, next));
  }

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [{ provide: GlobalErrorHandlingService, useValue: globalErrorHandlingService }],
    });
  });

  it('should be created', () => {
    const next = vi.fn().mockReturnValue(of(new HttpResponse()));
    const req = new HttpRequest<unknown>('POST', 'http://localhost', {});
    let result: HttpEvent<unknown> | undefined;

    intercept(req, next).subscribe((res) => {
      result = res;
    });

    expect(result).toBeDefined();
    expect(next).toHaveBeenCalled();
  });
});
