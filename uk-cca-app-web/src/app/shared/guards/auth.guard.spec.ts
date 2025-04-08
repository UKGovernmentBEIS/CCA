import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';

import { lastValueFrom, of } from 'rxjs';

import { AuthStore } from '@netz/common/auth';
import { MockType } from '@netz/common/testing';
import { AuthService, LatestTermsStore } from '@shared/services';

import { ConfigStore } from '../config/config.store';
import { AuthGuard } from './auth.guard';

describe('AuthGuard', () => {
  let router: Router;
  let authStore: AuthStore;
  let latestTermsStore: LatestTermsStore;
  let configStore: ConfigStore;
  const authService: MockType<AuthService> = {
    checkUser: jest.fn(() => of(null)),
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        AuthStore,
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: AuthService, useValue: authService },
      ],
    });

    authStore = TestBed.inject(AuthStore);
    router = TestBed.inject(Router);
    latestTermsStore = TestBed.inject(LatestTermsStore);
    configStore = TestBed.inject(ConfigStore);
    configStore.setState({ features: { terms: true } });
  });
  function getGuard() {
    return TestBed.runInInjectionContext(() => AuthGuard());
  }

  it("should redirect to terms if user is logged in and terms don't match and terms feature is enabled", async () => {
    authStore.setIsLoggedIn(true);
    authStore.setUserState({ status: 'DISABLED' });
    authStore.setUserTerms({ termsVersion: 1 });

    latestTermsStore.setLatestTerms({ version: 2, url: 'asd' });
    configStore.setState({ features: { terms: true } });

    let res = await lastValueFrom(getGuard());
    expect(res).toEqual(router.parseUrl('terms'));

    authStore.setUserTerms({ termsVersion: 2 });
    res = await lastValueFrom(getGuard());
    expect(res).toEqual(router.parseUrl('landing'));

    authStore.setUserState({ status: 'ENABLED' });
    res = await lastValueFrom(getGuard());
    expect(res).toBeTruthy();
  });
  it('should redirect to landing page if user is not logged in or is disabled and terms feature is enabled', async () => {
    authStore.setIsLoggedIn(false);
    configStore.setState({ features: { terms: true } });
    expect(await lastValueFrom(getGuard())).toEqual(router.parseUrl('landing'));

    authStore.setIsLoggedIn(true);
    authStore.setUserTerms({ termsVersion: 1 });
    authStore.setUserState({ status: 'DISABLED' });
    latestTermsStore.setLatestTerms({ version: 1, url: 'asd' });
    expect(await lastValueFrom(getGuard())).toEqual(router.parseUrl('landing'));

    authStore.setIsLoggedIn(true);
    authStore.setUserState({ status: 'TEMP_DISABLED' });
    expect(await lastValueFrom(getGuard())).toEqual(router.parseUrl('landing'));
  });
  it('should allow access if user is logged in and not disabled and terms feature is disabled', async () => {
    authStore.setIsLoggedIn(true);
    authStore.setUserState({ status: 'ACCEPTED' });
    configStore.setState({ features: { terms: false } });
    await expect(lastValueFrom(getGuard())).resolves.toEqual(true);
  });
});
