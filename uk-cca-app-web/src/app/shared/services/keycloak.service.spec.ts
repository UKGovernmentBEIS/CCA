import { TestBed } from '@angular/core/testing';

import Keycloak, { type KeycloakConfig, type KeycloakInitOptions } from 'keycloak-js';

import { KeycloakEventType, KeycloakService } from './keycloak.service';

// Mock keycloak-js
jest.mock('keycloak-js', () => {
  return jest.fn().mockImplementation(() => ({
    init: jest.fn().mockResolvedValue(true),
    login: jest.fn().mockResolvedValue(undefined),
    logout: jest.fn().mockResolvedValue(undefined),
    updateToken: jest.fn().mockResolvedValue(true),
    loadUserProfile: jest.fn().mockResolvedValue({ email: 'test@test.com' }),
    isTokenExpired: jest.fn().mockReturnValue(false),
    authenticated: true,
    token: 'mock-token',
    profile: { email: 'test@test.com' },
    tokenParsed: { exp: 9999999999 },
    refreshTokenParsed: { exp: 9999999999, iat: 1000000 },
  }));
});

describe('KeycloakService', () => {
  let service: KeycloakService;
  let mockKeycloakInstance: any;

  const keycloakConfig: KeycloakConfig & KeycloakInitOptions = {
    url: 'http://localhost:8080',
    realm: 'test-realm',
    clientId: 'test-client',
    onLoad: 'login-required',
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [KeycloakService],
    });

    service = TestBed.inject(KeycloakService);
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  describe('initialization', () => {
    it('should be created', () => {
      expect(service).toBeTruthy();
    });

    it('should initialize keycloak with correct config', async () => {
      const result = await service.init(keycloakConfig);

      expect(result).toBe(true);
      expect(Keycloak).toHaveBeenCalledWith({
        url: keycloakConfig.url,
        realm: keycloakConfig.realm,
        clientId: keycloakConfig.clientId,
      });
    });

    it('should pass init options to keycloak.init()', async () => {
      await service.init(keycloakConfig);

      const keycloakInstance = (Keycloak as any).mock.results[0].value;
      expect(keycloakInstance.init).toHaveBeenCalledWith(
        expect.objectContaining({
          onLoad: 'login-required',
        }),
      );
    });

    it('should setup event listeners after successful initialization', async () => {
      await service.init(keycloakConfig);

      const keycloakInstance = (Keycloak as any).mock.results[0].value;

      expect(keycloakInstance.onAuthSuccess).toBeDefined();
      expect(keycloakInstance.onAuthRefreshSuccess).toBeDefined();
      expect(keycloakInstance.onAuthRefreshError).toBeDefined();
      expect(keycloakInstance.onAuthLogout).toBeDefined();
      expect(keycloakInstance.onTokenExpired).toBeDefined();
      expect(keycloakInstance.onReady).toBeDefined();
      expect(keycloakInstance.onActionUpdate).toBeDefined();
    });

    it('should reject promise on initialization error', async () => {
      (Keycloak as any).mockImplementationOnce(() => ({
        init: jest.fn().mockRejectedValueOnce(new Error('Init failed')),
      }));

      const newService = new KeycloakService();
      await expect(newService.init(keycloakConfig)).rejects.toThrow('Init failed');
    });
  });

  describe('event listeners - keycloakEvents signal', () => {
    beforeEach(async () => {
      await service.init(keycloakConfig);
      mockKeycloakInstance = (Keycloak as any).mock.results[0].value;
    });

    it('should emit OnAuthSuccess event', () => {
      mockKeycloakInstance.onAuthSuccess();
      expect(service.keycloakEvents()).toEqual({ type: KeycloakEventType.OnAuthSuccess });
    });

    it('should emit OnAuthRefreshSuccess event', () => {
      mockKeycloakInstance.onAuthRefreshSuccess();
      expect(service.keycloakEvents()).toEqual({ type: KeycloakEventType.OnAuthRefreshSuccess });
    });

    it('should emit OnAuthRefreshError event', () => {
      mockKeycloakInstance.onAuthRefreshError();
      expect(service.keycloakEvents()).toEqual({ type: KeycloakEventType.OnAuthRefreshError });
    });

    it('should emit OnAuthLogout event', () => {
      mockKeycloakInstance.onAuthLogout();
      expect(service.keycloakEvents()).toEqual({ type: KeycloakEventType.OnAuthLogout });
    });

    it('should emit OnTokenExpired event', () => {
      mockKeycloakInstance.onTokenExpired();
      expect(service.keycloakEvents()).toEqual({ type: KeycloakEventType.OnTokenExpired });
    });

    it('should emit OnReady event', () => {
      mockKeycloakInstance.onReady();
      expect(service.keycloakEvents()).toEqual({ type: KeycloakEventType.OnReady });
    });

    it('should emit OnActionUpdate event', () => {
      mockKeycloakInstance.onActionUpdate();
      expect(service.keycloakEvents()).toEqual({ type: KeycloakEventType.OnActionUpdate });
    });

    it('should initially return null for keycloakEvents', () => {
      const newService = new KeycloakService();
      expect(newService.keycloakEvents()).toBeNull();
    });
  });

  describe('getters and accessors', () => {
    beforeEach(async () => {
      await service.init(keycloakConfig);
    });

    it('should return keycloak instance', () => {
      expect(service.keycloakInstance).toBeDefined();
    });

    it('should return authenticated status', () => {
      expect(service.isAuthenticated).toBe(true);
    });

    it('should return token', () => {
      expect(service.token).toBe('mock-token');
    });

    it('should return user profile', () => {
      expect(service.userProfile).toEqual({ email: 'test@test.com' });
    });

    it('should return token parsed', () => {
      expect(service.tokenParsed).toEqual({ exp: 9999999999 });
    });

    it('should return refresh token parsed', () => {
      expect(service.refreshTokenParsed).toEqual({ exp: 9999999999, iat: 1000000 });
    });

    it('should return undefined keycloak instance before init', () => {
      const newService = new KeycloakService();
      expect(newService.keycloakInstance).toBeUndefined();
    });

    it('should return undefined for isAuthenticated before init', () => {
      const newService = new KeycloakService();
      expect(newService.isAuthenticated).toBeUndefined();
    });
  });

  describe('updateToken', () => {
    beforeEach(async () => {
      await service.init(keycloakConfig);
      mockKeycloakInstance = (Keycloak as any).mock.results[0].value;
    });

    it('should call keycloak.updateToken with default minValidity', async () => {
      await service.updateToken();
      expect(mockKeycloakInstance.updateToken).toHaveBeenCalledWith(60);
    });

    it('should call keycloak.updateToken with provided minValidity', async () => {
      await service.updateToken(30);
      expect(mockKeycloakInstance.updateToken).toHaveBeenCalledWith(30);
    });

    it('should reject if keycloak not initialized', async () => {
      const newService = new KeycloakService();
      await expect(newService.updateToken()).rejects.toEqual('Keycloak not initialized');
    });
  });

  describe('login', () => {
    beforeEach(async () => {
      await service.init(keycloakConfig);
      mockKeycloakInstance = (Keycloak as any).mock.results[0].value;
    });

    it('should call keycloak.login with no options', async () => {
      await service.login();
      expect(mockKeycloakInstance.login).toHaveBeenCalledWith(undefined);
    });

    it('should call keycloak.login with provided options', async () => {
      const loginOptions = { redirectUri: 'http://localhost:4202/landing' };
      await service.login(loginOptions);
      expect(mockKeycloakInstance.login).toHaveBeenCalledWith(loginOptions);
    });

    it('should reject if keycloak not initialized', async () => {
      const newService = new KeycloakService();
      await expect(newService.login()).rejects.toEqual('Keycloak not initialized');
    });
  });

  describe('logout', () => {
    beforeEach(async () => {
      await service.init(keycloakConfig);
      mockKeycloakInstance = (Keycloak as any).mock.results[0].value;
    });

    it('should call keycloak.logout with no redirect URI', async () => {
      await service.logout();
      expect(mockKeycloakInstance.logout).toHaveBeenCalledWith({});
    });

    it('should call keycloak.logout with redirect URI', async () => {
      const redirectUri = 'http://localhost:4202/landing';
      await service.logout(redirectUri);
      expect(mockKeycloakInstance.logout).toHaveBeenCalledWith({ redirectUri });
    });

    it('should reject if keycloak not initialized', async () => {
      const newService = new KeycloakService();
      await expect(newService.logout()).rejects.toEqual('Keycloak not initialized');
    });
  });

  describe('loadUserProfile', () => {
    beforeEach(async () => {
      await service.init(keycloakConfig);
      mockKeycloakInstance = (Keycloak as any).mock.results[0].value;
    });

    it('should call keycloak.loadUserProfile', async () => {
      const profile = await service.loadUserProfile();
      expect(mockKeycloakInstance.loadUserProfile).toHaveBeenCalled();
      expect(profile).toEqual({ email: 'test@test.com' });
    });

    it('should reject if keycloak not initialized', async () => {
      const newService = new KeycloakService();
      await expect(newService.loadUserProfile()).rejects.toEqual('Keycloak not initialized');
    });

    it('should reject if loadUserProfile fails', async () => {
      mockKeycloakInstance.loadUserProfile.mockRejectedValueOnce(new Error('Profile load failed'));
      await expect(service.loadUserProfile()).rejects.toThrow('Profile load failed');
    });
  });

  describe('isTokenExpired', () => {
    beforeEach(async () => {
      await service.init(keycloakConfig);
      mockKeycloakInstance = (Keycloak as any).mock.results[0].value;
    });

    it('should call keycloak.isTokenExpired with default minValidity', () => {
      service.isTokenExpired();
      expect(mockKeycloakInstance.isTokenExpired).toHaveBeenCalledWith(undefined);
    });

    it('should call keycloak.isTokenExpired with provided minValidity', () => {
      service.isTokenExpired(60);
      expect(mockKeycloakInstance.isTokenExpired).toHaveBeenCalledWith(60);
    });

    it('should return true if keycloak not initialized', () => {
      const newService = new KeycloakService();
      expect(newService.isTokenExpired()).toBe(true);
    });

    it('should return token expiration status', () => {
      mockKeycloakInstance.isTokenExpired.mockReturnValue(true);
      const result = service.isTokenExpired();
      expect(result).toBe(true);
    });
  });
});
