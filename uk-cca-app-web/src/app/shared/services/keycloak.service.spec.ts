import { TestBed } from '@angular/core/testing';

import Keycloak from 'keycloak-js';

import { KeycloakEventType, KeycloakService } from './keycloak.service';

describe('KeycloakService', () => {
  let service: KeycloakService;
  let keycloakMock: Partial<Keycloak> & Record<string, unknown>;

  const events: KeycloakEventType[] = [
    KeycloakEventType.OnAuthSuccess,
    KeycloakEventType.OnAuthRefreshSuccess,
    KeycloakEventType.OnAuthRefreshError,
    KeycloakEventType.OnAuthLogout,
    KeycloakEventType.OnTokenExpired,
    KeycloakEventType.OnReady,
    KeycloakEventType.OnActionUpdate,
  ];

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [KeycloakService],
    });

    service = TestBed.inject(KeycloakService);

    keycloakMock = {
      authenticated: true,
      token: 'mock-token',
      profile: { email: 'test@test.com' },
      tokenParsed: { exp: 9999999999 },
      refreshTokenParsed: { exp: 9999999999, iat: 1000000 },
      login: vi.fn().mockResolvedValue(undefined),
      createLoginUrl: vi.fn().mockReturnValue('https://cca-sign-in.example'),
      logout: vi.fn().mockResolvedValue(undefined),
      updateToken: vi.fn().mockResolvedValue(true),
      loadUserProfile: vi.fn().mockResolvedValue({ email: 'test@test.com' }),
      isTokenExpired: vi.fn().mockReturnValue(false),
    };

    events.forEach((event) => {
      keycloakMock[event] = () => {
        service['keycloakEvents'].set({ type: event });
      };
    });

    service['keycloak'] = keycloakMock as unknown as Keycloak;
  });

  afterEach(() => {
    vi.clearAllMocks();
  });

  it('should reject updateToken, login, logout, loadUserProfile if not initialized', async () => {
    service['keycloak'] = undefined;
    await expect(service.updateToken()).rejects.toEqual('Keycloak not initialized');
    await expect(service.login()).rejects.toEqual('Keycloak not initialized');
    await expect(service.logout()).rejects.toEqual('Keycloak not initialized');
    await expect(service.loadUserProfile()).rejects.toEqual('Keycloak not initialized');
    expect(() => service.createLoginUrl()).toThrowError('Keycloak not initialized');
    expect(service.isTokenExpired()).toBe(true);
  });

  it('should emit keycloakEvents for all event types', () => {
    events.forEach((event) => {
      (keycloakMock[event] as () => void)();
      expect(service.keycloakEvents()).toEqual({ type: event });
    });
  });

  it('should return keycloak instance and auth info', () => {
    expect(service.keycloakInstance).toBeDefined();
    expect(service.isAuthenticated).toBe(true);
    expect(service.token).toBe('mock-token');
    expect(service.userProfile).toEqual({ email: 'test@test.com' });
    expect(service.tokenParsed).toEqual({ exp: 9999999999 });
    expect(service.refreshTokenParsed).toEqual({ exp: 9999999999, iat: 1000000 });
  });

  it('should call login, logout, updateToken, loadUserProfile', async () => {
    await service.login();
    const loginUrl = service.createLoginUrl({ redirectUri: 'http://redirect' });
    await service.logout('http://redirect');
    await service.updateToken(30);
    const profile = await service.loadUserProfile();

    expect(service.keycloakInstance.login).toHaveBeenCalled();
    expect(service.keycloakInstance.createLoginUrl).toHaveBeenCalledWith({ redirectUri: 'http://redirect' });
    expect(loginUrl).toBe('https://cca-sign-in.example');
    expect(service.keycloakInstance.logout).toHaveBeenCalledWith({ redirectUri: 'http://redirect' });
    expect(service.keycloakInstance.updateToken).toHaveBeenCalledWith(30);
    expect(service.keycloakInstance.loadUserProfile).toHaveBeenCalled();
    expect(profile).toEqual({ email: 'test@test.com' });
  });

  it('should call isTokenExpired with provided minValidity', () => {
    service.isTokenExpired(60);
    expect(service.keycloakInstance.isTokenExpired).toHaveBeenCalledWith(60);
  });
});
