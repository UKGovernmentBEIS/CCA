import { inject } from '@angular/core';

import { catchError, map, Observable, of, throwError } from 'rxjs';

import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchBadRequest, catchTaskReassignedBadRequest, ErrorCodes } from '@error/business-errors';
import { catchNotFoundRequest, ErrorCode } from '@error/not-found-error';
import { TaskApiService } from '@netz/common/forms';
import { PendingRequestService } from '@netz/common/services';
import { requestTaskQuery } from '@netz/common/store';
import { requestTaskReassignedError, taskNotFoundError } from '@shared/errors/request-task-error';

import {
  AdminTerminationSaveRequestTaskActionPayload,
  AdminTerminationSubmitRequestTaskPayload,
  CcaDecisionNotification,
  CcaNotifyOperatorForDecisionRequestTaskActionPayload,
  RequestTaskActionProcessDTO,
} from 'cca-api';

export class AdminTerminationTaskApiService extends TaskApiService {
  private readonly pendingRequestService = inject(PendingRequestService);
  private readonly businessErrorService = inject(BusinessErrorService);

  save(payload: AdminTerminationSubmitRequestTaskPayload): Observable<AdminTerminationSubmitRequestTaskPayload> {
    return this.service
      .processRequestTaskAction(this.createSaveAction(payload))
      .pipe(
        catchError((err) => {
          if (err.code === ErrorCode.NOTFOUND1001) {
            this.businessErrorService.showErrorForceNavigation(taskNotFoundError);
          }
          return throwError(() => err);
        }),
        this.pendingRequestService.trackRequest(),
      )
      .pipe(map(() => payload));
  }

  submit(): Observable<void> {
    return undefined;
  }

  private createSaveAction(
    payload: AdminTerminationSubmitRequestTaskPayload,
  ): RequestTaskActionProcessDTO & { requestTaskActionPayload: AdminTerminationSaveRequestTaskActionPayload } {
    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();
    const { adminTerminationReasonDetails, sectionsCompleted } = payload;

    return {
      requestTaskId,
      requestTaskActionType: 'ADMIN_TERMINATION_SAVE_APPLICATION',
      requestTaskActionPayload: {
        payloadType: 'ADMIN_TERMINATION_SAVE_PAYLOAD',
        adminTerminationReasonDetails,
        sectionsCompleted,
      },
    };
  }

  notifyOperator(decisionNotification: CcaDecisionNotification): Observable<void> {
    return this.service
      .processRequestTaskAction({
        requestTaskActionType: 'ADMIN_TERMINATION_NOTIFY_OPERATOR_FOR_DECISION',
        requestTaskId: this.store.select(requestTaskQuery.selectRequestTaskId)(),
        requestTaskActionPayload: {
          payloadType: 'NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD',
          decisionNotification: decisionNotification,
        } as CcaNotifyOperatorForDecisionRequestTaskActionPayload,
      })
      .pipe(
        catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
          this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
        ),
        catchBadRequest(
          [
            ErrorCodes.NOTIF1000,
            ErrorCodes.NOTIF1001,
            ErrorCodes.NOTIF1002,
            ErrorCodes.NOTIF1003,
            ErrorCodes.ACCOUNT1001,
          ],
          (res) => {
            console.error(res.error);
            return of(res);
          },
        ),
        catchTaskReassignedBadRequest(() =>
          this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
        ),
      );
  }
}
