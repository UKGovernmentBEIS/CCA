import { inject } from '@angular/core';
import { CanDeactivateFn, createUrlTreeFromSnapshot } from '@angular/router';

import { catchError, map, of, tap } from 'rxjs';

import { RequestTaskStore } from '@netz/common/store';

import { TasksService } from 'cca-api';

export const refreshDaysRemainingGuard: CanDeactivateFn<unknown> = (_, route) => {
  const store = inject(RequestTaskStore);
  const tasksService = inject(TasksService);

  const taskId = +route.params.taskId;

  return tasksService.getTaskItemInfoById(taskId).pipe(
    catchError(() => {
      createUrlTreeFromSnapshot(route, ['/dashboard']);
      return of(null);
    }),
    tap((requestTaskItem) => store.setRequestTaskItem(requestTaskItem)),
    map(() => true),
  );
};
