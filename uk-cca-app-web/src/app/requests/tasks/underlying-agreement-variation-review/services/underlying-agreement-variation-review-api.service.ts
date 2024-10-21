import { inject, Injectable } from '@angular/core';

import { catchError, map, Observable, of, throwError } from 'rxjs';

import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchBadRequest, catchTaskReassignedBadRequest, ErrorCodes } from '@error/business-errors';
import { catchNotFoundRequest, ErrorCode } from '@error/not-found-error';
import { TaskApiService } from '@netz/common/forms';
import { PendingRequestService } from '@netz/common/services';
import { requestTaskQuery } from '@netz/common/store';
import { requestTaskReassignedError, taskNotFoundError } from '@shared/errors/request-task-error';

import {
  CcaDecisionNotification,
  CcaNotifyOperatorForDecisionRequestTaskActionPayload,
  RequestTaskActionProcessDTO,
  UnderlyingAgreementVariationReviewRequestTaskPayload,
  UnderlyingAgreementVariationSaveFacilityReviewGroupDecisionRequestTaskActionPayload,
  UnderlyingAgreementVariationSaveReviewDeterminationRequestTaskActionPayload,
  UnderlyingAgreementVariationSaveReviewGroupDecisionRequestTaskActionPayload,
  UnderlyingAgreementVariationSaveReviewRequestTaskActionPayload,
} from 'cca-api';

type DecisionPayloadType =
  | UnderlyingAgreementVariationSaveReviewGroupDecisionRequestTaskActionPayload
  | UnderlyingAgreementVariationSaveFacilityReviewGroupDecisionRequestTaskActionPayload;

@Injectable()
export class UnderlyingAgreementVariationReviewApiService extends TaskApiService {
  private readonly pendingRequestService = inject(PendingRequestService);
  private readonly businessErrorService = inject(BusinessErrorService);

  save(
    payload: UnderlyingAgreementVariationReviewRequestTaskPayload,
  ): Observable<UnderlyingAgreementVariationReviewRequestTaskPayload> {
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

  saveReviewDecision(
    payload: DecisionPayloadType,
    taskActionType:
      | 'UNDERLYING_AGREEMENT_VARIATION_SAVE_REVIEW_GROUP_DECISION'
      | 'UNDERLYING_AGREEMENT_VARIATION_SAVE_FACILITY_REVIEW_GROUP_DECISION',
  ) {
    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();

    const requestPayload: RequestTaskActionProcessDTO & {
      requestTaskActionPayload: DecisionPayloadType;
    } = {
      requestTaskId,
      requestTaskActionType: taskActionType,
      requestTaskActionPayload: payload,
    };

    return this.service
      .processRequestTaskAction(requestPayload)
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

  saveDetermination(payload: UnderlyingAgreementVariationSaveReviewDeterminationRequestTaskActionPayload) {
    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();

    const requestPayload: RequestTaskActionProcessDTO & {
      requestTaskActionPayload: UnderlyingAgreementVariationSaveReviewDeterminationRequestTaskActionPayload;
    } = {
      requestTaskId,
      requestTaskActionType: 'UNDERLYING_AGREEMENT_VARIATION_SAVE_REVIEW_DETERMINATION',
      requestTaskActionPayload: payload,
    };

    return this.service
      .processRequestTaskAction(requestPayload)
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

  notifyOperator(decisionNotification: CcaDecisionNotification): Observable<void> {
    return this.service
      .processRequestTaskAction({
        requestTaskActionType: 'UNDERLYING_AGREEMENT_VARIATION_NOTIFY_OPERATOR_FOR_DECISION',
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
    payload: UnderlyingAgreementVariationReviewRequestTaskPayload,
  ): RequestTaskActionProcessDTO & {
    requestTaskActionPayload: UnderlyingAgreementVariationSaveReviewRequestTaskActionPayload;
  } {
    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();
    const { underlyingAgreement, sectionsCompleted } = payload;

    return {
      requestTaskId,
      requestTaskActionType: 'UNDERLYING_AGREEMENT_VARIATION_SAVE_APPLICATION_REVIEW',
      requestTaskActionPayload: {
        payloadType: 'UNDERLYING_AGREEMENT_VARIATION_SAVE_APPLICATION_REVIEW_PAYLOAD',
        underlyingAgreement: underlyingAgreement,
        sectionsCompleted,
        reviewSectionsCompleted: payload.reviewSectionsCompleted,
      },
    };
  }
}
