import { HttpErrorResponse, HttpStatusCode } from '@angular/common/http';
import { ErrorHandler } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { firstValueFrom, of } from 'rxjs';

import { AuthStore } from '@netz/common/auth';
import { ActivatedRouteStub, mockClass } from '@netz/common/testing';
import { KeycloakService } from '@shared/services';

import { AuthoritiesService, TermsAndConditionsService, UsersService } from 'cca-api';

import {
  mockAuthorityService,
  mockKeycloakService,
  mockTermsAndConditionsService,
  mockUsersService,
} from '../guards/mocks';
import { AuthService } from '../services/auth.service';
import { SignInTrackingService } from '../services/sign-in-tracking.service';
import { GlobalErrorHandlingService } from './global-error-handling.service';
import { StaleChunkRecoveryService } from './stale-chunk-recovery.service';

describe('GlobalErrorHandlingService', () => {
  let service: GlobalErrorHandlingService;
  let router: Router;
  let authStore: AuthStore;

  const authService = mockClass(AuthService);

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        { provide: AuthService, useValue: authService },
        { provide: ErrorHandler, useClass: GlobalErrorHandlingService },
      ],
    });

    authStore = TestBed.inject(AuthStore);
    authStore.setUserState({ status: 'ENABLED' });
    service = TestBed.inject(GlobalErrorHandlingService);
    router = TestBed.inject(Router);
  });

  afterEach(() => {
    vi.clearAllMocks();
  });

  it('should create', () => {
    expect(service).toBeTruthy();
  });

  it('should handle uncaught application errors', () => {
    const navigateSpy = vi.spyOn(router, 'navigate').mockResolvedValueOnce(true);
    const consoleErrorSpy = vi.spyOn(console, 'error').mockImplementation(() => undefined);

    const error = Error('Uncaught');
    service.handleError(error);

    expect(navigateSpy).toHaveBeenCalledWith(['/error', '500'], {
      state: { forceNavigation: true },
      skipLocationChange: true,
    });
    expect(consoleErrorSpy).toHaveBeenCalledWith('ERROR', error);
    consoleErrorSpy.mockRestore();
  });

  it('should load a fresh copy of the page instead of the error page when a chunk is stale', () => {
    const navigateSpy = vi.spyOn(router, 'navigate').mockResolvedValueOnce(true);
    const recoverSpy = vi.spyOn(TestBed.inject(StaleChunkRecoveryService), 'recover').mockReturnValueOnce(true);
    const error = new TypeError('Failed to fetch dynamically imported module: http://localhost/chunk-ABCD1234.js');

    service.handleError(error);

    expect(recoverSpy).toHaveBeenCalledWith(error);
    expect(navigateSpy).not.toHaveBeenCalled();
  });

  it('should show the error page when a stale chunk cannot be recovered', () => {
    const navigateSpy = vi.spyOn(router, 'navigate').mockResolvedValueOnce(true);
    vi.spyOn(TestBed.inject(StaleChunkRecoveryService), 'recover').mockReturnValueOnce(false);
    const consoleErrorSpy = vi.spyOn(console, 'error').mockImplementation(() => undefined);

    service.handleError(new TypeError('Importing a module script failed.'));

    expect(navigateSpy).toHaveBeenCalledWith(['/error', '500'], {
      state: { forceNavigation: true },
      skipLocationChange: true,
    });
    consoleErrorSpy.mockRestore();
  });

  it('should handle uncaught http 404 error', () => {
    const navigateSpy = vi.spyOn(router, 'navigate').mockResolvedValueOnce(true);
    const consoleErrorSpy = vi.spyOn(console, 'error').mockImplementation(() => undefined);

    const error = new HttpErrorResponse({ status: 404, statusText: 'test' });
    service.handleError(error);

    expect(navigateSpy).toHaveBeenCalledWith(['/error', '404'], {
      state: { forceNavigation: true },
    });
    expect(consoleErrorSpy).toHaveBeenCalledWith('ERROR', error);
    consoleErrorSpy.mockRestore();
  });

  it('should handle the 500 error', async () => {
    const navigateSpy = vi.spyOn(router, 'navigate').mockResolvedValueOnce(true);

    await expect(
      firstValueFrom(service.handleHttpError(new HttpErrorResponse({ status: 500, statusText: 'test' }))),
    ).rejects.toBeTruthy();

    expect(navigateSpy).toHaveBeenCalledTimes(1);
    expect(navigateSpy).toHaveBeenCalledWith(['/error', '500'], {
      state: { forceNavigation: true },
      skipLocationChange: true,
    });
  });

  it('should handle the 401 error', async () => {
    authService.reauthenticate.mockResolvedValueOnce();

    await expect(
      firstValueFrom(
        service.handleHttpError(
          new HttpErrorResponse({
            status: HttpStatusCode.Unauthorized,
            statusText: 'test',
          }),
        ),
      ),
    ).rejects.toBeTruthy();

    expect(authService.reauthenticate).toHaveBeenCalledTimes(1);
    expect(authService.login).not.toHaveBeenCalled();
  });

  it('should handle the 403 error', async () => {
    authService.loadUserState.mockReturnValueOnce(of({ status: 'ENABLED' }));
    const navigateSpy = vi.spyOn(router, 'navigate').mockResolvedValueOnce(true);

    await expect(
      firstValueFrom(
        service.handleHttpError(
          new HttpErrorResponse({
            status: HttpStatusCode.Forbidden,
            statusText: 'test',
          }),
        ),
      ),
    ).rejects.toBeTruthy();

    expect(authService.loadUserState).toHaveBeenCalledTimes(1);
    expect(navigateSpy).toHaveBeenCalledTimes(1);
    expect(navigateSpy).toHaveBeenCalledWith(['landing'], { state: { forceNavigation: true } });
    expect(authService.logout).not.toHaveBeenCalled();
  });

  it('should forward not handled errors', async () => {
    const navigateSpy = vi.spyOn(router, 'navigate').mockResolvedValueOnce(true);
    const error = new HttpErrorResponse({ status: 400, statusText: 'test' });

    await expect(firstValueFrom(service.handleHttpError(error))).rejects.toEqual(error);
    expect(navigateSpy).not.toHaveBeenCalled();
  });

  it('should forward error if it is a skip url', async () => {
    const navigateSpy = vi.spyOn(router, 'navigate').mockResolvedValueOnce(true);
    const error = new HttpErrorResponse({ status: 403, statusText: 'test', url: 'localhost/account/200/header-info' });

    await expect(firstValueFrom(service.handleHttpError(error))).rejects.toEqual(error);
    expect(navigateSpy).not.toHaveBeenCalled();
  });

  describe('sign-in tracking', () => {
    beforeEach(() => {
      TestBed.resetTestingModule();
      TestBed.configureTestingModule({
        providers: [
          { provide: KeycloakService, useValue: mockKeycloakService },
          { provide: UsersService, useValue: mockUsersService },
          { provide: AuthoritiesService, useValue: mockAuthorityService },
          { provide: TermsAndConditionsService, useValue: mockTermsAndConditionsService },
          { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
        ],
      });

      vi.mocked(mockKeycloakService.login).mockResolvedValue(undefined);
      sessionStorage.clear();
    });

    afterEach(() => {
      document.cookie = 'uk_cca_cookies_policy=; expires=Thu, 01 Jan 1970 00:00:00 GMT';
      sessionStorage.clear();
    });

    it('should not mark a sign-in as started when a 401 sends the user to Keycloak', async () => {
      document.cookie = 'uk_cca_cookies_policy={"usage":true}';
      const errorService = TestBed.inject(GlobalErrorHandlingService);

      await expect(
        firstValueFrom(errorService.handleHttpError(new HttpErrorResponse({ status: 401, statusText: 'test' }))),
      ).rejects.toBeTruthy();

      expect(mockKeycloakService.login).toHaveBeenCalledTimes(1);
      expect(sessionStorage.getItem(SignInTrackingService.SIGN_IN_STARTED_KEY)).toBeNull();
    });
  });
});
