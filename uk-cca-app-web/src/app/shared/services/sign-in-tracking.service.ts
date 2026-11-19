import { inject, Service } from '@angular/core';

import { CookiesService } from '../../cookies/cookies.service';

/**
 * Tracks sign-ins the user chose, so a full page load returning from Keycloak can be reported as a completed sign-in.
 * A full page load of an already signed in user returns from Keycloak with an authorisation code too, so the return
 * alone cannot tell a completed sign-in from an existing session.
 */
@Service()
export class SignInTrackingService {
  static readonly SIGN_IN_STARTED_KEY = 'cca_sign_in_started';

  // Sign-ins include two-factor authentication. Anything older than this is treated as an abandoned sign-in.
  private static readonly SIGN_IN_COMPLETION_WINDOW_MS = 30 * 60 * 1000;

  private readonly cookiesService = inject(CookiesService);

  /**
   * Records that the user chose to sign in. The flag only exists for analytics, so it is only set once the user has
   * accepted analytics cookies.
   */
  markSignInStarted(): void {
    if (!this.cookiesService.hasAnalyticsConsent()) return;

    try {
      sessionStorage.setItem(SignInTrackingService.SIGN_IN_STARTED_KEY, String(Date.now()));
    } catch {
      // Storage unavailable: sign-in still works, the return just cannot be reported as a completed sign-in.
    }
  }

  /**
   * Reports whether a sign-in was started recently and consumes the flag, so it is reported at most once.
   */
  consumeSignInStarted(): boolean {
    let startedAt: string | null;

    try {
      startedAt = sessionStorage.getItem(SignInTrackingService.SIGN_IN_STARTED_KEY);
      sessionStorage.removeItem(SignInTrackingService.SIGN_IN_STARTED_KEY);
    } catch {
      return false;
    }

    if (!startedAt) return false;

    const startedAtMilliseconds = Number(startedAt);

    return (
      Number.isFinite(startedAtMilliseconds) &&
      Date.now() - startedAtMilliseconds < SignInTrackingService.SIGN_IN_COMPLETION_WINDOW_MS
    );
  }

  /**
   * Clears the flag, so a sign-in that failed before reaching Keycloak is not later reported as completed.
   */
  clearSignInStarted(): void {
    try {
      sessionStorage.removeItem(SignInTrackingService.SIGN_IN_STARTED_KEY);
    } catch {
      // Storage unavailable: nothing to clear.
    }
  }
}
