import { TestBed } from '@angular/core/testing';

import { CookiesService } from '../../cookies/cookies.service';
import { SignInTrackingService } from './sign-in-tracking.service';

describe('SignInTrackingService', () => {
  let service: SignInTrackingService;

  const cookiesService = { hasAnalyticsConsent: vi.fn() };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [{ provide: CookiesService, useValue: cookiesService }],
    });

    service = TestBed.inject(SignInTrackingService);
    cookiesService.hasAnalyticsConsent.mockReturnValue(true);
    sessionStorage.clear();
  });

  afterEach(() => {
    vi.clearAllMocks();
    sessionStorage.clear();
  });

  it('should mark a sign-in as started when analytics cookies have been accepted', () => {
    service.markSignInStarted();

    expect(Number(sessionStorage.getItem(SignInTrackingService.SIGN_IN_STARTED_KEY))).toBeGreaterThan(0);
    expect(cookiesService.hasAnalyticsConsent).toHaveBeenCalledTimes(1);
  });

  it('should not mark a sign-in as started when analytics cookies have not been accepted', () => {
    cookiesService.hasAnalyticsConsent.mockReturnValue(false);

    service.markSignInStarted();

    expect(sessionStorage.getItem(SignInTrackingService.SIGN_IN_STARTED_KEY)).toBeNull();
  });

  it('should consume a recent started sign-in once', () => {
    service.markSignInStarted();

    expect(service.consumeSignInStarted()).toBe(true);
    expect(service.consumeSignInStarted()).toBe(false);
    expect(sessionStorage.getItem(SignInTrackingService.SIGN_IN_STARTED_KEY)).toBeNull();
  });

  it('should not consume a sign-in started outside the completion window', () => {
    sessionStorage.setItem(SignInTrackingService.SIGN_IN_STARTED_KEY, String(Date.now() - 31 * 60 * 1000));

    expect(service.consumeSignInStarted()).toBe(false);
    expect(sessionStorage.getItem(SignInTrackingService.SIGN_IN_STARTED_KEY)).toBeNull();
  });

  it('should not consume a sign-in that was never started', () => {
    expect(service.consumeSignInStarted()).toBe(false);
  });

  it('should clear a started sign-in', () => {
    service.markSignInStarted();

    service.clearSignInStarted();

    expect(sessionStorage.getItem(SignInTrackingService.SIGN_IN_STARTED_KEY)).toBeNull();
  });

  it('should keep working when session storage is unavailable', () => {
    const getItemSpy = vi.spyOn(Storage.prototype, 'getItem').mockImplementation(() => {
      throw new Error('session storage is unavailable');
    });
    const setItemSpy = vi.spyOn(Storage.prototype, 'setItem').mockImplementation(() => {
      throw new Error('session storage is unavailable');
    });

    try {
      expect(() => service.markSignInStarted()).not.toThrow();
      expect(service.consumeSignInStarted()).toBe(false);
      expect(() => service.clearSignInStarted()).not.toThrow();
    } finally {
      getItemSpy.mockRestore();
      setItemSpy.mockRestore();
    }
  });
});
