import { TestBed } from '@angular/core/testing';
import { UrlTree } from '@angular/router';

import { Observable } from 'rxjs';

import { LoggedInGuard } from './logged-in.guard';

describe('LoggedInGuard', () => {
  let guard: Observable<boolean | UrlTree>;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    guard = TestBed.runInInjectionContext(() => LoggedInGuard());
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });
});
