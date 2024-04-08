import { map, OperatorFunction, pipe } from 'rxjs';

import { AuthState } from '@core/store/auth/auth.state';
import { KeycloakProfile } from 'keycloak-js';

import { ApplicationUserDTO, TermsDTO, UserStateDTO } from 'cca-api';

export const selectUserProfile: OperatorFunction<AuthState, KeycloakProfile> = map((state) => state.userProfile);
export const selectTerms: OperatorFunction<AuthState, TermsDTO> = map((state) => state.terms);
export const selectIsLoggedIn: OperatorFunction<AuthState, boolean> = map((state) => state.isLoggedIn);
export const selectUser: OperatorFunction<AuthState, ApplicationUserDTO> = map((state) => state.user);
export const selectUserState: OperatorFunction<AuthState, UserStateDTO> = map((state) => state.userState);

export const selectUserRoleType: OperatorFunction<AuthState, UserStateDTO['roleType']> = pipe(
  selectUserState,
  map((state) => state?.roleType),
);
export const selectUserId: OperatorFunction<AuthState, string> = pipe(
  selectUserState,
  map((state) => state?.userId),
);
export const selectLoginStatus: OperatorFunction<AuthState, UserStateDTO['status']> = pipe(
  selectUserState,
  map((state) => state?.status),
);
