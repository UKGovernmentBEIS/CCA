import { createDescendingSelector, createSelector, StateSelector } from '@netz/common/store';
import { KeycloakProfile } from 'keycloak-js';

import { UserDTO, UserStateDTO, UserTermsVersionDTO } from 'cca-api';

import { AuthState } from './auth.state';

export const selectUserProfile: StateSelector<AuthState, KeycloakProfile> = createSelector(
  (state) => state.userProfile,
);

export const selectUserTerms: StateSelector<AuthState, UserTermsVersionDTO> = createSelector(
  (state) => state.userTerms,
);
export const selectIsLoggedIn: StateSelector<AuthState, boolean> = createSelector((state) => state.isLoggedIn);
export const selectUser: StateSelector<AuthState, UserDTO> = createSelector((state) => state.user);
export const selectUserState: StateSelector<AuthState, UserStateDTO> = createSelector((state) => state.userState);

export const selectUserRoleType: StateSelector<AuthState, UserStateDTO['roleType']> = createDescendingSelector(
  selectUserState,
  (state) => state?.roleType,
);
export const selectUserId: StateSelector<AuthState, string> = createDescendingSelector(
  selectUserState,
  (state) => state?.userId,
);
export const selectLoginStatus: StateSelector<AuthState, UserStateDTO['status']> = createDescendingSelector(
  selectUserState,
  (state) => state?.status,
);
