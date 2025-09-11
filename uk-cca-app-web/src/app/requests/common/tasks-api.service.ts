import { inject, Injectable } from '@angular/core';

import { catchError, Observable, tap, throwError } from 'rxjs';

import { BusinessErrorService } from '@error/business-error/business-error.service';
import { ErrorCode } from '@error/not-found-error';
import { PendingRequestService } from '@netz/common/services';
import { RequestTaskStore } from '@netz/common/store';
import { taskNotFoundError } from '@shared/errors';

import { RequestTaskActionProcessDTO, TasksService } from 'cca-api';

@Injectable({ providedIn: 'root' })
export class TasksApiService {
  private readonly tasksService = inject(TasksService);
  private readonly pendingRequestService = inject(PendingRequestService);
  private readonly businessErrorService = inject(BusinessErrorService);
  private readonly store = inject(RequestTaskStore);

  saveRequestTaskAction(dto: RequestTaskActionProcessDTO): Observable<unknown> {
    return this.tasksService.processRequestTaskAction(dto).pipe(
      catchError((err) => {
        if (err.code === ErrorCode.NOTFOUND1001) this.businessErrorService.showErrorForceNavigation(taskNotFoundError);
        return throwError(() => err);
      }),
      this.pendingRequestService.trackRequest(),
      tap((payload) => {
        this.store.setPayload(payload);
      }),
    );
  }
}
