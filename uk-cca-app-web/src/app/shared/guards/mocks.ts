import { signal } from '@angular/core';

import { KeycloakService } from '@shared/services';

import Mock = jest.Mock;
import { AuthService } from '@shared/services';

import { AuthoritiesService, TermsAndConditionsService, UsersService } from 'cca-api';

export const mockKeycloakService: Record<keyof KeycloakService, any> = {
  login: jest.fn(),
  logout: jest.fn(),
  isLoggedIn: jest.fn(),
  loadUserProfile: jest.fn(),
  init: jest.fn(),
  getKeycloakInstance: jest.fn(),
  getToken: jest.fn(),
  updateToken: jest.fn(),
  getUserProfile: jest.fn(),
  isTokenExpired: jest.fn(),
  getTokenParsed: jest.fn(),
  getRefreshTokenParsed: jest.fn(),
  keycloakEvents: signal(null),
} as any;

export const mockAuthService: Record<keyof AuthService, Mock> = {
  checkUser: jest.fn(),
  login: jest.fn(),
  logout: jest.fn(),
  loadUser: jest.fn(),
  loadUserState: jest.fn(),
  loadUserProfile: jest.fn(),
  loadIsLoggedIn: jest.fn(),
  loadUserTerms: jest.fn(),
};

export const mockUsersService: Record<keyof UsersService, Mock> = {
  getCurrentUser: jest.fn(),
  registerUserLastLoginDomain: jest.fn(),
} as any;

export const mockAuthorityService: Record<keyof AuthoritiesService, Mock> = {
  getCurrentUserState: jest.fn(),
} as any;

export const mockTermsAndConditionsService: Record<keyof TermsAndConditionsService, Mock> = {
  getLatestTerms: jest.fn(),
} as any;
