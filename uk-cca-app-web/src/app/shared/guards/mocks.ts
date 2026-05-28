import { signal } from '@angular/core';

import { KeycloakService } from '@shared/services';
import { AuthService } from '@shared/services';
import { Mock } from 'vitest';

import { AuthoritiesService, TermsAndConditionsService, UsersService } from 'cca-api';

export const mockKeycloakService: Record<keyof KeycloakService, any> = {
  login: vi.fn(),
  logout: vi.fn(),
  isLoggedIn: vi.fn(),
  loadUserProfile: vi.fn(),
  init: vi.fn(),
  getKeycloakInstance: vi.fn(),
  getToken: vi.fn(),
  updateToken: vi.fn(),
  getUserProfile: vi.fn(),
  isTokenExpired: vi.fn(),
  getTokenParsed: vi.fn(),
  getRefreshTokenParsed: vi.fn(),
  keycloakEvents: signal(null),
} as any;

export const mockAuthService: Record<keyof AuthService, Mock> = {
  checkUser: vi.fn(),
  login: vi.fn(),
  logout: vi.fn(),
  loadUser: vi.fn(),
  loadUserState: vi.fn(),
  loadUserProfile: vi.fn(),
  loadIsLoggedIn: vi.fn(),
  loadUserTerms: vi.fn(),
};

export const mockUsersService: Record<keyof UsersService, Mock> = {
  getCurrentUser: vi.fn(),
  registerUserLastLoginDomain: vi.fn(),
} as any;

export const mockAuthorityService: Record<keyof AuthoritiesService, Mock> = {
  getCurrentUserState: vi.fn(),
} as any;

export const mockTermsAndConditionsService: Record<keyof TermsAndConditionsService, Mock> = {
  getLatestTerms: vi.fn(),
} as any;
