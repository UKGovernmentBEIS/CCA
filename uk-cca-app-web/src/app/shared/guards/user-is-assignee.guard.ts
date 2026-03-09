import { inject } from '@angular/core';
import { UrlTree } from '@angular/router';

import { AuthStore, selectUserId } from '@netz/common/auth';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';

export function userIsAssigneeGuard(): boolean | UrlTree {
  const authStore = inject(AuthStore);
  const requestTaskStore = inject(RequestTaskStore);

  return authStore.select(selectUserId)() === requestTaskStore.select(requestTaskQuery.selectAssigneeUserId)();
}
