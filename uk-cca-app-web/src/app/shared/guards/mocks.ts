import { signal } from '@angular/core';

import { KeycloakService } from '@shared/services';
import { AuthService } from '@shared/services';
import { Mock } from 'vitest';

import { AuthoritiesService, TermsAndConditionsService, UsersService } from 'cca-api';

export const mockKeycloakService = {
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
} as unknown as KeycloakService;

export const mockAuthService = {
  checkUser: vi.fn(),
  login: vi.fn(),
  logout: vi.fn(),
  loadUser: vi.fn(),
  loadUserState: vi.fn(),
  loadUserProfile: vi.fn(),
  loadIsLoggedIn: vi.fn(),
  loadUserTerms: vi.fn(),
} as unknown as Record<keyof AuthService, Mock>;

export const mockUsersService: Record<keyof UsersService, Mock> = {
  getCurrentUser: vi.fn(),
  registerUserLastLoginDomain: vi.fn(),
} as unknown as Record<keyof UsersService, Mock>;

export const mockAuthorityService: Record<keyof AuthoritiesService, Mock> = {
  getCurrentUserState: vi.fn(),
} as unknown as Record<keyof AuthoritiesService, Mock>;

export const mockTermsAndConditionsService: Record<keyof TermsAndConditionsService, Mock> = {
  getLatestTerms: vi.fn(),
} as unknown as Record<keyof TermsAndConditionsService, Mock>;
