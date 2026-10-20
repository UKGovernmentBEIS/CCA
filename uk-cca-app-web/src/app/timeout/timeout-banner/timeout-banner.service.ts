import { effect, inject, Injectable, OnDestroy, signal } from '@angular/core';

import { AuthService } from '@shared/services';
import { KeycloakEventType, KeycloakService } from '@shared/services';

import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class TimeoutBannerService implements OnDestroy {
  private readonly keycloak = inject(KeycloakService);
  private readonly authService = inject(AuthService);

  private get refreshTokenParsed() {
    return this.keycloak.keycloakInstance?.refreshTokenParsed;
  }

  private get refreshTokenParsedExp() {
    return this.refreshTokenParsed?.exp;
  }

  private get refreshTokenParsedIat() {
    return this.refreshTokenParsed?.iat;
  }

  private get refreshTokenExpOffset() {
    return this.refreshTokenParsedExp - this.refreshTokenParsedIat;
  }

  private initialRefreshTokenExpOffset: number | undefined;

  readonly timeOffsetSeconds = environment.timeoutBanner.timeOffsetSeconds;
  readonly countDownTime = signal(0);
  readonly timeExtensionAllowed = signal(true);
  readonly isVisible = signal(false);

  private countdownTimeout = null;
  private bannerTimeout = null;
  private scheduledExpiryTime: number | null = null;

  constructor() {
    effect(() => {
      const event = this.keycloak.keycloakEvents();
      if (!event) return;

      switch (event.type) {
        case KeycloakEventType.OnAuthRefreshSuccess:
          this.updateCountdownTime();
          // Set initial offset on first refresh event if not yet set
          if (this.initialRefreshTokenExpOffset === undefined && this.hasValidToken()) {
            this.initialRefreshTokenExpOffset = this.refreshTokenExpOffset;
          } else if (
            this.initialRefreshTokenExpOffset !== undefined &&
            this.refreshTokenExpOffset < this.initialRefreshTokenExpOffset
          ) {
            this.timeExtensionAllowed.set(false);
          }
          break;

        case KeycloakEventType.OnAuthLogout:
          this.idleLogout();
          break;
      }
    });

    // Schedule banner display based on countdown
    effect(() => {
      const countDownTime = this.countDownTime();
      const currentExpiryTime = this.refreshTokenParsedExp;

      if (countDownTime <= 0 || !this.hasValidToken()) return;

      // If banner is already visible, don't reschedule
      if (this.isVisible()) return;

      // Only reschedule if the expiry time changed significantly (> 60 seconds difference)
      // This prevents constant rescheduling on token refresh when expiry extends by ~40-60s
      if (this.scheduledExpiryTime !== null) {
        const expiryDiff = Math.abs(currentExpiryTime - this.scheduledExpiryTime);
        if (expiryDiff < 60) return;
      }

      // Clear any existing timeout
      if (this.countdownTimeout) {
        clearTimeout(this.countdownTimeout);
        this.countdownTimeout = null;
      }

      this.scheduledExpiryTime = currentExpiryTime;
      this.countdownTimeout = setTimeout(() => {
        this.isVisible.set(true);
      }, countDownTime);
    });

    // Handle banner display and automatic logout
    effect(() => {
      const isVisible = this.isVisible();

      // Clear any existing timeout
      if (this.bannerTimeout) {
        clearTimeout(this.bannerTimeout);
        this.bannerTimeout = null;
      }

      if (!isVisible) return;

      // Token expires at refreshTokenParsedExp * 1000 milliseconds
      // Show banner at (refreshTokenParsedExp - timeOffsetSeconds) * 1000
      // So wait until expiration: timeOffsetSeconds * 1000 milliseconds from when banner shows
      this.bannerTimeout = setTimeout(() => {
        this.isVisible.set(false);
        this.idleLogout();
      }, this.timeOffsetSeconds * 1000);
    });

    // Initialize countdown when token becomes available
    effect(() => {
      // Trigger on any auth event to ensure we recalculate if needed
      this.keycloak.keycloakEvents();
      if (this.hasValidToken()) {
        this.updateCountdownTime();
      }
    });
  }

  private hasValidToken(): boolean {
    return this.refreshTokenParsedExp !== undefined && this.refreshTokenParsedIat !== undefined;
  }

  private updateCountdownTime(): void {
    if (this.hasValidToken()) {
      this.countDownTime.set(this.calculateCountdownTime());
    }
  }

  extendSession() {
    if (this.keycloak?.keycloakInstance) {
      this.keycloak.updateToken(-1).then(() => {
        this.scheduledExpiryTime = null; // Reset so banner can be rescheduled with new expiry
        this.isVisible.set(false);
      });
    }
  }

  signOut() {
    this.isVisible.set(false);
    this.authService.logout();
  }

  private idleLogout() {
    const idleTime = this.refreshTokenParsedExp - this.refreshTokenParsedIat;
    this.keycloak.logout(`${location.origin}/timed-out?idle=${idleTime}`);
  }

  private calculateCountdownTime(): number {
    // Calculate milliseconds until token expiration, minus the offset (when to show banner)
    // Using absolute expiry time (exp) ensures countdown is stable across token refreshes
    const nowMs = Date.now();
    const expiryMs = this.refreshTokenParsedExp * 1000;
    const msUntilExpiry = expiryMs - nowMs;
    const msOffset = this.timeOffsetSeconds * 1000;
    const countdown = msUntilExpiry - msOffset;

    return countdown;
  }

  ngOnDestroy(): void {
    if (this.countdownTimeout) {
      clearTimeout(this.countdownTimeout);
    }
    if (this.bannerTimeout) {
      clearTimeout(this.bannerTimeout);
    }
    this.scheduledExpiryTime = null;
  }
}
