import { signal } from '@angular/core';
import { TestBed } from '@angular/core/testing';

import { mockClass } from '@netz/common/testing';
import { AuthService, KeycloakEventType, KeycloakService } from '@shared/services';

import { TimeoutBannerService } from './timeout-banner.service';

describe('TimeoutBannerService', () => {
  let service: TimeoutBannerService;
  let keycloakService: jest.Mocked<KeycloakService>;
  let authService: jest.Mocked<AuthService>;

  const futureExp = Math.floor(Date.now() / 1000) + 210;
  const mockRefreshTokenParsed = { iat: Math.floor(Date.now() / 1000) - 100, exp: futureExp };

  beforeEach(() => {
    const keycloakServiceMock = mockClass(KeycloakService);
    const authServiceMock = mockClass(AuthService);

    TestBed.configureTestingModule({
      providers: [
        { provide: KeycloakService, useValue: keycloakServiceMock },
        { provide: AuthService, useValue: authServiceMock },
        TimeoutBannerService,
      ],
    });

    keycloakService = TestBed.inject(KeycloakService) as jest.Mocked<KeycloakService>;
    authService = TestBed.inject(AuthService) as jest.Mocked<AuthService>;
    service = TestBed.inject(TimeoutBannerService);

    (keycloakService.keycloakEvents as any) = signal(null);
    (keycloakService.updateToken as any) = jest.fn().mockResolvedValue(true);
    Object.defineProperty(keycloakService, 'keycloakInstance', {
      value: {
        refreshTokenParsed: mockRefreshTokenParsed,
      },
      configurable: true,
    });

    authService.logout.mockResolvedValue(undefined);
  });

  afterEach(() => {
    jest.clearAllTimers();
    jest.useRealTimers();
    jest.clearAllMocks();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should initialize with correct values', () => {
    expect(service.isVisible()).toBeFalsy();
    expect(service.timeExtensionAllowed()).toBeTruthy();
  });

  it('should extend session', async () => {
    await service.extendSession();
    expect(keycloakService.updateToken).toHaveBeenCalledWith(-1);
  });

  it('should hide banner when extending session', async () => {
    service.isVisible.set(true);
    await service.extendSession();
    expect(service.isVisible()).toBeFalsy();
  });

  it('should sign out and hide banner', () => {
    service.isVisible.set(true);
    service.signOut();
    expect(service.isVisible()).toBeFalsy();
    expect(authService.logout).toHaveBeenCalled();
  });

  it('should handle auth events', () => {
    (keycloakService.keycloakEvents as any).set({
      type: KeycloakEventType.OnAuthRefreshSuccess,
    });
    expect(service.countDownTime()).toBeGreaterThanOrEqual(0);
  });

  it('should cleanup on destroy', () => {
    jest.useFakeTimers();
    (keycloakService.keycloakEvents as any).set({
      type: KeycloakEventType.OnAuthRefreshSuccess,
    });
    service.ngOnDestroy();
    expect(jest.getTimerCount()).toBe(0);
    jest.useRealTimers();
  });
});
