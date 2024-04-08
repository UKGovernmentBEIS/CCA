import { TestBed } from '@angular/core/testing';
import { Router, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { lastValueFrom, Observable, of } from 'rxjs';

import { AuthStore } from '@core/store/auth';
import { MockType } from '@testing';

import { AuthService } from '../services/auth.service';
import { NonAuthGuard } from './non-auth.guard';

describe('NonAuthGuard', () => {
  let guard: Observable<true | UrlTree>;
  let router: Router;
  let authStore: AuthStore;

  const authService: MockType<AuthService> = {
    checkUser: jest.fn(() => of(undefined)),
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [{ provide: AuthService, useValue: authService }],
    });

    authStore = TestBed.inject(AuthStore);
    guard = TestBed.runInInjectionContext(() => NonAuthGuard());
    router = TestBed.inject(Router);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should allow access if the user is not logged in', () => {
    authStore.setIsLoggedIn(false);

    return expect(lastValueFrom(guard)).resolves.toEqual(true);
  });

  it('should redirect to main route if the user is logged in', async () => {
    authStore.setIsLoggedIn(true);
    const navigateSpy = jest.spyOn(router, 'parseUrl').mockImplementation();

    await lastValueFrom(guard);

    expect(navigateSpy).toHaveBeenCalled();
  });
});
