import { TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { lastValueFrom, of } from 'rxjs';

import { AuthStore } from '@netz/common/auth';
import { ActivatedRouteStub } from '@netz/common/testing';
import { AuthService, LatestTermsStore } from '@shared/services';
import { KeycloakService } from 'keycloak-angular';

import { AuthoritiesService, TermsAndConditionsService, UsersService } from 'cca-api';

import { ConfigStore } from '../config/config.store';
import {
  mockAuthorityService,
  mockAuthService,
  mockKeycloakService,
  mockTermsAndConditionsService,
  mockUsersService,
} from './mocks';
import { TermsAndConditionsGuard } from './terms-and-conditions.guard';

describe('TermsAndConditionsGuard', () => {
  let authStore: AuthStore;
  let router: Router;
  let latestTermsStore: LatestTermsStore;
  let configStore: ConfigStore;

  const route = new ActivatedRouteStub();

  mockAuthService.checkUser.mockReturnValue(of(undefined));

  function getGuard(_, snapshot) {
    return TestBed.runInInjectionContext(() => TermsAndConditionsGuard(null, snapshot));
  }

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        { provide: KeycloakService, useValue: mockKeycloakService },
        { provide: UsersService, useValue: mockUsersService },
        { provide: AuthoritiesService, useValue: mockAuthorityService },
        { provide: TermsAndConditionsService, useValue: mockTermsAndConditionsService },
        { provide: AuthService, useValue: mockAuthService },
        { provide: ActivatedRoute, useValue: route },
      ],
    });

    authStore = TestBed.inject(AuthStore);
    authStore.setUserTerms({ version: 1 } as any);
    router = TestBed.inject(Router);

    latestTermsStore = TestBed.inject(LatestTermsStore);
    latestTermsStore.setLatestTerms({ version: 1, url: 'url' });

    configStore = TestBed.inject(ConfigStore);
    configStore.setState({ features: { terms: true } });
  });

  afterEach(() => {
    mockAuthService.checkUser.mockClear();
  });

  it('should allow access when terms feature is disabled', async () => {
    const state: any = { url: '/terms' };
    configStore.setState({ features: { terms: false } });

    const result = await lastValueFrom(getGuard(null, state));

    expect(result).toEqual(true);
    expect(mockAuthService.checkUser).toHaveBeenCalledTimes(1);
  });

  it("should allow access when url is '/terms' and terms versions differ", async () => {
    authStore.setUserTerms({ termsVersion: 2 });
    configStore.setState({ features: { terms: true } });

    const state: any = { url: '/terms' };
    const result = await lastValueFrom(getGuard(null, state));

    expect(result).toEqual(true);
    expect(mockAuthService.checkUser).toHaveBeenCalledTimes(1);
  });

  it("should disallow access when url is '/terms' and terms versions are equal", async () => {
    authStore.setUserTerms({ termsVersion: 1 });
    configStore.setState({ features: { terms: true } });

    const state: any = { url: '/terms' };
    const result = await lastValueFrom(getGuard(null, state));

    expect(result).toEqual(router.parseUrl('landing'));
    expect(mockAuthService.checkUser).toHaveBeenCalledTimes(1);
  });

  it('should allow access when terms versions are equal', async () => {
    authStore.setUserTerms({ termsVersion: 1 });
    configStore.setState({ features: { terms: true } });

    const result = await lastValueFrom(getGuard(null, { url: '' } as any));

    expect(result).toEqual(true);
    expect(mockAuthService.checkUser).toHaveBeenCalledTimes(1);
  });

  it('should disallow access when terms versions differ', async () => {
    authStore.setUser({ termsVersion: 2 } as any);

    const result = await lastValueFrom(getGuard(null, { url: '' } as any));

    expect(result).toEqual(router.parseUrl('landing'));
    expect(mockAuthService.checkUser).toHaveBeenCalledTimes(1);
  });
});
