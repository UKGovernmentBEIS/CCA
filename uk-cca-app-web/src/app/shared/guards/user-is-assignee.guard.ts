import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, createUrlTreeFromSnapshot, UrlTree } from '@angular/router';

import { AuthStore, selectUserId } from '@netz/common/auth';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';

export function userIsAssigneeGuard(route: ActivatedRouteSnapshot): boolean | UrlTree {
  console.log('user assignee guard');

  const authStore = inject(AuthStore);
  const requestTaskStore = inject(RequestTaskStore);
  const taskId = route.paramMap.get('taskId');
  console.log('user assignee guard 2 ');
  console.log(authStore.select(selectUserId)() === requestTaskStore.select(requestTaskQuery.selectAssigneeUserId)());
  return authStore.select(selectUserId)() === requestTaskStore.select(requestTaskQuery.selectAssigneeUserId)()
    ? true
    : createUrlTreeFromSnapshot(route, [`/tasks/${taskId}`]);
}
