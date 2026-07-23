import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, createUrlTreeFromSnapshot } from '@angular/router';

import { AuthStore, selectUserId } from '@netz/common/auth';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';

export function userIsAssigneeGuard(route: ActivatedRouteSnapshot) {
  const authStore = inject(AuthStore);
  const requestTaskStore = inject(RequestTaskStore);

  const userId = authStore.select(selectUserId)();
  const assigneeUserId = requestTaskStore.select(requestTaskQuery.selectAssigneeUserId)();

  return userId !== undefined && userId === assigneeUserId ? true : createUrlTreeFromSnapshot(route, ['../']);
}
