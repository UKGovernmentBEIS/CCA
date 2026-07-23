import { createDescendingSelector, createSelector, StateSelector } from '@netz/common/store';
import { KeycloakProfile } from 'keycloak-js';

import { UserDTO, UserStateDTO, UserTermsVersionDTO } from 'cca-api';

import { AuthState } from './auth.state';

export const selectUserProfile: StateSelector<AuthState, KeycloakProfile | null> = createSelector(
  (state) => state.userProfile,
);

export const selectUserTerms: StateSelector<AuthState, UserTermsVersionDTO | null> = createSelector(
  (state) => state.userTerms,
);
export const selectIsLoggedIn: StateSelector<AuthState, boolean | null> = createSelector((state) => state.isLoggedIn);
export const selectUser: StateSelector<AuthState, UserDTO | null> = createSelector((state) => state.user);
export const selectUserState: StateSelector<AuthState, UserStateDTO | null> = createSelector(
  (state) => state.userState,
);

export const selectUserRoleType: StateSelector<AuthState, UserStateDTO['roleType'] | undefined> =
  createDescendingSelector(selectUserState, (state) => state?.roleType);
export const selectUserId: StateSelector<AuthState, string | undefined> = createDescendingSelector(
  selectUserState,
  (state) => state?.userId,
);
export const selectLoginStatus: StateSelector<AuthState, UserStateDTO['status'] | undefined> = createDescendingSelector(
  selectUserState,
  (state) => state?.status,
);
