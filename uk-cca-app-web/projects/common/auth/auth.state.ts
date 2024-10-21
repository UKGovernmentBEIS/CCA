import { KeycloakProfile } from 'keycloak-js';

import { UserDTO, UserStateDTO, UserTermsVersionDTO } from 'cca-api';

export interface AuthState {
  user: UserDTO;
  userProfile: KeycloakProfile;
  userState: UserStateDTO;
  userTerms: UserTermsVersionDTO;
  isLoggedIn: boolean;
}

export const initialState: AuthState = {
  user: null,
  userProfile: null,
  userState: null,
  userTerms: null,
  isLoggedIn: null,
};
