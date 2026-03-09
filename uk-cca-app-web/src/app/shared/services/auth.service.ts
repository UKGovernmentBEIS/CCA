import { inject, Injectable } from '@angular/core';
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

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly authStore = inject(AuthStore);
  private readonly configStore = inject(ConfigStore);
  private readonly keycloakService = inject(KeycloakService);
  private readonly usersService = inject(UsersService);
  private readonly authorityService = inject(AuthoritiesService);
  private readonly termsAndConditionsService = inject(TermsAndConditionsService);
  private readonly route = inject(ActivatedRoute);

  login(options?: KeycloakLoginOptions): Promise<void> {
    let leaf = this.route.snapshot;

    while (leaf.firstChild) {
      leaf = leaf.firstChild;
    }

    return this.keycloakService.login({
      ...options,
      ...(leaf.data?.blockSignInRedirect ? { redirectUri: location.origin } : null),
    });
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
      map(() => null),
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
