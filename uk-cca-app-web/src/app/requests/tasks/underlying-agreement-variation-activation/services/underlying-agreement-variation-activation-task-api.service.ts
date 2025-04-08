import { inject, Injectable } from '@angular/core';

import { catchError, Observable, of, throwError } from 'rxjs';

import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchBadRequest, catchTaskReassignedBadRequest, ErrorCodes } from '@error/business-errors';
import { catchNotFoundRequest, ErrorCode } from '@error/not-found-error';
import { TaskApiService } from '@netz/common/forms';
import { PendingRequestService } from '@netz/common/services';
import { requestTaskQuery } from '@netz/common/store';
import { requestTaskReassignedError, taskNotFoundError } from '@shared/errors';

import {
  CcaDecisionNotification,
  CcaNotifyOperatorForDecisionRequestTaskActionPayload,
  RequestTaskActionProcessDTO,
  UnderlyingAgreementVariationActivationRequestTaskPayload,
} from 'cca-api';

@Injectable()
export class UnderlyingAgreementVariationActivationTaskApiService extends TaskApiService {
  private readonly pendingRequestService = inject(PendingRequestService);
  private readonly businessErrorService = inject(BusinessErrorService);

  save(
    payload: UnderlyingAgreementVariationActivationRequestTaskPayload,
  ): Observable<UnderlyingAgreementVariationActivationRequestTaskPayload> {
    return this.service.processRequestTaskAction(this.createSaveAction(payload)).pipe(
      catchError((err) => {
        if (err.code === ErrorCode.NOTFOUND1001) {
          this.businessErrorService.showErrorForceNavigation(taskNotFoundError);
        }
        return throwError(() => err);
      }),
      this.pendingRequestService.trackRequest(),
    );
  }

  submit(): Observable<void> {
    return undefined;
  }

  notifyOperator(decisionNotification: CcaDecisionNotification): Observable<void> {
    return this.service
      .processRequestTaskAction({
        requestTaskActionType: 'UNDERLYING_AGREEMENT_VARIATION_ACTIVATION_NOTIFY_OPERATOR_FOR_DECISION',
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

  private createSaveAction(
    payload: UnderlyingAgreementVariationActivationRequestTaskPayload,
  ): RequestTaskActionProcessDTO & {
    requestTaskActionPayload: UnderlyingAgreementVariationActivationRequestTaskPayload;
  } {
    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();
    const { underlyingAgreementActivationDetails, sectionsCompleted } = payload;

    return {
      requestTaskId,
      requestTaskActionType: 'UNDERLYING_AGREEMENT_VARIATION_ACTIVATION_SAVE_APPLICATION',
      requestTaskActionPayload: {
        payloadType: 'UNDERLYING_AGREEMENT_VARIATION_ACTIVATION_SAVE_PAYLOAD',
        underlyingAgreementActivationDetails,
        sectionsCompleted,
      },
    };
  }
}
