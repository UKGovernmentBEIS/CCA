import { inject, Injectable } from '@angular/core';

import { catchError, map, Observable, throwError } from 'rxjs';

import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchTaskReassignedBadRequest } from '@error/business-errors';
import { catchNotFoundRequest, ErrorCode } from '@error/not-found-error';
import { TaskApiService } from '@netz/common/forms';
import { PendingRequestService } from '@netz/common/services';
import { requestTaskQuery } from '@netz/common/store';
import { requestTaskReassignedError, taskNotFoundError } from '@shared/errors/request-task-error';

import {
  RequestTaskActionPayload,
  RequestTaskActionProcessDTO,
  UnderlyingAgreementVariationSaveRequestTaskActionPayload,
  UnderlyingAgreementVariationSubmitRequestTaskPayload,
} from 'cca-api';

@Injectable()
export class UnderlyingAgreementVariationApiService extends TaskApiService {
  private readonly pendingRequestService = inject(PendingRequestService);
  private readonly businessErrorService = inject(BusinessErrorService);

  save(
    payload: UnderlyingAgreementVariationSubmitRequestTaskPayload,
  ): Observable<UnderlyingAgreementVariationSubmitRequestTaskPayload> {
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
    return this.service
      .processRequestTaskAction({
        requestTaskActionType: 'UNDERLYING_AGREEMENT_VARIATION_SUBMIT_APPLICATION',
        requestTaskId: this.store.select(requestTaskQuery.selectRequestTaskId)(),
        requestTaskActionPayload: {
          payloadType: 'EMPTY_PAYLOAD',
        } as RequestTaskActionPayload,
      })
      .pipe(
        catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
          this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
        ),
        catchTaskReassignedBadRequest(() =>
          this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
        ),
      );
  }

  private createSaveAction(
    payload: UnderlyingAgreementVariationSubmitRequestTaskPayload,
  ): RequestTaskActionProcessDTO & {
    requestTaskActionPayload: UnderlyingAgreementVariationSaveRequestTaskActionPayload;
  } {
    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();
    const { underlyingAgreement, sectionsCompleted } = payload;

    return {
      requestTaskId,
      requestTaskActionType: 'UNDERLYING_AGREEMENT_VARIATION_SAVE_APPLICATION',
      requestTaskActionPayload: {
        payloadType: 'UNDERLYING_AGREEMENT_VARIATION_APPLICATION_SAVE_PAYLOAD',
        underlyingAgreement: {
          ...underlyingAgreement,
          underlyingAgreementTargetUnitDetails: {
            operatorName: underlyingAgreement.underlyingAgreementTargetUnitDetails.operatorName,
            operatorAddress: underlyingAgreement.underlyingAgreementTargetUnitDetails.operatorAddress,
            responsiblePersonDetails: underlyingAgreement.underlyingAgreementTargetUnitDetails.responsiblePersonDetails,
          },
        },
        sectionsCompleted,
      },
    };
  }
}
