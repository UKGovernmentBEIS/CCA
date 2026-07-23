import { KeycloakProfile } from 'keycloak-js';

import { UserDTO, UserStateDTO, UserTermsVersionDTO } from 'cca-api';

export interface AuthState {
  user: UserDTO | null;
  userProfile: KeycloakProfile | null;
  userState: UserStateDTO | null;
  userTerms: UserTermsVersionDTO | null;
  isLoggedIn: boolean | null;
}

export const initialState: AuthState = {
  user: null,
  userProfile: null,
  userState: null,
  userTerms: null,
  isLoggedIn: null,
};
