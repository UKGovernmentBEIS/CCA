import { signal } from '@angular/core';
import { TestBed } from '@angular/core/testing';

import { mockClass } from '@netz/common/testing';
import { AuthService, KeycloakEvent, KeycloakEventType, KeycloakService } from '@shared/services';
import { Mocked } from 'vitest';

import { TimeoutBannerService } from './timeout-banner.service';

describe('TimeoutBannerService', () => {
  let service: TimeoutBannerService;
  let keycloakService: Mocked<KeycloakService>;
  let authService: Mocked<AuthService>;
  let keycloakEvents: ReturnType<typeof signal<KeycloakEvent | null>>;

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

    keycloakService = TestBed.inject(KeycloakService) as Mocked<KeycloakService>;
    authService = TestBed.inject(AuthService) as Mocked<AuthService>;

    keycloakEvents = signal<KeycloakEvent | null>(null);
    Object.assign(keycloakService, {
      keycloakEvents,
      keycloakInstance: { refreshTokenParsed: mockRefreshTokenParsed },
    });
    keycloakService.updateToken.mockResolvedValue(true);
    authService.logout.mockResolvedValue(undefined);

    service = TestBed.inject(TimeoutBannerService);
  });

  afterEach(() => {
    vi.clearAllTimers();
    vi.useRealTimers();
    vi.clearAllMocks();
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
    keycloakEvents.set({ type: KeycloakEventType.OnAuthRefreshSuccess });
    expect(service.countDownTime()).toBeGreaterThanOrEqual(0);
  });

  it('should cleanup on destroy', () => {
    vi.useFakeTimers();
    keycloakEvents.set({ type: KeycloakEventType.OnAuthRefreshSuccess });
    service.ngOnDestroy();
    expect(vi.getTimerCount()).toBe(0);
  });
});
