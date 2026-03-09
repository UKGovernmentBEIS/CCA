import { TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router, UrlTree } from '@angular/router';

import { AuthStore } from '@netz/common/auth';
import { ActivatedRouteStub } from '@netz/common/testing';
import { KeycloakService } from '@shared/services';

import { AuthoritiesService, TermsAndConditionsService, UsersService } from 'cca-api';

import { AuthorizeGuard } from './authorize.guard';
import { mockAuthorityService, mockKeycloakService, mockTermsAndConditionsService, mockUsersService } from './mocks';

describe('AuthorizeGuard', () => {
  let guard: boolean | UrlTree;
  let authStore: AuthStore;
  let router: Router;

  const route = new ActivatedRouteStub();

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        { provide: KeycloakService, useValue: mockKeycloakService },
        { provide: UsersService, useValue: mockUsersService },
        { provide: AuthoritiesService, useValue: mockAuthorityService },
        { provide: TermsAndConditionsService, useValue: mockTermsAndConditionsService },
        { provide: ActivatedRoute, useValue: route },
      ],
    });

    authStore = TestBed.inject(AuthStore);
    router = TestBed.inject(Router);
  });

  it('should be created', () => {
    authStore.setUserState({ status: 'ENABLED' });
    guard = TestBed.runInInjectionContext(() => AuthorizeGuard());
    expect(guard).toBeTruthy();
  });

  it('should allow navigation when loginStatus enabled', async () => {
    authStore.setUserState({ status: 'ENABLED' });
    guard = TestBed.runInInjectionContext(() => AuthorizeGuard());
    expect(guard).toEqual(true);
  });

  it('should redirect to landing page when loginStatus not enabled', async () => {
    authStore.setUserState({ status: 'DISABLED' });
    guard = TestBed.runInInjectionContext(() => AuthorizeGuard());
    expect(guard).toEqual(router.parseUrl('landing'));
  });

  it(`should allow when role is REGULATOR and has no permissions`, async () => {
    authStore.setUserState({
      status: 'NO_AUTHORITY',
      roleType: 'REGULATOR',
    });

    guard = TestBed.runInInjectionContext(() => AuthorizeGuard());
    expect(guard).toEqual(true);
  });
});
