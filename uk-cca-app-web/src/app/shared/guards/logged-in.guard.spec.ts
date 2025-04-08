import { TestBed } from '@angular/core/testing';
import { UrlTree } from '@angular/router';

import { LoggedInGuard } from './logged-in.guard';

describe('LoggedInGuard', () => {
  let guard: boolean | UrlTree;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    guard = TestBed.runInInjectionContext(() => LoggedInGuard());
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });
});
