import { TestBed } from '@angular/core/testing';

import { CookiesService } from './cookies.service';

describe('CookiesService', () => {
  let service: CookiesService;

  const clearCookies = () => {
    document.cookie = 'uk_cca_cookies_policy=; expires=Thu, 01 Jan 1970 00:00:00 GMT';
    document.cookie = 'uk_cca_cookies_preferences_set=; expires=Thu, 01 Jan 1970 00:00:00 GMT';
  };

  beforeEach(() => {
    TestBed.configureTestingModule({});

    service = TestBed.inject(CookiesService);
    clearCookies();
  });

  afterEach(clearCookies);

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should not report analytics consent without a policy cookie', () => {
    expect(service.hasAnalyticsConsent()).toBe(false);
  });

  it('should report analytics consent from the policy cookie', () => {
    document.cookie = 'uk_cca_cookies_policy={"essential":true,"usage":true}';

    expect(service.hasAnalyticsConsent()).toBe(true);

    document.cookie = 'uk_cca_cookies_policy={"essential":true,"usage":false}';

    expect(service.hasAnalyticsConsent()).toBe(false);
  });

  it('should not report analytics consent when the policy cookie is malformed', () => {
    document.cookie = 'uk_cca_cookies_policy=not-json';

    expect(() => service.hasAnalyticsConsent()).not.toThrow();
    expect(service.hasAnalyticsConsent()).toBe(false);
  });
});
