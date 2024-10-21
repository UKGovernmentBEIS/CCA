import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn } from '@angular/router';

import { combineLatest, map, tap } from 'rxjs';

import { AuthStore, selectUserId } from '@netz/common/auth';

import { OperatorAuthoritiesService, OperatorUsersService } from 'cca-api';

import { ActiveOperatorStore } from './operator-details/active-operator.store';

export const CanEditOperatorUserDetailsGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const operatorUsersService = inject(OperatorUsersService);
  const operatorAuthortiesService = inject(OperatorAuthoritiesService);
  const accountId = +route.paramMap.get('targetUnitId');
  const userId = route.paramMap.get('userId');
  const currentUserId = inject(AuthStore).select(selectUserId);
  const isCurrentUser = currentUserId() === userId;
  const store = inject(ActiveOperatorStore);
  return combineLatest([
    isCurrentUser
      ? operatorUsersService.getCurrentOperatorUser(accountId)
      : operatorUsersService.getOperatorUserById(accountId, userId),
    operatorAuthortiesService.getAccountOperatorAuthorities(accountId),
  ]).pipe(
    tap(([operatorUserDetailsDTO, operatorAuthoritiesInfoDTO]) => {
      store.setState({
        editable: operatorAuthoritiesInfoDTO.editable,
        details: operatorUserDetailsDTO,
      });
    }),
    map(() => true),
  );
};
