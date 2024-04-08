import { Injectable } from '@angular/core';

import { AuthState, initialState } from '@core/store/auth/auth.state';
import { KeycloakProfile } from 'keycloak-js';

import { ApplicationUserDTO, TermsDTO, UserStateDTO } from 'cca-api';

import { Store } from '../store';

@Injectable({ providedIn: 'root' })
export class AuthStore extends Store<AuthState> {
  constructor() {
    super(initialState);
  }

  setIsLoggedIn(isLoggedIn: boolean) {
    this.setState({ ...this.getState(), isLoggedIn });
  }

  setUser(user: ApplicationUserDTO) {
    this.setState({ ...this.getState(), user });
  }

  setUserProfile(userProfile: KeycloakProfile) {
    this.setState({ ...this.getState(), userProfile });
  }

  setUserState(userState: UserStateDTO) {
    this.setState({ ...this.getState(), userState });
  }

  setTerms(terms: TermsDTO) {
    this.setState({ ...this.getState(), terms });
  }

  reset(): void {
    this.setState(initialState);
  }
}
