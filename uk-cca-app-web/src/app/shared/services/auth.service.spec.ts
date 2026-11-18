import { TestBed } from '@angular/core/testing';
import { ActivatedRoute, ActivatedRouteSnapshot } from '@angular/router';

import { firstValueFrom, of } from 'rxjs';

import {
  AuthStore,
  initialState,
  selectIsLoggedIn,
  selectUser,
  selectUserProfile,
  selectUserState,
  selectUserTerms,
} from '@netz/common/auth';
import { ActivatedRouteSnapshotStub, ActivatedRouteStub, mockClass } from '@netz/common/testing';
import { KeycloakService } from '@shared/services';
import { Mocked } from 'vitest';

import {
  AuthoritiesService,
  TermsAndConditionsService,
  TermsDTO,
  UsersService,
  UserStateDTO,
  UserTermsVersionDTO,
} from 'cca-api';

import { ConfigStore } from '../config/config.store';
import { AuthService } from './auth.service';

describe('AuthService', () => {
  let service: AuthService;
  let authStore: AuthStore;
  let activatedRoute: ActivatedRoute;
  let configStore: ConfigStore;

  const keycloakService = mockClass(KeycloakService);

  const user = {
    email: 'test@test.com',
    firstName: 'test',
    lastName: 'test',
    termsVersion: 1,
  };

  const userState: UserStateDTO = {
    status: 'ENABLED',
    roleType: 'OPERATOR',
    userId: 'opTestId',
  };

  const usersService: Partial<Mocked<UsersService>> = {
    getCurrentUser: vi.fn().mockReturnValue(of(user)),
  };

  const authoritiesService: Partial<Mocked<AuthoritiesService>> = {
    getCurrentUserState: vi.fn().mockReturnValue(of(userState)),
  };

  const latestTerms: TermsDTO = { url: '/test', version: 1 };
  const userTerms: UserTermsVersionDTO = { termsVersion: 1 };

  const termsService: Partial<Mocked<TermsAndConditionsService>> = {
    getLatestTerms: vi.fn().mockReturnValue(of(latestTerms)),
    getUserTerms: vi.fn().mockReturnValue(of(userTerms)),
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        { provide: KeycloakService, useValue: keycloakService },
        { provide: UsersService, useValue: usersService },
        { provide: AuthoritiesService, useValue: authoritiesService },
        { provide: TermsAndConditionsService, useValue: termsService },
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
      ],
    });

    authStore = TestBed.inject(AuthStore);
    service = TestBed.inject(AuthService);
    activatedRoute = TestBed.inject(ActivatedRoute);
    keycloakService.loadUserProfile.mockResolvedValue({ email: 'test@test.com' });
    configStore = TestBed.inject(ConfigStore);
    configStore.setState({ features: { terms: true } });
  });

  afterEach(() => {
    vi.clearAllMocks();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should login', async () => {
    await service.login();
    service.loadUser();

    expect(keycloakService.login).toHaveBeenCalledTimes(1);
    expect(keycloakService.login).toHaveBeenCalledWith({});
    expect(usersService.getCurrentUser).toHaveBeenCalledTimes(1);
  });

  it('should create a login URL', () => {
    keycloakService.createLoginUrl.mockReturnValue('https://cca-sign-in.example');

    expect(service.createLoginUrl({ redirectUri: location.origin })).toBe('https://cca-sign-in.example');
    expect(keycloakService.createLoginUrl).toHaveBeenCalledWith({ redirectUri: location.origin });
  });

  it('should logout', async () => {
    await service.logout();

    expect(keycloakService.logout).toHaveBeenCalled();
  });

  it('should load and update user status', async () => {
    expect(authStore.select(selectUserState)()).toBeNull();
    await expect(firstValueFrom(service.loadUserState())).resolves.toEqual(userState);
    expect(authStore.select(selectUserState)()).toEqual(userState);
  });

  it('should update all user info when checkUser is called', async () => {
    expect(authStore.state).toEqual(initialState);
    (keycloakService as { isAuthenticated: boolean }).isAuthenticated = false;

    await expect(firstValueFrom(service.checkUser())).resolves.toBeNull();

    expect(authStore.select(selectIsLoggedIn)()).toBeFalsy();
    expect(authStore.select(selectUserState)()).toBeNull();
    expect(authStore.select(selectUserTerms)()).toBeNull();
    expect(authStore.select(selectUser)()).toBeNull();
    expect(authStore.select(selectUserProfile)()).toBeNull();

    authStore.setIsLoggedIn(null);
    (keycloakService as { isAuthenticated: boolean }).isAuthenticated = true;

    await expect(firstValueFrom(service.checkUser())).resolves.toBeNull();

    expect(authStore.select(selectIsLoggedIn)()).toBeTruthy();
    expect(authStore.select(selectUserState)()).toEqual(userState);
    expect(authStore.select(selectUserTerms)()).toEqual(userTerms);
    expect(authStore.select(selectUser)()).toEqual(user);
    expect(authStore.select(selectUserProfile)()).toEqual({ email: 'test@test.com' });
  });

  it('should not update user info if logged in is already determined', async () => {
    authStore.setIsLoggedIn(false);
    const spy = vi.spyOn(service, 'loadUserState');

    await expect(firstValueFrom(service.checkUser())).resolves.toBeNull();
    expect(spy).not.toHaveBeenCalled();
  });

  it('should redirect to origin if leaf data is blocking sign in redirect', async () => {
    (activatedRoute as { snapshot: ActivatedRouteSnapshot }).snapshot = new ActivatedRouteSnapshotStub(
      undefined,
      undefined,
      {
        blockSignInRedirect: true,
      },
    );

    await service.login();

    expect(keycloakService.login).toHaveBeenCalledTimes(1);
    expect(keycloakService.login).toHaveBeenCalledWith({ redirectUri: location.origin });
  });
});
