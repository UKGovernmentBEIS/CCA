import { inject, Injector, isDevMode, runInInjectionContext } from '@angular/core';
import { CanActivateFn, CanDeactivateFn, Router } from '@angular/router';

import { catchError, forkJoin, map, of, switchMap, tap } from 'rxjs';

import { RequestTaskStore } from '@netz/common/store';

import { RequestActionsService, RequestItemsService, TasksService } from 'cca-api';

import { REQUEST_TASK_IS_EDITABLE_RESOLVER } from './request-task.providers';
import { RequestTaskIsEditableResolver } from './request-task.types';

export function getRequestTaskPageDefaultCanActivateGuard(taskIdParam = 'taskId'): CanActivateFn {
  return (route) => {
    const injector = inject(Injector);
    const router = inject(Router);
    const store = inject(RequestTaskStore);
    const tasksService = inject(TasksService);
    const requestActionsService = inject(RequestActionsService);
    const requestItemsService = inject(RequestItemsService);
    const editableResolver: RequestTaskIsEditableResolver = inject(REQUEST_TASK_IS_EDITABLE_RESOLVER);

    if (!route.paramMap.has(taskIdParam)) {
      console.warn(`No :${taskIdParam} param in route`);
      return true;
    }

    const id = Number(route.paramMap.get(taskIdParam));
    if (Number.isNaN(id)) {
      console.warn(`Invalid :${taskIdParam} param in route`);
      return true;
    }

    // auto-generated API methods return HttpResponse<T> | T unions;

    return tasksService.getTaskItemInfoById(id).pipe(
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      switchMap((requestTaskItem: any) =>
        forkJoin({
          timeline: requestActionsService
            .getRequestActionsByRequestId(requestTaskItem.requestInfo.id)
            // eslint-disable-next-line @typescript-eslint/no-explicit-any
            .pipe(map((t: any) => (Array.isArray(t) ? t : (t.body ?? [])))),

          related: requestItemsService
            .getItemsByRequest(requestTaskItem.requestInfo.id)
            // eslint-disable-next-line @typescript-eslint/no-explicit-any
            .pipe(map((r: any) => (r.items !== undefined ? r : (r.body ?? { items: [] })))),
        }).pipe(map(({ timeline, related }) => ({ requestTaskItem, timeline, related }))),
      ),
      tap(({ requestTaskItem, timeline, related }) => {
        store.setRequestTaskItem(requestTaskItem);
        store.setTimeline(timeline);
        store.setRelatedTasks(related.items);
        store.setIsEditable(runInInjectionContext(injector, editableResolver));
      }),
      map(() => true),
      catchError((e) => {
        if (isDevMode()) console.error(e);
        return of(router.createUrlTree(['dashboard']));
      }),
    );
  };
}

export function getRequestTaskPageCanDeactivateGuard(): CanDeactivateFn<unknown> {
  return () => {
    inject(RequestTaskStore).reset();
    return true;
  };
}
