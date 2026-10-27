import { inject, Service } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { combineLatest, from, map, type Observable, of, switchMap, tap } from 'rxjs';

import { AuthStore } from '@netz/common/auth';
import { KeycloakService } from '@shared/services';
import type { KeycloakLoginOptions, KeycloakProfile } from 'keycloak-js';

import {
  AuthoritiesService,
  TermsAndConditionsService,
  type UserDTO,
  UsersService,
  type UserStateDTO,
  type UserTermsVersionDTO,
} from 'cca-api';

import { selectIsFeatureEnabled } from '../config/config.selectors';
import { ConfigStore } from '../config/config.store';
import { SignInTrackingService } from './sign-in-tracking.service';

@Service()
export class AuthService {
  private readonly authStore = inject(AuthStore);
  private readonly configStore = inject(ConfigStore);
  private readonly keycloakService = inject(KeycloakService);
  private readonly usersService = inject(UsersService);
  private readonly authorityService = inject(AuthoritiesService);
  private readonly termsAndConditionsService = inject(TermsAndConditionsService);
  private readonly route = inject(ActivatedRoute);
  private readonly signInTrackingService = inject(SignInTrackingService);

  /**
   * Starts a sign-in the user chose, from a sign-in button or link. Only these sign-ins are reported as completed
   * sign-ins, so this marks the sign-in as started. Automatic re-authentication uses {@link reauthenticate} instead.
   */
  async login(options?: KeycloakLoginOptions): Promise<void> {
    const loginOptions = this.getLoginOptions(options);

    this.signInTrackingService.markSignInStarted();

    try {
      await this.keycloakService.login(loginOptions);
    } catch (error) {
      this.signInTrackingService.clearSignInStarted();
      throw error;
    }
  }

  /**
   * Starts a Keycloak sign-in for a session that expired or was rejected by the API. The user did not choose to sign
   * in, so this sign-in is never reported as a completed sign-in.
   */
  reauthenticate(options?: KeycloakLoginOptions): Promise<void> {
    return this.keycloakService.login(this.getLoginOptions(options));
  }

  createLoginUrl(options?: KeycloakLoginOptions): Promise<string> {
    return this.keycloakService.createLoginUrl(this.getLoginOptions(options));
  }

  private getLoginOptions(options?: KeycloakLoginOptions): KeycloakLoginOptions {
    let leaf = this.route.snapshot;

    while (leaf.firstChild) {
      leaf = leaf.firstChild;
    }

    return {
      ...options,
      ...(leaf.data?.blockSignInRedirect ? { redirectUri: location.origin } : null),
    };
  }

  logout(redirectPath = ''): Promise<void> {
    this.authStore.setIsLoggedIn(false);
    return this.keycloakService.logout(location.origin + redirectPath);
  }

  loadUser(): Observable<UserDTO> {
    return this.usersService.getCurrentUser().pipe(tap((user) => this.authStore.setUser(user)));
  }

  loadUserState(): Observable<UserStateDTO> {
    return this.authorityService.getCurrentUserState().pipe(tap((userState) => this.authStore.setUserState(userState)));
  }

  checkUser(): Observable<null> {
    if (this.authStore.state.isLoggedIn !== null) return of(null);
    return this.loadIsLoggedIn().pipe(
      switchMap((res) =>
        !res
          ? of(null)
          : combineLatest([this.loadUserState(), this.loadUserTerms(), this.loadUser(), this.loadUserProfile()]),
      ),
      map((): null => null),
    );
  }

  loadUserProfile(): Observable<KeycloakProfile> {
    return from(this.keycloakService.loadUserProfile()).pipe(tap((profile) => this.authStore.setUserProfile(profile)));
  }

  loadUserTerms(): Observable<UserTermsVersionDTO | null> {
    const termsEnabled = this.configStore.select(selectIsFeatureEnabled('terms'));
    if (!termsEnabled()) return of(null);
    return this.termsAndConditionsService.getUserTerms().pipe(tap((terms) => this.authStore.setUserTerms(terms)));
  }

  loadIsLoggedIn(): Observable<boolean> {
    return of(this.keycloakService.isAuthenticated).pipe(tap((isLoggedIn) => this.authStore.setIsLoggedIn(isLoggedIn)));
  }
}
