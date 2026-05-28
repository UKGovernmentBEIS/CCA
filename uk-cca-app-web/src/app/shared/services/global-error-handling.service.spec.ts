import { HttpErrorResponse, HttpStatusCode } from '@angular/common/http';
import { ErrorHandler } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';

import { firstValueFrom, of } from 'rxjs';

import { AuthStore } from '@netz/common/auth';
import { mockClass } from '@netz/common/testing';

import { AuthService } from '../services/auth.service';
import { GlobalErrorHandlingService } from './global-error-handling.service';

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
    authService.login.mockResolvedValueOnce();

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

    expect(authService.login).toHaveBeenCalledTimes(1);
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
});
