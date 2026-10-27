import { Service, signal } from '@angular/core';

import Keycloak, {
  type KeycloakInitOptions,
  type KeycloakLoginOptions,
  type KeycloakProfile,
  type KeycloakServerConfig,
  type KeycloakTokenParsed,
} from 'keycloak-js';

import { logger } from '../utils/logger';

export enum KeycloakEventType {
  OnAuthSuccess = 'onAuthSuccess',
  OnAuthRefreshSuccess = 'onAuthRefreshSuccess',
  OnAuthRefreshError = 'onAuthRefreshError',
  OnAuthLogout = 'onAuthLogout',
  OnActionUpdate = 'onActionUpdate',
  OnTokenExpired = 'onTokenExpired',
  OnReady = 'onReady',
}

export interface KeycloakEvent {
  type: KeycloakEventType;
}

@Service()
export class KeycloakService {
  private keycloak: Keycloak | undefined;

  readonly keycloakEvents = signal<KeycloakEvent | null>(null);

  get keycloakInstance(): Keycloak | undefined {
    return this.keycloak;
  }

  get isAuthenticated(): boolean {
    return this.keycloak?.authenticated ?? false;
  }

  get token(): string | undefined {
    return this.keycloak?.token;
  }

  get userProfile(): KeycloakProfile | undefined {
    return this.keycloak?.profile;
  }

  get tokenParsed(): KeycloakTokenParsed {
    return this.keycloak?.tokenParsed;
  }

  get refreshTokenParsed(): KeycloakTokenParsed {
    return this.keycloak?.refreshTokenParsed;
  }

  init(config: KeycloakServerConfig & KeycloakInitOptions): Promise<boolean> {
    const { realm, clientId, url, ...initOptions } = config;

    const keycloakConfig: KeycloakServerConfig = {
      url,
      realm,
      clientId,
    };

    this.keycloak = this.createKeycloak(keycloakConfig);
    this.setupEventListeners();

    return new Promise((resolve, reject) => {
      this.keycloak
        .init(initOptions)
        .then((authenticated) => {
          resolve(authenticated);
        })
        .catch((error) => {
          logger.error('Keycloak initialization failed', error);
          reject(error);
        });
    });
  }

  // Factory seam so tests can inject a fake Keycloak adapter.
  protected createKeycloak(config: KeycloakServerConfig): Keycloak {
    return new Keycloak(config);
  }

  private setupEventListeners(): void {
    if (!this.keycloak) return;

    this.keycloak.onAuthSuccess = () => {
      this.keycloakEvents.set({ type: KeycloakEventType.OnAuthSuccess });
    };

    this.keycloak.onAuthRefreshSuccess = () => {
      this.keycloakEvents.set({ type: KeycloakEventType.OnAuthRefreshSuccess });
    };

    this.keycloak.onAuthRefreshError = () => {
      this.keycloakEvents.set({ type: KeycloakEventType.OnAuthRefreshError });
    };

    this.keycloak.onAuthLogout = () => {
      this.keycloakEvents.set({ type: KeycloakEventType.OnAuthLogout });
    };

    this.keycloak.onTokenExpired = () => {
      this.keycloakEvents.set({ type: KeycloakEventType.OnTokenExpired });
    };

    this.keycloak.onReady = () => {
      this.keycloakEvents.set({ type: KeycloakEventType.OnReady });
    };

    this.keycloak.onActionUpdate = () => {
      this.keycloakEvents.set({ type: KeycloakEventType.OnActionUpdate });
    };
  }

  private withKeycloak<T>(action: (keycloak: Keycloak) => Promise<T>): Promise<T> {
    if (!this.keycloak) {
      return Promise.reject(new Error('Keycloak not initialized'));
    }
    return action(this.keycloak);
  }

  updateToken(minValidity?: number): Promise<boolean> {
    return this.withKeycloak((keycloak) => keycloak.updateToken(minValidity ?? 60));
  }

  login(options?: KeycloakLoginOptions): Promise<void> {
    return this.withKeycloak((keycloak) => keycloak.login(options));
  }

  createLoginUrl(options?: KeycloakLoginOptions): Promise<string> {
    return this.withKeycloak((keycloak) => keycloak.createLoginUrl(options));
  }

  logout(redirectUri?: string): Promise<void> {
    return this.withKeycloak((keycloak) => keycloak.logout({ redirectUri }));
  }

  loadUserProfile(): Promise<KeycloakProfile> {
    return this.withKeycloak((keycloak) => keycloak.loadUserProfile());
  }

  isTokenExpired(minValidity?: number): boolean {
    if (!this.keycloak) {
      return true;
    }
    return this.keycloak.isTokenExpired(minValidity);
  }
}
