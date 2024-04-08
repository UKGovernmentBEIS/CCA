import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, CanDeactivate } from '@angular/router';

import { map, tap } from 'rxjs';

import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchNotFoundRequest, ErrorCode } from '@error/not-found-error';
import { taskNotFoundError } from '@shared/errors/request-task-error';

import { TasksService } from 'cca-api';

import { PaymentStore } from './store/payment.store';

@Injectable({ providedIn: 'root' })
export class PaymentGuard implements CanActivate, CanDeactivate<any> {
  constructor(
    private readonly store: PaymentStore,
    private readonly tasksService: TasksService,
    private readonly businessErrorService: BusinessErrorService,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): any {
    return this.tasksService.getTaskItemInfoById(Number(route.paramMap.get('taskId'))).pipe(
      catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
        this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
      ),
      tap((requestTaskItem) => {
        this.store.reset();
        this.store.setState({
          ...this.store.getState(),
          requestId: requestTaskItem.requestInfo.id,
          requestType: requestTaskItem.requestInfo.type,
          competentAuthority: requestTaskItem.requestInfo.competentAuthority,
          requestTaskId: requestTaskItem.requestTask.id,
          requestTaskItem: {
            requestTask: requestTaskItem.requestTask,
            requestInfo: requestTaskItem.requestInfo,
            userAssignCapable: requestTaskItem.userAssignCapable,
            allowedRequestTaskActions: requestTaskItem.allowedRequestTaskActions,
          },
          isEditable:
            requestTaskItem.allowedRequestTaskActions.includes('PAYMENT_MARK_AS_PAID') ||
            requestTaskItem.allowedRequestTaskActions.includes('PAYMENT_MARK_AS_RECEIVED'),
          paymentDetails: requestTaskItem.requestTask.payload,
        });
      }),
      map(() => true),
    );
  }

  canDeactivate(): boolean {
    return true;
  }
}
