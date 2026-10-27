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

  it('should schedule the banner before the refresh token expires', () => {
    vi.useFakeTimers();
    vi.setSystemTime(new Date('2030-01-01T00:00:00Z'));
    const exp = Math.floor(Date.now() / 1000) + 300;
    Object.assign(keycloakService, {
      keycloakInstance: { refreshTokenParsed: { iat: exp - 1800, exp }, timeSkew: 0 },
    });

    keycloakEvents.set({ type: KeycloakEventType.OnAuthRefreshSuccess });
    TestBed.tick();

    expect(service.countDownTime()).toBe((300 - service.timeOffsetSeconds) * 1000);

    vi.advanceTimersByTime(service.countDownTime());

    expect(service.isVisible()).toBe(true);
  });

  it('should account for keycloak clock skew when calculating the countdown', () => {
    vi.useFakeTimers();
    vi.setSystemTime(new Date('2030-01-01T00:00:00Z'));
    const exp = Math.floor(Date.now() / 1000) + 300;
    Object.assign(keycloakService, {
      keycloakInstance: { refreshTokenParsed: { iat: exp - 1800, exp }, timeSkew: 60 },
    });

    keycloakEvents.set({ type: KeycloakEventType.OnAuthRefreshSuccess });
    TestBed.tick();

    // Positive skew means the local clock runs ahead of the server, so the token has more time left
    // than naive local time would suggest (keycloak-js: remaining = exp - now + timeSkew).
    expect(service.countDownTime()).toBe((300 - service.timeOffsetSeconds + 60) * 1000);
  });

  it('should cleanup on destroy', () => {
    vi.useFakeTimers();
    keycloakEvents.set({ type: KeycloakEventType.OnAuthRefreshSuccess });
    service.ngOnDestroy();
    expect(vi.getTimerCount()).toBe(0);
  });
});
