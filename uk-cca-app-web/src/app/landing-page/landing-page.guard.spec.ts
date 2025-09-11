import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';

import { lastValueFrom, of } from 'rxjs';

import { AuthStore } from '@netz/common/auth';
import { MockType } from '@netz/common/testing';
import { ConfigStore } from '@shared/config';
import { AuthService, LatestTermsStore } from '@shared/services';

import { LandingPageGuard } from './landing-page.guard';

describe('LandingPageGuard', () => {
  let router: Router;
  let authStore: AuthStore;
  let latestTermsStore: LatestTermsStore;
  let configStore: ConfigStore;

  const authService: MockType<AuthService> = {
    checkUser: jest.fn(() => of(undefined)),
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [{ provide: AuthService, useValue: authService }, LandingPageGuard],
    });

    authStore = TestBed.inject(AuthStore);
    authStore.setIsLoggedIn(true);
    authStore.setUserState({ status: 'ENABLED', roleType: 'OPERATOR' });
    authStore.setUser({ email: 'asd@asd.com', firstName: 'Darth', lastName: 'Vader' });
    authStore.setUserTerms({ termsVersion: 1 });

    latestTermsStore = TestBed.inject(LatestTermsStore);
    latestTermsStore.setLatestTerms({ version: 1, url: 'asd' });

    configStore = TestBed.inject(ConfigStore);
    configStore.setState({ features: { terms: true } });
    router = TestBed.inject(Router);
  });

  function getGuard() {
    return TestBed.runInInjectionContext(() => LandingPageGuard());
  }

  it('should allow if user is not logged in', () => {
    authStore.setIsLoggedIn(false);
    return expect(lastValueFrom(getGuard())).resolves.toEqual(true);
  });

  it('should allow if user has no role type', async () => {
    authStore.setUserState({ roleType: null });
    return expect(await lastValueFrom(getGuard())).toEqual(true);
  });

  it('should allow if user is logged in and terms match and status is not ENABLED', () => {
    authStore.setUserState({ status: 'DISABLED' });
    return expect(lastValueFrom(getGuard())).resolves.toEqual(true);
  });

  it('should allow if user is logged in and no authority', () => {
    authStore.setIsLoggedIn(true);
    authStore.setUserState({ status: 'NO_AUTHORITY' });
    return expect(lastValueFrom(getGuard())).resolves.toEqual(true);
  });

  it(`should allow when user has login with status 'ACCEPTED'`, async () => {
    authStore.setUserState({ status: 'ACCEPTED' });
    await expect(lastValueFrom(getGuard())).resolves.toEqual(true);
  });

  it(`should redirect to dashboard when user is REGULATOR and has NO_AUTHORITY`, async () => {
    authStore.setUserState({
      roleType: 'REGULATOR',
      status: 'NO_AUTHORITY',
    });

    await expect(lastValueFrom(getGuard())).resolves.toEqual(router.parseUrl('dashboard'));

    authStore.setUserState({
      ...authStore.state.userState,
      roleType: 'OPERATOR',
    });

    await expect(lastValueFrom(getGuard())).resolves.toEqual(true);
  });

  it(`should redirect to dashboard when user is REGULATOR and has NO_AUTHORITY`, async () => {
    authStore.setUserState({
      roleType: 'REGULATOR',
      status: 'NO_AUTHORITY',
    });
    expect(await lastValueFrom(getGuard())).toEqual(router.parseUrl('dashboard'));

    authStore.setUserState({
      ...authStore.state.userState,
      roleType: 'OPERATOR',
    });
    await expect(lastValueFrom(getGuard())).resolves.toEqual(true);
  });

  it(`should allow when user has login 'DISABLED' or 'TEMP_DISABLED'`, async () => {
    authStore.setUserState({
      roleType: 'REGULATOR',
      status: 'TEMP_DISABLED',
    });

    await expect(lastValueFrom(getGuard())).resolves.toEqual(true);

    authStore.setUserState({
      ...authStore.state.userState,
      roleType: 'OPERATOR',
    });

    await expect(lastValueFrom(getGuard())).resolves.toEqual(true);
  });

  it('should not redirect to terms if terms feature is disabled', async () => {
    configStore.setState({ features: { terms: false } });
    await expect(lastValueFrom(getGuard())).resolves.toEqual(router.parseUrl('dashboard'));
  });

  it(`should redirect to terms when terms feature is enabled and terms differ`, async () => {
    configStore.setState({ features: { terms: true } });
    authStore.setUserTerms({ termsVersion: 1 });
    latestTermsStore.setLatestTerms({ url: 'aa', version: 2 });
    await expect(lastValueFrom(getGuard())).resolves.toEqual(router.parseUrl('terms'));
  });
});
